package com.msa.eshop.data.repository

import com.msa.eshop.data.local.dao.OrderDao
import com.msa.eshop.data.local.entity.OrderEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class MainRepository @Inject constructor(
    private val orderDao: OrderDao
) {

    val getAllOrder: Flow<List<OrderEntity>> = orderDao.getAll()
}