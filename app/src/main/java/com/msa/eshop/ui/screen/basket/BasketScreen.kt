package com.msa.eshop.ui.screen.basket

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.msa.eshop.R
import com.msa.eshop.ui.common.card.BasketCard
import com.msa.eshop.ui.common.topBar.TopBarDetails
import com.msa.eshop.ui.theme.Typography

@Composable
fun BasketScreen(
    modifier: Modifier = Modifier,
    viewModel: BasketViewModel = hiltViewModel()
) {
    val orders by viewModel.allOrder.collectAsState()

    androidx.compose.material3.Scaffold(
        modifier = modifier.background(color = Color.White),
        topBar = {
            TopBarDetails("لیست خرید های شما")
        }
    ) { innerPadding ->
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .background(color = Color.White)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                if (orders.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "سبد خرید شما خالی است",
                            style = Typography.titleSmall
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(
                            items = orders,
                            key = { it.id }
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

                Button(
                    enabled = orders.isNotEmpty(),
                    onClick = {
                        viewModel.navigateToSimulate()
                    },
                    modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    ),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.pre_invoice_registration),
                        style = Typography.titleSmall
                    )
                }
            }
        }
    }
}