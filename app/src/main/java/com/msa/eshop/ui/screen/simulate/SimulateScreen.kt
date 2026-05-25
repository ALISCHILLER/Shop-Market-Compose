package com.msa.eshop.ui.screen.simulate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.msa.eshop.R
import com.msa.eshop.data.local.entity.PaymentMethodEntity
import com.msa.eshop.ui.common.card.SimulateCard
import com.msa.eshop.ui.common.topBar.TopBarDetails
import com.msa.eshop.ui.component.dialog.ErrorDialog
import com.msa.eshop.ui.component.lottiefile.LoadingAnimate
import com.msa.eshop.ui.theme.PlatinumSilver
import com.msa.eshop.utils.Currency

@Composable
fun SimulateScreen(
    modifier: Modifier = Modifier,
    viewModel: SimulateViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

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
                TopBarDetails("پیش‌نمایش فاکتور")
            },
            bottomBar = {
                SimulateBottomBar(
                    uiState = uiState,
                    onChooseAddressClick = {
                        val paymentMethod = PaymentMethodEntity(
                            cashprice = uiState.cashTotal.toString(),
                            checkprice = uiState.checkTotal.toString(),
                            receiptprice = uiState.receiptTotal.toString()
                        )

                        viewModel.savePayment(paymentMethod)
                    }
                )
            }
        ) { innerPadding ->
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                when {
                    uiState.isEmpty -> {
                        EmptySimulateState(
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
                            items(
                                items = uiState.items,
                                key = { it.id },
                                contentType = { "simulate_item" }
                            ) { simulate ->
                                SimulateCard(simulate = simulate)
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

@Composable
private fun SimulateBottomBar(
    uiState: SimulateUiState,
    onChooseAddressClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SummaryRow(
                    title = "قیمت نقدی",
                    value = "${Currency(uiState.cashTotal).toFormattedString()} ریال"
                )

                SummaryRow(
                    title = "قیمت چک",
                    value = "${Currency(uiState.checkTotal).toFormattedString()} ریال"
                )

                SummaryRow(
                    title = "قیمت عرفی",
                    value = "${Currency(uiState.receiptTotal).toFormattedString()} ریال"
                )
            }
        }

        Button(
            onClick = onChooseAddressClick,
            enabled = uiState.canContinue,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.choess_address),
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}

@Composable
private fun SummaryRow(
    title: String,
    value: String
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$title:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun EmptySimulateState(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "سبد خرید شما برای پیش‌نمایش فاکتور خالی است",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}