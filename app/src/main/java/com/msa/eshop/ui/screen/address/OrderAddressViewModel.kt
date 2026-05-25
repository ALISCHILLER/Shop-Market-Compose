package com.msa.eshop.ui.screen.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import com.msa.eshop.data.Model.response.OrderAddressModel
import com.msa.eshop.data.local.entity.OrderAddressEntity
import com.msa.eshop.data.repository.OrderAddressRepository
import com.msa.eshop.ui.navigation.NavInfo
import com.msa.eshop.ui.navigation.NavManager
import com.msa.eshop.ui.navigation.Route
import com.msa.eshop.utils.result.GeneralStateModel
import com.msa.eshop.utils.result.makeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

data class OrderAddressUiState(
    val addresses: List<OrderAddressModel> = emptyList(),
    val selectedAddressId: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val selectedAddress: OrderAddressModel?
        get() = addresses.firstOrNull { it.id == selectedAddressId }

    val canContinue: Boolean
        get() = selectedAddress != null && !isLoading
}

@HiltViewModel
class OrderAddressViewModel @Inject constructor(
    private val orderAddressRepository: OrderAddressRepository,
    private val navManager: NavManager
) : ViewModel() {

    private val _state = MutableStateFlow(GeneralStateModel())
    val state: StateFlow<GeneralStateModel> = _state.asStateFlow()

    private val _uiState = MutableStateFlow(OrderAddressUiState(isLoading = true))
    val uiState: StateFlow<OrderAddressUiState> = _uiState.asStateFlow()

    private val _orderAddress = MutableStateFlow<List<OrderAddressModel>>(emptyList())
    val orderAddress: StateFlow<List<OrderAddressModel>> = _orderAddress.asStateFlow()

    init {
        orderAddressRequest()
    }

    fun clearState() {
        _state.value = GeneralStateModel()
        _uiState.update {
            it.copy(errorMessage = null)
        }
    }

    fun selectAddress(address: OrderAddressModel) {
        _uiState.update {
            it.copy(
                selectedAddressId = address.id,
                errorMessage = null
            )
        }
    }

    fun confirmSelectedAddress() {
        val selectedAddress = _uiState.value.selectedAddress

        if (selectedAddress == null) {
            updateStateError("لطفاً آدرس را انتخاب کنید")
            return
        }

        saveSelectedAddress(selectedAddress)
    }

    fun OrderAddressRequest() {
        orderAddressRequest()
    }

    private fun orderAddressRequest() {
        makeRequest(
            scope = viewModelScope,
            request = {
                orderAddressRepository.OrderAddressModelRequest()
            },
            onSuccess = { response ->
                val addresses = response?.data.orEmpty()

                Timber.tag(TAG).d("Address loaded | count=${addresses.size}")

                _orderAddress.value = addresses

                _uiState.update {
                    it.copy(
                        addresses = addresses,
                        isLoading = false,
                        errorMessage = null
                    )
                }

                updateStateLoading(false)
            },
            updateStateLoading = ::updateStateLoading,
            updateStateError = ::updateStateError
        )
    }

    fun getOrderToInsertCart(
        address: String,
        orderAddress: OrderAddressModel
    ) {
        if (address.isBlank()) {
            updateStateError("لطفاً آدرس را انتخاب کنید")
            return
        }

        saveSelectedAddress(orderAddress)
    }

    private fun saveSelectedAddress(
        orderAddress: OrderAddressModel
    ) {
        viewModelScope.launch {
            updateStateLoading(true)

            runCatching {
                orderAddressRepository.insertOrderAddress(
                    OrderAddressEntity(
                        customerAddress = orderAddress.customerAddress,
                        customerPhone = orderAddress.customerPhone,
                        centerName = orderAddress.centerName,
                        customerMobile = orderAddress.customerMobile,
                        id = orderAddress.id
                    )
                )
            }.onSuccess {
                updateStateLoading(false)
                navigateToPaymentMethodScreen()
            }.onFailure { throwable ->
                Timber.tag(TAG).e(throwable, "Save selected address failed")
                updateStateError("ثبت آدرس انتخاب‌شده با خطا مواجه شد")
            }
        }
    }

    fun navigateToLocationRegistration() {
        navManager.navigate(
            NavInfo(
                id = Route.LocationRegistrationScreen.route,
                navOption = NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .build()
            )
        )
    }

    private fun navigateToPaymentMethodScreen() {
        navManager.navigate(
            NavInfo(
                id = Route.PaymentMethodScreen.route,
                navOption = NavOptions.Builder()
                    .setPopUpTo(
                        Route.OrderAddressScreen.route,
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
        private const val TAG = "OrderAddressVM"
    }
}