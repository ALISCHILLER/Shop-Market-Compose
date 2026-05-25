package com.msa.eshop.ui.screen.simulate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import com.msa.eshop.data.Model.request.SimulateModelRequest
import com.msa.eshop.data.Model.response.SimulateModel
import com.msa.eshop.data.local.entity.OrderEntity
import com.msa.eshop.data.local.entity.PaymentMethodEntity
import com.msa.eshop.data.repository.SimulateRepository
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

data class SimulateUiState(
    val items: List<SimulateModel> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) {
    val isEmpty: Boolean
        get() = !isLoading && items.isEmpty() && errorMessage == null

    val cashTotal: Int
        get() = items.sumOf { it.priceByDiscountPercentAndTax_immediate }

    val checkTotal: Int
        get() = items.sumOf { it.priceByDiscountPercentAndTax_cheque }

    val receiptTotal: Int
        get() = items.sumOf { it.priceByDiscountPercentAndTax_Receipt }

    val canContinue: Boolean
        get() = items.isNotEmpty() && !isLoading
}

@HiltViewModel
class SimulateViewModel @Inject constructor(
    private val simulateRepository: SimulateRepository,
    private val navManager: NavManager
) : ViewModel() {

    private val _state = MutableStateFlow(GeneralStateModel(isLoading = true))
    val state: StateFlow<GeneralStateModel> = _state.asStateFlow()

    private val _uiState = MutableStateFlow(SimulateUiState())
    val uiState: StateFlow<SimulateUiState> = _uiState.asStateFlow()

    private val _simulate = MutableStateFlow<List<SimulateModel>>(emptyList())
    val simulat: StateFlow<List<SimulateModel>> = _simulate.asStateFlow()

    private val _order = MutableStateFlow<List<OrderEntity>>(emptyList())
    val order: StateFlow<List<OrderEntity>> = _order.asStateFlow()

    private var requestJob: Job? = null

    init {
        getOrderToSimulate()
    }

    fun getOrderToSimulate() {
        if (requestJob?.isActive == true) return

        requestJob = viewModelScope.launch {
            updateStateLoading(true)

            runCatching {
                simulateRepository.getAllOrder.first()
            }.onSuccess { orders ->
                _order.value = orders

                if (orders.isEmpty()) {
                    _simulate.value = emptyList()

                    _uiState.update {
                        it.copy(
                            items = emptyList(),
                            isLoading = false,
                            errorMessage = null
                        )
                    }

                    updateStateLoading(false)
                    return@onSuccess
                }

                val requests = orders.map { it.toSimulateModelRequest() }
                productRequest(requests)
            }.onFailure { throwable ->
                Timber.tag(TAG).e(throwable, "Load local orders failed")
                updateStateError("دریافت اقلام سبد خرید با خطا مواجه شد")
            }
        }
    }

    fun refresh() {
        requestJob?.cancel()
        requestJob = null
        getOrderToSimulate()
    }

    fun productRequest(simulateModel: List<SimulateModelRequest>) {
        if (simulateModel.isEmpty()) {
            updateStateLoading(false)
            return
        }

        makeRequest(
            scope = viewModelScope,
            request = {
                simulateRepository.SimulateModelRequest(simulateModel)
            },
            onSuccess = { response ->
                val items = response?.data.orEmpty()

                Timber.tag(TAG).d("Simulate success | count=${items.size}")

                _simulate.value = items

                _uiState.update {
                    it.copy(
                        items = items,
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

    fun savePayment(payment: PaymentMethodEntity) {
        viewModelScope.launch {
            updateStateLoading(true)

            runCatching {
                simulateRepository.insertTopayment(payment)
            }.onSuccess {
                updateStateLoading(false)
                navigateToOrderAddress()
            }.onFailure { throwable ->
                Timber.tag(TAG).e(throwable, "Save payment failed")
                updateStateError("ثبت روش پرداخت با خطا مواجه شد")
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun OrderEntity.toSimulateModelRequest(): SimulateModelRequest {
        return SimulateModelRequest(
            productCode = productCode,
            quantity = numberOrder
        )
    }

    private fun navigateToOrderAddress() {
        navManager.navigate(
            NavInfo(
                id = Route.OrderAddressScreen.route,
                navOption = NavOptions.Builder()
                    .setPopUpTo(
                        Route.SimulateScreen.route,
                        false
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
        private const val TAG = "SimulateViewModel"
    }
}