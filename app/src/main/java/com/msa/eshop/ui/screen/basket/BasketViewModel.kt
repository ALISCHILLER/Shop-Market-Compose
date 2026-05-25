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
import kotlinx.coroutines.launch

@HiltViewModel
class BasketViewModel @Inject constructor(
    private val basketRepository: BasketRepository,
    private val navManager: NavManager
) : ViewModel() {

    private val _allOrder = MutableStateFlow<List<OrderEntity>>(emptyList())
    val allOrder: StateFlow<List<OrderEntity>> = _allOrder

    private var orderJob: Job? = null

    init {
        observeOrders()
    }

    private fun observeOrders() {
        if (orderJob != null) return

        orderJob = viewModelScope.launch {
            basketRepository.getAllOrder.collect { orders ->
                _allOrder.value = orders
            }
        }
    }

    fun deleteOrder(orderId: String) {
        viewModelScope.launch {
            basketRepository.deleteOrder(orderId)
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
        }
    }

    fun navigateToSimulate() {
        navManager.navigate(
            NavInfo(
                id = Route.SimulateScreen.route,
                navOption = NavOptions.Builder()
                    .setPopUpTo(Route.BasketScreen.route, false)
                    .build()
            )
        )
    }
}