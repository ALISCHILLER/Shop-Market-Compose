package com.msa.eshop.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msa.eshop.data.Model.response.DiscountResultModel
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

private data class HomeFilterState(
    val groupCode: Int = ALL_PRODUCTS_GROUP_CODE,
    val searchQuery: String = ""
)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository
) : ViewModel() {

    private val converter = Convert_Number()

    private val _state = MutableStateFlow(GeneralStateModel(isLoading = true))
    val state: StateFlow<GeneralStateModel> = _state.asStateFlow()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val filterState = MutableStateFlow(HomeFilterState())

    private var productJob: Job? = null
    private var productGroupJob: Job? = null
    private var orderJob: Job? = null

    private var observersStarted = false
    private var firstLoadRequested = false

    private val allProductsGroup = ProductGroupEntity(
        productCategoryCode = ALL_PRODUCTS_GROUP_CODE,
        productCategoryName = "همه",
        productCategoryImage = null,
        productCategoryImageUnselect = null
    )

    init {
        onEvent(HomeEvent.Started)
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.Started -> productCheck()

            HomeEvent.Refresh -> refresh()

            is HomeEvent.SearchChanged -> onSearchChanged(event.query)

            is HomeEvent.GroupSelected -> onGroupSelected(event.productCategoryCode)

            is HomeEvent.DiscountClicked -> discountRequest(event.product.id)

            is HomeEvent.ProductQuantitySaved -> {
                saveProductOrder(
                    productModelEntity = event.product,
                    value1 = event.value1,
                    value2 = event.value2
                )
            }

            is HomeEvent.ProductClicked -> {
                Timber.tag(TAG).d("Product clicked | id=${event.product.id}")
            }

            HomeEvent.ErrorDismissed -> clearError()
        }
    }

    fun productCheck() {
        if (firstLoadRequested) return
        firstLoadRequested = true

        startObserversIfNeeded()
        syncCatalog(force = false)
    }

    fun refresh() {
        startObserversIfNeeded()
        syncCatalog(force = true)
    }

    fun onGroupSelected(productCategoryCode: Int) {
        if (_uiState.value.selectedGroupCode == productCategoryCode) return

        _uiState.update {
            it.copy(
                selectedGroupCode = productCategoryCode,
                isLoading = it.products.isEmpty(),
                errorMessage = null
            )
        }

        filterState.update {
            it.copy(groupCode = productCategoryCode)
        }
    }

    fun onSearchChanged(search: String) {
        val normalizedSearch = converter.PersianToEnglish(search.trim())

        _uiState.update {
            it.copy(
                searchQuery = search,
                isLoading = it.products.isEmpty(),
                errorMessage = null
            )
        }

        filterState.update {
            it.copy(searchQuery = normalizedSearch)
        }
    }

    fun clearError() {
        _state.update {
            it.copy(error = null)
        }

        _uiState.update {
            it.copy(errorMessage = null)
        }
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
            runCatching {
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
            }.onFailure { throwable ->
                Timber.tag(TAG).e(throwable, "Save product order failed")
                updateStateError("ثبت کالا در سبد خرید با خطا مواجه شد")
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
        if (_uiState.value.isDiscountLoading) return

        _uiState.update {
            it.copy(
                activeDiscountProductCode = productCode,
                discounts = emptyList(),
                isDiscountLoading = true,
                errorMessage = null
            )
        }

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
                        activeDiscountProductCode = productCode,
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
                    val products = response?.data.orEmpty()

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
                    val groups = response?.data.orEmpty()

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
                _uiState.update {
                    it.copy(
                        banners = response?.data.orEmpty(),
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
            filterState
                .debounce(250)
                .distinctUntilChanged()
                .flatMapLatest { filter ->
                    homeRepository.observeProducts(
                        groupCode = filter.groupCode,
                        searchQuery = filter.searchQuery
                    )
                }
                .collect { products ->
                    _uiState.update {
                        it.copy(
                            products = products,
                            selectedGroupCode = filterState.value.groupCode,
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
                val uiGroups = buildList {
                    add(allProductsGroup)

                    addAll(
                        productGroups
                            .asSequence()
                            .filter { it.productCategoryCode != ALL_PRODUCTS_GROUP_CODE }
                            .filter { it.productCategoryCode != 0 }
                            .distinctBy { it.productCategoryCode }
                            .toList()
                    )
                }

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
                isRefreshing = false,
                isDiscountLoading = false,
                errorMessage = errorMessage
            )
        }
    }

    companion object {
        private const val TAG = "HomeViewModel"
    }
}