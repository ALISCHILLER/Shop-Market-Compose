package com.msa.eshop.ui.screen.home

import com.msa.eshop.data.local.entity.ProductModelEntity

sealed interface HomeEvent {

    data object Started : HomeEvent

    data object Refresh : HomeEvent

    data class SearchChanged(
        val query: String
    ) : HomeEvent

    data class GroupSelected(
        val productCategoryCode: Int
    ) : HomeEvent

    data class DiscountClicked(
        val product: ProductModelEntity
    ) : HomeEvent

    data class ProductQuantitySaved(
        val product: ProductModelEntity,
        val value1: Int,
        val value2: Int
    ) : HomeEvent

    data class ProductClicked(
        val product: ProductModelEntity
    ) : HomeEvent

    data object ErrorDismissed : HomeEvent
}