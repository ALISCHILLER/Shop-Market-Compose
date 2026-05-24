@file:OptIn(ExperimentalMaterial3Api::class)

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.msa.componentcompose.ui.component.lottiefile.LoadingAnimate
import com.msa.eshop.data.local.entity.ProductGroupEntity
import com.msa.eshop.ui.common.card.ProductCard
import com.msa.eshop.ui.common.card.ProductGroupCard
import com.msa.eshop.ui.component.verticalSlider.VerticalSlider
import com.msa.eshop.ui.theme.PlatinumSilver

private const val ALL_PRODUCTS_GROUP_CODE = 99

@Composable
fun HomeScreen() {
    val viewModel: HomeViewModel = hiltViewModel()

    val state by viewModel.state.collectAsState()
    val products by viewModel.allProduct.collectAsState()
    val banner by viewModel.banner.collectAsState()
    val productGroupsFromDb by viewModel.allProductGroup.collectAsState()

    var selectedProductGroupCode by remember {
        mutableIntStateOf(ALL_PRODUCTS_GROUP_CODE)
    }

    val productGroups = remember(productGroupsFromDb) {
        listOf(
            ProductGroupEntity(
                productCategoryCode = ALL_PRODUCTS_GROUP_CODE,
                productCategoryName = "همه",
                productCategoryImage = null,
                productCategoryImageUnselect = null
            )
        ) + productGroupsFromDb
    }

    LaunchedEffect(Unit) {
        viewModel.productCheck()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PlatinumSilver)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopBarSearch(
                    onSearchChange = { query ->
                        selectedProductGroupCode = ALL_PRODUCTS_GROUP_CODE
                        viewModel.searchProduct(query)
                    }
                )
            }
        ) { innerPadding ->
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                SwipeRefresh(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    state = rememberSwipeRefreshState(
                        isRefreshing = state.isLoading
                    ),
                    onRefresh = {
                        selectedProductGroupCode = ALL_PRODUCTS_GROUP_CODE
                        viewModel.refresh()
                    }
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White),
                        contentPadding = PaddingValues(
                            start = 8.dp,
                            end = 8.dp,
                            top = 8.dp,
                            bottom = 16.dp
                        ),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item(
                            span = { GridItemSpan(maxLineSpan) }
                        ) {
                            VerticalSlider(
                                imageUrl = banner,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                            )
                        }

                        item(
                            span = { GridItemSpan(maxLineSpan) }
                        ) {
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                LazyRow(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White)
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp)
                                ) {
                                    items(
                                        items = productGroups,
                                        key = { it.productCategoryCode }
                                    ) { productGroup ->
                                        ProductGroupCard(
                                            productGroupEntity = productGroup,
                                            isSelected = selectedProductGroupCode == productGroup.productCategoryCode,
                                            onClick = {
                                                selectedProductGroupCode = it.productCategoryCode
                                                viewModel.getProduct(it.productCategoryCode)
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        if (products.isEmpty() && !state.isLoading) {
                            item(
                                span = { GridItemSpan(maxLineSpan) }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "کالایی برای نمایش وجود ندارد"
                                    )
                                }
                            }
                        } else {
                            items(
                                items = products,
                                key = { it.id }
                            ) { product ->
                                ProductCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    product = product,
                                    onClick = {
                                        // TODO
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        if (state.isLoading && products.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LoadingAnimate()
            }
        }
    }
}