package com.msa.eshop.ui.screen.paymentMethod

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import com.msa.eshop.data.Model.request.InsertCartModelRequest
import com.msa.eshop.data.local.entity.OrderEntity
import com.msa.eshop.data.repository.PaymentMethodRepository
import com.msa.eshop.ui.navigation.NavInfo
import com.msa.eshop.ui.navigation.NavManager
import com.msa.eshop.ui.navigation.Route
import com.msa.eshop.utils.result.GeneralStateModel
import com.msa.eshop.utils.result.makeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class PaymentMethodViewModel @Inject constructor(
    private val paymentMethodRepository: PaymentMethodRepository,
    private val navManager: NavManager
) : ViewModel() {

    private val _state = MutableStateFlow(GeneralStateModel())
    val state: StateFlow<GeneralStateModel> = _state.asStateFlow()

    private val _uiState = MutableStateFlow(PaymentMethodUiState(isLoading = true))
    val uiState: StateFlow<PaymentMethodUiState> = _uiState.asStateFlow()

    private var observeJob: Job? = null
    private var submitJob: Job? = null

    init {
        observeLocalData()
    }

    fun getOrderAddress() {
        observeLocalData()
    }

    fun getAllPaymentMethod() {
        observeLocalData()
    }

    private fun observeLocalData() {
        if (observeJob?.isActive == true) return

        observeJob = viewModelScope.launch {
            updateStateLoading(true)

            runCatching {
                val addresses = paymentMethodRepository.getAllorderAddress.first()
                val paymentMethods = paymentMethodRepository.getAllPayment.first()
                val orders = paymentMethodRepository.getAllOrder.first()

                _uiState.update {
                    it.copy(
                        selectedAddress = addresses.firstOrNull(),
                        paymentMethod = paymentMethods.firstOrNull(),
                        orders = orders,
                        isLoading = false,
                        errorMessage = null
                    )
                }

                _state.update {
                    it.copy(
                        isLoading = false,
                        error = null
                    )
                }

                Timber.tag(TAG).d(
                    "Payment data loaded | addresses=${addresses.size}, paymentMethods=${paymentMethods.size}, orders=${orders.size}"
                )
            }.onFailure { throwable ->
                Timber.tag(TAG).e(throwable, "Load payment data failed")
                updateStateError("اطلاعات پرداخت دریافت نشد")
            }
        }
    }

    fun onPaymentSelected(option: String) {
        _uiState.update {
            it.copy(
                selectedPaymentTitle = option.extractPaymentTitle(),
                errorMessage = null
            )
        }
    }

    fun onReceiveSelected(option: String) {
        _uiState.update {
            it.copy(
                selectedReceiveTitle = option,
                errorMessage = null
            )
        }
    }

    fun submitPayment() {
        val currentState = _uiState.value

        if (currentState.isLoading) return

        val address = currentState.selectedAddress
        if (address == null) {
            updateStateError("آدرس سفارش مشخص نیست")
            return
        }

        val addressId = address.id
        if (addressId.isBlank()) {
            updateStateError("شناسه آدرس سفارش نامعتبر است")
            return
        }

        if (currentState.orders.isEmpty()) {
            updateStateError("سبد خرید خالی است")
            return
        }

        if (submitJob?.isActive == true) return

        updateStateLoading(true)

        submitJob = viewModelScope.launch {
            val paymentTermId = resolvePaymentTermId(currentState.selectedPaymentTitle)

            val requests = currentState.orders.map { order ->
                order.toInsertCartModelRequest(
                    addressId = addressId,
                    paymentTermId = paymentTermId
                )
            }

            insertCartRequest(requests)
        }
    }

    fun getOrderToSimulate(addressId: String) {
        val safeAddressId = addressId.trim()

        if (safeAddressId.isBlank()) {
            updateStateError("شناسه آدرس سفارش نامعتبر است")
            return
        }

        if (submitJob?.isActive == true) return

        updateStateLoading(true)

        submitJob = viewModelScope.launch {
            runCatching {
                paymentMethodRepository.getAllOrder.first()
            }.onSuccess { orders ->
                if (orders.isEmpty()) {
                    updateStateError("سبد خرید خالی است")
                    return@onSuccess
                }

                val paymentTermId = resolvePaymentTermId(_uiState.value.selectedPaymentTitle)

                val requests = orders.map { order ->
                    order.toInsertCartModelRequest(
                        addressId = safeAddressId,
                        paymentTermId = paymentTermId
                    )
                }

                insertCartRequest(requests)
            }.onFailure { throwable ->
                Timber.tag(TAG).e(throwable, "Load orders for payment failed")
                updateStateError("دریافت اقلام سبد خرید با خطا مواجه شد")
            }
        }
    }

    private fun OrderEntity.toInsertCartModelRequest(
        addressId: String,
        paymentTermId: String
    ): InsertCartModelRequest {
        return InsertCartModelRequest(
            productCode = productCode,
            quantity = numberOrder,
            customerAddressId = addressId,
            paymentTermId = paymentTermId
        )
    }

    private fun insertCartRequest(
        insertCartModelRequest: List<InsertCartModelRequest>
    ) {
        if (insertCartModelRequest.isEmpty()) {
            updateStateError("اقلامی برای ثبت سفارش وجود ندارد")
            return
        }

        makeRequest(
            scope = viewModelScope,
            request = {
                paymentMethodRepository.requestInsertCart(insertCartModelRequest)
            },
            onSuccess = { response ->
                val hasResult = response?.data != null

                if (!hasResult) {
                    updateStateError("ثبت سفارش انجام نشد")
                    return@makeRequest
                }

                viewModelScope.launch {
                    runCatching {
                        paymentMethodRepository.deleatOrder()
                    }.onFailure { throwable ->
                        Timber.tag(TAG).e(throwable, "Delete local orders failed")
                    }

                    updateStateLoading(false)
                    navigateToHome()
                }
            },
            updateStateLoading = ::updateStateLoading,
            updateStateError = ::updateStateError
        )
    }

    fun clearError() {
        _state.update {
            it.copy(error = null)
        }

        _uiState.update {
            it.copy(errorMessage = null)
        }
    }

    private fun resolvePaymentTermId(paymentTitle: String): String {
        /*
         * فعلاً Entity فقط قیمت‌ها را دارد و ID جداگانه برای نقدی/چک/عرفی ندارد.
         * اگر API بعداً paymentTermId جداگانه داد، اینجا map می‌شود.
         */
        return DEFAULT_PAYMENT_TERM_ID
    }

    private fun navigateToHome() {
        navManager.navigate(
            NavInfo(
                id = Route.HomeScreen.route,
                navOption = NavOptions.Builder()
                    .setPopUpTo(
                        Route.PaymentMethodScreen.route,
                        true
                    )
                    .setLaunchSingleTop(true)
                    .build()
            )
        )
    }

    private fun updateStateLoading(isLoading: Boolean) {
        _state.update {
            it.copy(
                isLoading = isLoading,
                error = null
            )
        }

        _uiState.update {
            it.copy(
                isLoading = isLoading,
                errorMessage = null
            )
        }
    }

    private fun updateStateError(errorMessage: String?) {
        Timber.tag(TAG).e(errorMessage.orEmpty())

        _state.update {
            it.copy(
                isLoading = false,
                error = errorMessage
            )
        }

        _uiState.update {
            it.copy(
                isLoading = false,
                errorMessage = errorMessage
            )
        }
    }

    companion object {
        private const val TAG = "PaymentMethodVM"
        private const val DEFAULT_PAYMENT_TERM_ID = "16ccab60-279b-410a-90d1-b2673d5d1dd1"
    }
}