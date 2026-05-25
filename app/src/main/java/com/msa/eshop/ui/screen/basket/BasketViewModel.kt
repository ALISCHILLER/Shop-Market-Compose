package com.msa.eshop.ui.screen.basket

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import com.msa.eshop.data.local.entity.OrderEntity
import com.msa.eshop.data.repository.BasketRepository
import com.msa.eshop.ui.navigation.NavInfo
import com.msa.eshop.ui.navigation.NavManager
import com.msa.eshop.ui.navigation.Route
import com.msa.eshop.utils.calculateSalePrice
import com.msa.eshop.utils.calculateTotalValue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

data class BasketUiState(
    val orders: List<OrderEntity> = emptyList(),
    val totalPrice: Long = 0L,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) {
    val isEmpty: Boolean
        get() = !isLoading && orders.isEmpty()

    val canSubmit: Boolean
        get() = orders.isNotEmpty() && !isLoading
}

@HiltViewModel
class BasketViewModel @Inject constructor(
    private val basketRepository: BasketRepository,
    private val navManager: NavManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(BasketUiState())
    val uiState: StateFlow<BasketUiState> = _uiState.asStateFlow()

    private val _allOrder = MutableStateFlow<List<OrderEntity>>(emptyList())
    val allOrder: StateFlow<List<OrderEntity>> = _allOrder.asStateFlow()

    private var orderJob: Job? = null

    init {
        observeOrders()
    }

    private fun observeOrders() {
        if (orderJob?.isActive == true) return

        orderJob = viewModelScope.launch {
            basketRepository.getAllOrder.collect { orders ->
                val totalPrice = orders.sumOf { order ->
                    order.numberOrder.toLong() * order.price.toLong()
                }

                _allOrder.value = orders

                _uiState.value = BasketUiState(
                    orders = orders,
                    totalPrice = totalPrice,
                    isLoading = false,
                    errorMessage = null
                )
            }
        }
    }

    fun deleteOrder(orderId: String) {
        viewModelScope.launch {
            runCatching {
                basketRepository.deleteOrder(orderId)
            }.onFailure { throwable ->
                Timber.tag(TAG).e(throwable, "Delete order failed")
                _uiState.value = _uiState.value.copy(
                    errorMessage = "حذف کالا از سبد خرید با خطا مواجه شد"
                )
            }
        }
    }

    fun calculateTotalPrice(
        value1: Int,
        value2: Int,
        orderEntity: OrderEntity
    ): Float {
        val totalValue = calculateTotalValue(
            value1 = value1,
            value2 = value2,
            convertFactor2 = orderEntity.convertFactor2
        )

        return calculateSalePrice(
            totalValue = totalValue,
            price = orderEntity.price
        )
    }

    fun updateOrderQuantity(
        orderEntity: OrderEntity,
        value1: Int,
        value2: Int
    ) {
        val totalValue = calculateTotalValue(
            value1 = value1,
            value2 = value2,
            convertFactor2 = orderEntity.convertFactor2
        )

        viewModelScope.launch {
            runCatching {
                if (totalValue > 0) {
                    basketRepository.insertOrder(
                        orderEntity.copy(
                            numberOrder = totalValue,
                            numberOrder1 = value1.coerceAtLeast(0),
                            numberOrder2 = value2.coerceAtLeast(0)
                        )
                    )
                } else {
                    basketRepository.deleteOrder(orderEntity.id)
                }
            }.onFailure { throwable ->
                Timber.tag(TAG).e(throwable, "Update order quantity failed")
                _uiState.value = _uiState.value.copy(
                    errorMessage = "به‌روزرسانی تعداد کالا با خطا مواجه شد"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun navigateToSimulate() {
        if (!_uiState.value.canSubmit) return

        navManager.navigate(
            NavInfo(
                id = Route.SimulateScreen.route,
                navOption = NavOptions.Builder()
                    .setPopUpTo(
                        Route.BasketScreen.route,
                        false
                    )
                    .setLaunchSingleTop(true)
                    .build()
            )
        )
    }

    companion object {
        private const val TAG = "BasketViewModel"
    }
}