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

    private val converter = Convert_Number()

    private val _state = MutableStateFlow(GeneralStateModel())
    val state: StateFlow<GeneralStateModel> = _state

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

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

    private var hasStartedObservers = false

    private var currentGroupCode: Int = ALL_PRODUCTS_GROUP_CODE
    private var currentSearchQuery: String = ""

    private val allProductsGroup = ProductGroupEntity(
        productCategoryCode = ALL_PRODUCTS_GROUP_CODE,
        productCategoryName = "همه",
        productCategoryImage = null,
        productCategoryImageUnselect = null
    )

    fun productCheck() {
        startObserversIfNeeded()

        val productCount = homeRepository.getProductCount()

        if (productCount == 0) {
            productRequest()
        }

        productGroupRequest()
        Bannerrequest()
    }

    fun refresh() {
        startObserversIfNeeded()

        productRequest()
        productGroupRequest()
        Bannerrequest()
    }

    fun onGroupSelected(productCategoryCode: Int) {
        currentGroupCode = productCategoryCode

        _uiState.value = _uiState.value.copy(
            selectedGroupCode = productCategoryCode
        )

        observeProducts()
    }

    fun onSearchChanged(search: String) {
        currentSearchQuery = converter.PersianToEnglish(search.trim())

        _uiState.value = _uiState.value.copy(
            searchQuery = search
        )

        observeProducts()
    }

    fun getProduct(productCategoryCode: Int) {
        onGroupSelected(productCategoryCode)
    }

    fun searchProduct(search: String) {
        onSearchChanged(search)
    }

    fun getAllOrder() {
        observeAllOrders()
    }

    fun saveProductOrder(
        productModelEntity: ProductModelEntity,
        value1: Int,
        value2: Int
    ) {
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

                    _uiState.value = _uiState.value.copy(
                        banners = banners
                    )
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
                response?.data?.let { discounts ->
                    Timber.tag("HomeViewModel").d("discountRequest SUCCESS: $discounts")

                    _discount.value = discounts

                    _uiState.value = _uiState.value.copy(
                        discounts = discounts,
                        isDiscountLoading = false
                    )
                }
            },
            updateStateLoading = { isLoading ->
                _uiState.value = _uiState.value.copy(
                    isDiscountLoading = isLoading
                )
            },
            updateStateError = { errorMessage ->
                _uiState.value = _uiState.value.copy(
                    isDiscountLoading = false,
                    errorMessage = errorMessage
                )
            }
        )
    }

    private fun startObserversIfNeeded() {
        if (hasStartedObservers) return

        hasStartedObservers = true

        observeProducts()
        observeAllProductGroups()
        observeAllOrders()
    }

    private fun observeProducts() {
        productJob?.cancel()

        productJob = viewModelScope.launch {
            homeRepository.observeProducts(
                groupCode = currentGroupCode,
                searchQuery = currentSearchQuery
            ).collect { products ->
                _allProduct.value = products

                _uiState.value = _uiState.value.copy(
                    products = products,
                    selectedGroupCode = currentGroupCode
                )

                updateStateLoading(false)
            }
        }
    }

    private fun observeAllProductGroups() {
        productGroupJob?.cancel()

        productGroupJob = viewModelScope.launch {
            homeRepository.getAllProductGroup.collect { productGroups ->
                Log.e("HomeViewModel", "getAllProductGroup: $productGroups")

                val uiGroups = listOf(allProductsGroup) + productGroups
                    .filter { it.productCategoryCode != ALL_PRODUCTS_GROUP_CODE }
                    .filter { it.productCategoryCode != 0 }
                    .distinctBy { it.productCategoryCode }

                _allProductGroup.value = productGroups

                _uiState.value = _uiState.value.copy(
                    productGroups = uiGroups
                )

                updateStateLoading(false)
            }
        }
    }

    private fun observeAllOrders() {
        orderJob?.cancel()

        orderJob = viewModelScope.launch {
            homeRepository.getAllOrder.collect { orders ->
                _allOrder.value = orders

                _uiState.value = _uiState.value.copy(
                    orders = orders
                )
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

        _uiState.value = _uiState.value.copy(
            isLoading = isLoading,
            errorMessage = null
        )
    }

    private fun updateStateError(errorMessage: String?) {
        _state.value = _state.value.copy(
            isLoading = false,
            error = errorMessage
        )

        _uiState.value = _uiState.value.copy(
            isLoading = false,
            errorMessage = errorMessage
        )
    }
}