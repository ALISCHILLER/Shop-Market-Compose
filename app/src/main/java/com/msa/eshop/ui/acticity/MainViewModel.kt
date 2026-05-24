package com.msa.eshop.ui.acticity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msa.eshop.data.local.entity.OrderEntity
import com.msa.eshop.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val _allOrder = MutableStateFlow<List<OrderEntity>>(emptyList())
    val allOrder: StateFlow<List<OrderEntity>> = _allOrder

    private var orderJob: Job? = null

    init {
        observeOrders()
    }

    fun getAllOrder() {
        observeOrders()
    }

    private fun observeOrders() {
        if (orderJob != null) return

        orderJob = viewModelScope.launch {
            mainRepository.getAllOrder.collect { orders ->
                _allOrder.value = orders
            }
        }
    }
}