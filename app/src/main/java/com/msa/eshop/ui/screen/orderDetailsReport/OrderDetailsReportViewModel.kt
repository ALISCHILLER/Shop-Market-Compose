package com.msa.eshop.ui.screen.orderDetailsReport

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msa.eshop.data.Model.response.ReportCartDetailsModel
import com.msa.eshop.data.repository.ReportCartDetailsRepository
import com.msa.eshop.utils.result.GeneralStateModel
import com.msa.eshop.utils.result.makeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber

data class OrderDetailsReportUiState(
    val cartCode: Int = 0,
    val items: List<ReportCartDetailsModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val isEmpty: Boolean
        get() = !isLoading && items.isEmpty() && errorMessage == null
}

@HiltViewModel
class OrderDetailsReportViewModel @Inject constructor(
    private val reportCartDetailsRepository: ReportCartDetailsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GeneralStateModel())
    val state: StateFlow<GeneralStateModel> = _state.asStateFlow()

    private val _uiState = MutableStateFlow(OrderDetailsReportUiState())
    val uiState: StateFlow<OrderDetailsReportUiState> = _uiState.asStateFlow()

    private val _reportCartDetails = MutableStateFlow<List<ReportCartDetailsModel>>(emptyList())
    val reportCartDetails: StateFlow<List<ReportCartDetailsModel>> = _reportCartDetails.asStateFlow()

    private var lastRequestedCartCode: Int? = null

    fun reportCartDetailsRequest(cartCode: Int) {
        if (cartCode <= 0) {
            updateStateError("شماره سفارش نامعتبر است")
            return
        }

        val currentState = _uiState.value
        if (
            lastRequestedCartCode == cartCode &&
            currentState.items.isNotEmpty() &&
            !currentState.isLoading
        ) {
            return
        }

        lastRequestedCartCode = cartCode

        makeRequest(
            scope = viewModelScope,
            request = {
                reportCartDetailsRepository.reportCartDetails(cartCode)
            },
            onSuccess = { response ->
                val items = response?.data.orEmpty()

                Timber.tag(TAG).d(
                    "Order details loaded | cartCode=$cartCode | count=${items.size}"
                )

                _reportCartDetails.value = items

                _uiState.update {
                    it.copy(
                        cartCode = cartCode,
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

    fun clearError() {
        _state.update {
            it.copy(error = null)
        }

        _uiState.update {
            it.copy(errorMessage = null)
        }
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
        private const val TAG = "OrderDetailsVM"
    }
}