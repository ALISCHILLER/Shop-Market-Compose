package com.msa.eshop.ui.screen.orderStatusReport

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import com.msa.eshop.data.Model.request.ReportHistoryCustomerModelRequest
import com.msa.eshop.data.Model.response.ReportHistoryCustomerModel
import com.msa.eshop.data.repository.OrderStatusReportRepository
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

data class OrderStatusReportUiState(
    val orders: List<ReportHistoryCustomerModel> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) {
    val isEmpty: Boolean
        get() = !isLoading && orders.isEmpty() && errorMessage == null
}

@HiltViewModel
class OrderStatusReportViewModel @Inject constructor(
    private val navManager: NavManager,
    private val orderStatusReportRepository: OrderStatusReportRepository
) : ViewModel() {

    private val _orderStatusReport = MutableStateFlow<List<ReportHistoryCustomerModel>>(emptyList())
    val orderStatusReport: StateFlow<List<ReportHistoryCustomerModel>> = _orderStatusReport.asStateFlow()

    private val _state = MutableStateFlow(GeneralStateModel(isLoading = true))
    val state: StateFlow<GeneralStateModel> = _state.asStateFlow()

    private val _uiState = MutableStateFlow(OrderStatusReportUiState())
    val uiState: StateFlow<OrderStatusReportUiState> = _uiState.asStateFlow()

    private var hasRequested = false

    init {
        loadReports()
    }

    fun loadReports(force: Boolean = false) {
        if (hasRequested && !force) return
        hasRequested = true

        viewModelScope.launch {
            updateStateLoading(true)

            runCatching {
                orderStatusReportRepository.getUser.first()
            }.onSuccess { user ->
                val request = ReportHistoryCustomerModelRequest(
                    customerId = user.id,
                    fromDate = "",
                    endDate = ""
                )

                reportHistoryOrderRequest(request)
            }.onFailure { throwable ->
                hasRequested = false

                Timber.tag(TAG).e(throwable, "Load user for report failed")
                updateStateError("اطلاعات کاربر برای دریافت گزارش پیدا نشد")
            }
        }
    }

    fun refresh() {
        loadReports(force = true)
    }

    private fun reportHistoryOrderRequest(
        reportHistoryOrder: ReportHistoryCustomerModelRequest
    ) {
        makeRequest(
            scope = viewModelScope,
            request = {
                orderStatusReportRepository.reportHistoryOrder(reportHistoryOrder)
            },
            onSuccess = { response ->
                val orders = response?.data.orEmpty()

                Timber.tag(TAG).d("Order history loaded | count=${orders.size}")

                _orderStatusReport.value = orders

                _uiState.update {
                    it.copy(
                        orders = orders,
                        isLoading = false,
                        errorMessage = null
                    )
                }

                updateStateLoading(false)
            },
            updateStateLoading = ::updateStateLoading,
            updateStateError = { errorMessage ->
                hasRequested = false
                updateStateError(errorMessage)
            }
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

    fun navigateOrderDetailsReport(card: Int) {
        if (card <= 0) return

        navManager.navigate(
            NavInfo(
                id = "${Route.OrderDetailsReportScreen.route}/$card",
                navOption = NavOptions.Builder()
                    .setPopUpTo(
                        Route.OrderStatusReportScreen.route,
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
        private const val TAG = "OrderStatusVM"
    }
}