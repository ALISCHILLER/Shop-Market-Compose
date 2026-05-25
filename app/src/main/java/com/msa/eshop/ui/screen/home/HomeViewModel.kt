package com.msa.eshop.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msa.eshop.data.Model.response.DiscountResultModel
import com.msa.eshop.data.local.entity.OrderEntity
import com.msa.eshop.data.local.entity.ProductGroupEntity
import com.msa.eshop.data.local.entity.ProductModelEntity
import com.msa.eshop.data.repository.HomeRepository
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository
) : ViewModel() {

    private val converter = Convert_Number()

    private val _state = MutableStateFlow(GeneralStateModel(isLoading = true))
    val state: StateFlow<GeneralStateModel> = _state

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    private var productJob: Job? = null
    private var productGroupJob: Job? = null
    private var orderJob: Job? = null

    private var observersStarted = false
    private var firstLoadRequested = false

    private var currentGroupCode: Int = ALL_PRODUCTS_GROUP_CODE
    private var currentSearchQuery: String = ""

    private val allProductsGroup = ProductGroupEntity(
        productCategoryCode = ALL_PRODUCTS_GROUP_CODE,
        productCategoryName = "همه",
        productCategoryImage = null,
        productCategoryImageUnselect = null
    )

    init {
        startObserversIfNeeded()
        productCheck()
    }

    fun productCheck() {
        if (firstLoadRequested) return
        firstLoadRequested = true
        syncCatalog(force = false)
    }

    fun refresh() {
        syncCatalog(force = true)
    }

    fun onGroupSelected(productCategoryCode: Int) {
        if (currentGroupCode == productCategoryCode) return

        currentGroupCode = productCategoryCode

        _uiState.update {
            it.copy(
                selectedGroupCode = productCategoryCode,
                isLoading = true,
                errorMessage = null
            )
        }

        observeProducts()
    }

    fun onSearchChanged(search: String) {
        val normalizedSearch = converter.PersianToEnglish(search.trim())

        currentSearchQuery = normalizedSearch

        _uiState.update {
            it.copy(
                searchQuery = search,
                isLoading = true,
                errorMessage = null
            )
        }

        observeProducts()
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

        viewModelScope.launch {
            if (totalValue > 0) {
                homeRepository.insertOrder(
                    createOrderEntity(
                        productModelEntity = productModelEntity,
                        totalValue = totalValue,
                        value1 = value1,
                        value2 = value2
                    )
                )
            } else {
                homeRepository.deleteOrder(productModelEntity.id)
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

        saveProductOrder(
            productModelEntity = productModelEntity,
            value1 = value1,
            value2 = value2
        )

        return calculateSalePrice(
            totalValue = totalValue,
            price = productModelEntity.price
        )
    }

    fun discountRequest(productCode: String) {
        makeRequest(
            scope = viewModelScope,
            request = {
                homeRepository.requestDiscount(productCode)
            },
            onSuccess = { response ->
                val discounts: List<DiscountResultModel> = response?.data.orEmpty()

                _uiState.update {
                    it.copy(
                        discounts = discounts,
                        isDiscountLoading = false,
                        errorMessage = null
                    )
                }
            },
            updateStateLoading = { isLoading ->
                _uiState.update {
                    it.copy(isDiscountLoading = isLoading)
                }
            },
            updateStateError = { errorMessage ->
                _uiState.update {
                    it.copy(
                        isDiscountLoading = false,
                        errorMessage = errorMessage
                    )
                }
            }
        )
    }

    private fun syncCatalog(force: Boolean) {
        viewModelScope.launch {
            startObserversIfNeeded()

            _uiState.update {
                it.copy(
                    isLoading = it.products.isEmpty(),
                    isRefreshing = it.products.isNotEmpty(),
                    errorMessage = null
                )
            }

            val productCount = homeRepository.getProductCount()
            val productGroupCount = homeRepository.getProductGroupCount()

            if (force || productCount == 0) {
                requestProducts()
            }

            if (force || productGroupCount == 0) {
                requestProductGroups()
            }

            requestBanners()
        }
    }

    private fun requestProducts() {
        makeRequest(
            scope = viewModelScope,
            request = {
                homeRepository.productRequest()
            },
            onSuccess = { response ->
                viewModelScope.launch {
                    val products: List<ProductModelEntity> = response?.data.orEmpty()
                    if (products.isNotEmpty()) {
                        homeRepository.insertProduct(products)
                    }

                    updateStateLoading(false)
                }
            },
            updateStateLoading = ::updateStateLoading,
            updateStateError = ::updateStateError
        )
    }

    private fun requestProductGroups() {
        makeRequest(
            scope = viewModelScope,
            request = {
                homeRepository.productGroupRequest()
            },
            onSuccess = { response ->
                viewModelScope.launch {
                    val groups: List<ProductGroupEntity> = response?.data.orEmpty()
                    if (groups.isNotEmpty()) {
                        homeRepository.insertProductGroup(groups)
                    }

                    updateStateLoading(false)
                }
            },
            updateStateLoading = ::updateStateLoading,
            updateStateError = ::updateStateError
        )
    }

    private fun requestBanners() {
        makeRequest(
            scope = viewModelScope,
            request = {
                homeRepository.requestBanner()
            },
            onSuccess = { response ->
                val banners = response?.data.orEmpty()

                _uiState.update {
                    it.copy(
                        banners = banners,
                        isRefreshing = false,
                        errorMessage = null
                    )
                }

                updateStateLoading(false)
            },
            updateStateLoading = { isLoading ->
                _uiState.update {
                    it.copy(
                        isRefreshing = isLoading && it.products.isNotEmpty(),
                        isLoading = isLoading && it.products.isEmpty()
                    )
                }
            },
            updateStateError = ::updateStateError
        )
    }

    private fun startObserversIfNeeded() {
        if (observersStarted) return
        observersStarted = true

        observeProducts()
        observeProductGroups()
        observeOrders()
    }

    private fun observeProducts() {
        productJob?.cancel()

        productJob = viewModelScope.launch {
            homeRepository.observeProducts(
                groupCode = currentGroupCode,
                searchQuery = currentSearchQuery
            ).collect { products ->
                _uiState.update {
                    it.copy(
                        products = products,
                        selectedGroupCode = currentGroupCode,
                        isLoading = false,
                        isRefreshing = false
                    )
                }
            }
        }
    }

    private fun observeProductGroups() {
        productGroupJob?.cancel()

        productGroupJob = viewModelScope.launch {
            homeRepository.getAllProductGroup.collect { productGroups ->
                val uiGroups = listOf(allProductsGroup) + productGroups
                    .filter { it.productCategoryCode != ALL_PRODUCTS_GROUP_CODE }
                    .filter { it.productCategoryCode != 0 }
                    .distinctBy { it.productCategoryCode }

                _uiState.update {
                    it.copy(productGroups = uiGroups)
                }
            }
        }
    }

    private fun observeOrders() {
        orderJob?.cancel()

        orderJob = viewModelScope.launch {
            homeRepository.getAllOrder.collect { orders ->
                _uiState.update {
                    it.copy(orders = orders)
                }
            }
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
                isLoading = isLoading && it.products.isEmpty(),
                isRefreshing = isLoading && it.products.isNotEmpty(),
                errorMessage = null
            )
        }
    }

    private fun updateStateError(errorMessage: String?) {
        Timber.tag("HomeViewModel").e(errorMessage.orEmpty())

        _state.update {
            it.copy(
                isLoading = false,
                error = errorMessage
            )
        }

        _uiState.update {
            it.copy(
                isLoading = false,
                isRefreshing = false,
                errorMessage = errorMessage
            )
        }
    }
}