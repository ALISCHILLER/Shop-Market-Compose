package com.msa.eshop.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.msa.eshop.ui.common.card.ProductCard
import com.msa.eshop.ui.common.card.ProductGroupCard
import com.msa.eshop.ui.component.lottiefile.LoadingAnimate
import com.msa.eshop.ui.component.verticalSlider.VerticalSlider
import com.msa.eshop.ui.theme.PlatinumSilver

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    HomeContent(
        uiState = uiState,
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onEvent: (HomeEvent) -> Unit
) {
    val orderByProductId = remember(uiState.orders) {
        uiState.orders.associateBy { it.id }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PlatinumSilver)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = PlatinumSilver,
            topBar = {
                TopBarSearch(
                    query = uiState.searchQuery,
                    onSearchChange = {
                        onEvent(HomeEvent.SearchChanged(it))
                    }
                )
            }
        ) { innerPadding ->
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                SwipeRefresh(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    state = rememberSwipeRefreshState(uiState.isRefreshing),
                    onRefresh = {
                        onEvent(HomeEvent.Refresh)
                    }
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 168.dp),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 12.dp,
                            end = 12.dp,
                            top = 12.dp,
                            bottom = 24.dp
                        ),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (uiState.banners.isNotEmpty()) {
                            item(
                                span = { GridItemSpan(maxLineSpan) },
                                key = "banners",
                                contentType = "banners"
                            ) {
                                VerticalSlider(
                                    imageUrl = uiState.banners,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(178.dp)
                                )
                            }
                        }

                        if (uiState.productGroups.isNotEmpty()) {
                            item(
                                span = { GridItemSpan(maxLineSpan) },
                                key = "product_groups",
                                contentType = "product_groups"
                            ) {
                                LazyRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    contentPadding = PaddingValues(
                                        horizontal = 2.dp,
                                        vertical = 6.dp
                                    )
                                ) {
                                    items(
                                        items = uiState.productGroups,
                                        key = { it.productCategoryCode },
                                        contentType = { "product_group" }
                                    ) { productGroup ->
                                        ProductGroupCard(
                                            productGroupEntity = productGroup,
                                            isSelected = uiState.selectedGroupCode == productGroup.productCategoryCode,
                                            onClick = {
                                                onEvent(
                                                    HomeEvent.GroupSelected(
                                                        it.productCategoryCode
                                                    )
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        when {
                            uiState.errorMessage != null && uiState.products.isEmpty() -> {
                                item(
                                    span = { GridItemSpan(maxLineSpan) },
                                    key = "error",
                                    contentType = "state"
                                ) {
                                    StateMessage(
                                        message = uiState.errorMessage,
                                        modifier = Modifier.padding(top = 40.dp)
                                    )
                                }
                            }

                            uiState.shouldShowEmpty -> {
                                item(
                                    span = { GridItemSpan(maxLineSpan) },
                                    key = "empty",
                                    contentType = "state"
                                ) {
                                    StateMessage(
                                        message = "کالایی برای نمایش وجود ندارد",
                                        modifier = Modifier.padding(top = 40.dp)
                                    )
                                }
                            }

                            else -> {
                                items(
                                    items = uiState.products,
                                    key = { it.id },
                                    contentType = { "product" }
                                ) { product ->
                                    ProductCard(
                                        modifier = Modifier.fillMaxWidth(),
                                        product = product,
                                        order = orderByProductId[product.id],
                                        discounts = if (uiState.activeDiscountProductCode == product.id) {
                                            uiState.discounts
                                        } else {
                                            emptyList()
                                        },
                                        isDiscountLoading = uiState.isDiscountLoading &&
                                                uiState.activeDiscountProductCode == product.id,
                                        onSaveOrder = { selectedProduct, value1, value2 ->
                                            onEvent(
                                                HomeEvent.ProductQuantitySaved(
                                                    product = selectedProduct,
                                                    value1 = value1,
                                                    value2 = value2
                                                )
                                            )
                                        },
                                        onDiscountClick = {
                                            onEvent(HomeEvent.DiscountClicked(it))
                                        },
                                        onClick = {
                                            onEvent(HomeEvent.ProductClicked(it))
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (uiState.isInitialLoading) {
            LoadingAnimate()
        }
    }
}

@Composable
private fun StateMessage(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}