package com.msa.eshop.ui.screen.orderDetailsReport

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.msa.eshop.ui.common.card.OrderDetailsReportCard
import com.msa.eshop.ui.common.topBar.TopBarDetails
import com.msa.eshop.ui.theme.PlatinumSilver

@Composable
fun OrderDetailsReportScreen(
    modifier: Modifier = Modifier,
    card: Int = 0,
    viewModel: OrderDetailsReportViewModel = hiltViewModel()
) {
    val orderDetails by viewModel.reportCartDetails.collectAsStateWithLifecycle()

    LaunchedEffect(card) {
        if (card > 0) {
            viewModel.reportCartDetailsRequest(card)
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(PlatinumSilver),
        containerColor = PlatinumSilver,
        topBar = {
            TopBarDetails("جزئیات سفارش")
        }
    ) { innerPadding ->
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            if (orderDetails.isEmpty()) {
                EmptyOrderDetails(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(12.dp)
                ) {
                    items(
                        items = orderDetails,
                        key = { item ->
                            "${item.productCode}_${item.productName}"
                        }
                    ) { order ->
                        OrderDetailsReportCard(reportCart = order)
                    }
                }
            }
        }
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