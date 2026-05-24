package com.msa.eshop.ui.screen.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msa.eshop.data.Model.response.BannerModel
import com.msa.eshop.data.Model.response.DiscountResultModel
import com.msa.eshop.data.local.entity.OrderEntity
import com.msa.eshop.data.local.entity.ProductGroupEntity
import com.msa.eshop.data.local.entity.ProductModelEntity
import com.msa.eshop.data.local.entity.UserModelEntity
import com.msa.eshop.data.repository.HomeRepository
import com.msa.eshop.ui.navigation.NavManager
import com.msa.eshop.utils.Convert_Number
import com.msa.eshop.utils.calculateSalePrice
import com.msa.eshop.utils.calculateTotalValue
import com.msa.eshop.utils.createOrderEntity
import com.msa.eshop.utils.result.GeneralStateModel
import com.msa.eshop.utils.result.makeRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val navManager: NavManager,
    private val homeRepository: HomeRepository
) : ViewModel() {

    companion object {
        private const val ALL_PRODUCTS_GROUP_CODE = 99
    }

    private val _state = MutableStateFlow(GeneralStateModel())
    val state: StateFlow<GeneralStateModel> = _state

    private val _allProduct = MutableStateFlow<List<ProductModelEntity>>(emptyList())
    val allProduct: StateFlow<List<ProductModelEntity>> = _allProduct

    private val _allProductGroup = MutableStateFlow<List<ProductGroupEntity>>(emptyList())
    val allProductGroup: StateFlow<List<ProductGroupEntity>> = _allProductGroup

    private val _allOrder = MutableStateFlow<List<OrderEntity>>(emptyList())
    val allOrder: StateFlow<List<OrderEntity>> = _allOrder

    private val _user = MutableStateFlow<UserModelEntity?>(null)
    val user: StateFlow<UserModelEntity?> = _user

    private val _banner = MutableStateFlow<List<BannerModel>>(emptyList())
    val banner: StateFlow<List<BannerModel>> = _banner

    private val _discount = MutableStateFlow<List<DiscountResultModel>>(emptyList())
    val discount: StateFlow<List<DiscountResultModel>> = _discount

    private var productJob: Job? = null
    private var productGroupJob: Job? = null
    private var orderJob: Job? = null

    fun productCheck() {
        observeAllProducts()
        observeAllProductGroups()
        observeAllOrders()

        val productCount = homeRepository.getProductCount()

        if (productCount == 0) {
            productRequest()
        }

        productGroupRequest()
        Bannerrequest()
    }

    fun refresh() {
        observeAllProducts()
        observeAllProductGroups()
        observeAllOrders()

        productRequest()
        productGroupRequest()
        Bannerrequest()
    }

    fun productRequest() {
        makeRequest(
            scope = viewModelScope,
            request = {
                homeRepository.productRequest()
            },
            onSuccess = { response ->
                viewModelScope.launch {
                    response?.data?.let { products ->
                        homeRepository.insertProduct(products)
                    }
                    updateStateLoading(false)
                }
            },
            updateStateLoading = { isLoading ->
                updateStateLoading(isLoading)
            },
            updateStateError = { errorMessage ->
                updateStateError(errorMessage)
            }
        )
    }

    fun productGroupRequest() {
        makeRequest(
            scope = viewModelScope,
            request = {
                homeRepository.productGroupRequest()
            },
            onSuccess = { response ->
                viewModelScope.launch {
                    response?.data?.let { productGroups ->
                        homeRepository.insertProductGroup(productGroups)
                    }
                    updateStateLoading(false)
                }
            },
            updateStateLoading = { isLoading ->
                updateStateLoading(isLoading)
            },
            updateStateError = { errorMessage ->
                updateStateError(errorMessage)
            }
        )
    }

    fun Bannerrequest() {
        makeRequest(
            scope = viewModelScope,
            request = {
                homeRepository.requestBanner()
            },
            onSuccess = { response ->
                response?.data?.let { banners ->
                    Timber.tag("HomeViewModel").d("Bannerrequest SUCCESS: $banners")
                    _banner.value = banners
                }
                updateStateLoading(false)
            },
            updateStateLoading = { isLoading ->
                updateStateLoading(isLoading)
            },
            updateStateError = { errorMessage ->
                updateStateError(errorMessage)
            }
        )
    }

    fun discountRequest(productCode: String) {
        makeRequest(
            scope = viewModelScope,
            request = {
                homeRepository.requestDiscount(productCode)
            },
            onSuccess = { response ->
                response.data?.let { discounts ->
                    Timber.tag("HomeViewModel").d("discountRequest SUCCESS: $discounts")
                    _discount.value = discounts
                }
                updateStateLoading(false)
            },
            updateStateLoading = { isLoading ->
                updateStateLoading(isLoading)
            },
            updateStateError = { errorMessage ->
                updateStateError(errorMessage)
            }
        )
    }

    fun getProduct(productCategoryCode: Int) {
        productJob?.cancel()

        productJob = viewModelScope.launch {
            val productFlow = if (productCategoryCode == ALL_PRODUCTS_GROUP_CODE) {
                homeRepository.getAllProduct
            } else {
                homeRepository.getProduct(productCategoryCode)
            }

            productFlow.collect { products ->
                _allProduct.value = products
                updateStateLoading(false)
            }
        }
    }

    fun getAllOrder() {
        observeAllOrders()
    }

    fun searchProduct(search: String) {
        val converter = Convert_Number()
        val query = converter.PersianToEnglish(search.trim())

        productJob?.cancel()

        productJob = viewModelScope.launch {
            val productFlow = if (query.isBlank()) {
                homeRepository.getAllProduct
            } else {
                homeRepository.searchProduct(query)
            }

            productFlow.collect { products ->
                _allProduct.value = products
                updateStateLoading(false)
            }
        }
    }

    fun calculateTotalPriceAndHandleOrder(
        value1: Int,
        value2: Int,
        productModelEntity: ProductModelEntity
    ): Float {
        val totalValue = calculateTotalValue(
            value1 = value1,
            value2 = value2,
            convertFactor2 = productModelEntity.convertFactor2
        )

        updateOrderInDatabase(
            productModelEntity = productModelEntity,
            totalValue = totalValue,
            value1 = value1,
            value2 = value2
        )

        return calculateSalePrice(
            totalValue = totalValue,
            price = productModelEntity.price
        )
    }

    private fun observeAllProducts() {
        productJob?.cancel()

        productJob = viewModelScope.launch {
            homeRepository.getAllProduct.collect { products ->
                _allProduct.value = products
                updateStateLoading(false)
            }
        }
    }

    private fun observeAllProductGroups() {
        productGroupJob?.cancel()

        productGroupJob = viewModelScope.launch {
            homeRepository.getAllProductGroup.collect { productGroups ->
                Log.e("TAG", "getAllProductGroup: $productGroups")
                _allProductGroup.value = productGroups
                updateStateLoading(false)
            }
        }
    }

    private fun observeAllOrders() {
        orderJob?.cancel()

        orderJob = viewModelScope.launch {
            homeRepository.getAllOrder.collect { orders ->
                _allOrder.value = orders
            }
        }
    }

    private fun updateOrderInDatabase(
        productModelEntity: ProductModelEntity,
        totalValue: Int,
        value1: Int,
        value2: Int
    ) {
        viewModelScope.launch {
            if (totalValue > 0) {
                insertOrder(
                    productModelEntity = productModelEntity,
                    totalValue = totalValue,
                    value1 = value1,
                    value2 = value2
                )
            } else {
                homeRepository.deleteOrder(productModelEntity.id)
            }
        }
    }

    private suspend fun insertOrder(
        productModelEntity: ProductModelEntity,
        totalValue: Int,
        value1: Int,
        value2: Int
    ) {
        homeRepository.insertOrder(
            createOrderEntity(
                productModelEntity = productModelEntity,
                totalValue = totalValue,
                value1 = value1,
                value2 = value2
            )
        )
    }

    private fun updateStateLoading(isLoading: Boolean) {
        _state.value = _state.value.copy(
            isLoading = isLoading,
            error = null
        )
    }

    private fun updateStateError(errorMessage: String?) {
        _state.value = _state.value.copy(
            isLoading = false,
            error = errorMessage
        )
    }
}