package com.msa.eshop.ui.screen.home

import com.msa.eshop.data.Model.response.BannerModel
import com.msa.eshop.data.Model.response.DiscountResultModel
import com.msa.eshop.data.local.entity.OrderEntity
import com.msa.eshop.data.local.entity.ProductGroupEntity
import com.msa.eshop.data.local.entity.ProductModelEntity

const val ALL_PRODUCTS_GROUP_CODE = 99

data class HomeUiState(
    val products: List<ProductModelEntity> = emptyList(),
    val productGroups: List<ProductGroupEntity> = emptyList(),
    val banners: List<BannerModel> = emptyList(),
    val orders: List<OrderEntity> = emptyList(),
    val discounts: List<DiscountResultModel> = emptyList(),
    val selectedGroupCode: Int = ALL_PRODUCTS_GROUP_CODE,
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isDiscountLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val isInitialLoading: Boolean
        get() = isLoading && products.isEmpty()

    val shouldShowEmpty: Boolean
        get() = !isLoading && products.isEmpty() && errorMessage == null
}