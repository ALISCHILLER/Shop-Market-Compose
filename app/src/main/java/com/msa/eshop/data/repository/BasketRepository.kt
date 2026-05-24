package com.msa.eshop.data.repository

import com.msa.eshop.data.local.dao.OrderDao
import com.msa.eshop.data.local.entity.OrderEntity
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class BasketRepository @Inject constructor(
    private val orderDao: OrderDao
) {

    val getAllOrder: Flow<List<OrderEntity>> = orderDao.getAll()

    suspend fun insertOrder(orderEntity: OrderEntity) {
        withContext(Dispatchers.IO) {
            orderDao.insert(orderEntity)
        }
    }

    suspend fun deleteOrder(orderId: String) {
        withContext(Dispatchers.IO) {
            orderDao.deleteOrder(orderId)
        }
    }

    suspend fun deleteAllOrders() {
        withContext(Dispatchers.IO) {
            orderDao.delete()
        }
    }
}