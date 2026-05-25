package com.msa.eshop.ui.screen.basket

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.msa.eshop.ui.common.card.BasketCard
import com.msa.eshop.ui.common.topBar.TopBarDetails
import com.msa.eshop.ui.component.dialog.ErrorDialog
import com.msa.eshop.ui.component.lottiefile.LoadingAnimate
import com.msa.eshop.ui.theme.PlatinumSilver
import com.msa.eshop.utils.Currency

@Composable
fun BasketScreen(
    modifier: Modifier = Modifier,
    viewModel: BasketViewModel = hiltViewModel()
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
                TopBarDetails("سبد خرید")
            },
            bottomBar = {
                BasketBottomBar(
                    totalPrice = uiState.totalPrice,
                    enabled = uiState.canSubmit,
                    onSubmit = viewModel::navigateToSimulate
                )
            }
        ) { innerPadding ->
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                when {
                    uiState.isEmpty -> {
                        EmptyBasket(
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
                                items = uiState.orders,
                                key = { it.id },
                                contentType = { "basket_item" }
                            ) { order ->
                                BasketCard(
                                    orderEntity = order,
                                    onQuantityChange = { value1, value2 ->
                                        viewModel.updateOrderQuantity(
                                            orderEntity = order,
                                            value1 = value1,
                                            value2 = value2
                                        )
                                    },
                                    onDelete = {
                                        viewModel.deleteOrder(order.id)
                                    }
                                )
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
private fun EmptyBasket(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "سبد خرید شما خالی است",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun BasketBottomBar(
    totalPrice: Long,
    enabled: Boolean,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .navigationBarsPadding()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)

        Text(
            text = "جمع کل: ${Currency(totalPrice).toFormattedString()} ریال",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Button(
            enabled = enabled,
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.pre_invoice_registration),
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}