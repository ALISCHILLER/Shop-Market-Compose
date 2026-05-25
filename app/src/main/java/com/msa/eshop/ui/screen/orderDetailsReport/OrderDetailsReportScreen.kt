package com.msa.eshop.ui.screen.orderDetailsReport

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.msa.eshop.data.Model.response.ReportCartDetailsModel
import com.msa.eshop.ui.common.card.OrderDetailsReportCard
import com.msa.eshop.ui.common.topBar.TopBarDetails
import com.msa.eshop.ui.component.dialog.ErrorDialog
import com.msa.eshop.ui.component.lottiefile.LoadingAnimate
import com.msa.eshop.ui.theme.PlatinumSilver

@Composable
fun OrderDetailsReportScreen(
    modifier: Modifier = Modifier,
    card: Int = 0,
    viewModel: OrderDetailsReportViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    LaunchedEffect(card) {
        viewModel.reportCartDetailsRequest(card)
    }

    uiState.errorMessage?.let { error ->
        ErrorDialog(
            error,
            { viewModel.clearError() },
            false
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PlatinumSilver)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = PlatinumSilver,
            topBar = {
                TopBarDetails(
                    name = "جزئیات سفارش",
                    details = if (card > 0) "شماره سفارش: $card" else ""
                )
            }
        ) { innerPadding ->
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                when {
                    uiState.isEmpty -> {
                        EmptyOrderDetails(
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxSize()
                        )
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxSize(),
                            contentPadding = PaddingValues(12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            itemsIndexed(
                                items = uiState.items,
                                key = { index, item ->
                                    item.orderDetailsStableKey(index)
                                },
                                contentType = { _, _ -> "order_detail" }
                            ) { _, order ->
                                OrderDetailsReportCard(reportCart = order)
                            }
                        }
                    }
                }
            }
        }

        if (uiState.isLoading) {
            LoadingAnimate()
        }
    }
}

private fun ReportCartDetailsModel.orderDetailsStableKey(index: Int): String {
    return buildString {
        append(id)
        append("_")
        append(cartCode)
        append("_")
        append(productCode)
        append("_")
        append(quantity)
        append("_")
        append(index)
    }
}

@Composable
private fun EmptyOrderDetails(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "جزئیاتی برای این سفارش پیدا نشد",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}