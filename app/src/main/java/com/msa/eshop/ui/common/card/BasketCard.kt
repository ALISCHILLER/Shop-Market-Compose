package com.msa.eshop.ui.common.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.msa.eshop.R
import com.msa.eshop.data.local.entity.OrderEntity
import com.msa.eshop.ui.component.dialog.AlertDialogExample
import com.msa.eshop.ui.component.weightC.CounterButtonNew
import com.msa.eshop.ui.theme.PlatinumSilver
import com.msa.eshop.ui.theme.Typography
import com.msa.eshop.ui.theme.barcolorlight2
import com.msa.eshop.utils.Currency
import com.msa.eshop.utils.calculateSalePrice
import com.msa.eshop.utils.calculateTotalValue

@Composable
fun BasketCard(
    modifier: Modifier = Modifier,
    orderEntity: OrderEntity,
    onQuantityChange: (Int, Int) -> Unit,
    onDelete: () -> Unit
) {
    var value1 by remember(orderEntity.id, orderEntity.numberOrder1) {
        mutableIntStateOf(orderEntity.numberOrder1)
    }

    var value2 by remember(orderEntity.id, orderEntity.numberOrder2) {
        mutableIntStateOf(orderEntity.numberOrder2)
    }

    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    val totalValue = calculateTotalValue(
        value1 = value1,
        value2 = value2,
        convertFactor2 = orderEntity.convertFactor2
    )

    val totalPrice = calculateSalePrice(
        totalValue = totalValue,
        price = orderEntity.price
    )

    if (showDeleteDialog) {
        AlertDialogExample(
            onConfirmation = {
                onDelete()
                showDeleteDialog = false
            },
            onDismissRequest = {
                showDeleteDialog = false
            }
        )
    }

    fun updateQuantity(newValue1: Int = value1, newValue2: Int = value2) {
        value1 = newValue1.coerceAtLeast(0)
        value2 = newValue2.coerceAtLeast(0)
        onQuantityChange(value1, value2)
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 5.dp)
                .shadow(10.dp, RoundedCornerShape(18.dp)),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(5.dp)
                                .size(width = 90.dp, height = 65.dp)
                                .background(
                                    color = PlatinumSilver,
                                    shape = RoundedCornerShape(18.dp)
                                )
                                .aspectRatio(1f)
                        ) {
                            AsyncImage(
                                model = orderEntity.productImage,
                                contentDescription = orderEntity.productName,
                                modifier = Modifier.fillMaxSize(),
                                error = painterResource(id = R.drawable.not_load_image),
                                placeholder = painterResource(id = R.drawable.not_load_image)
                            )
                        }

                        Column(
                            modifier = Modifier.background(Color.White)
                        ) {
                            Text(
                                modifier = Modifier.padding(3.dp),
                                text = orderEntity.productName.orEmpty(),
                                style = Typography.labelSmall,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            Row(
                                modifier = Modifier.padding(3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "فی:",
                                    style = Typography.titleSmall
                                )

                                Spacer(modifier = Modifier.padding(5.dp))

                                Text(
                                    text = Currency(orderEntity.price).toFormattedString(),
                                    style = Typography.titleSmall,
                                    color = barcolorlight2
                                )

                                Spacer(modifier = Modifier.padding(5.dp))

                                Text(
                                    text = "ریال",
                                    style = Typography.titleSmall,
                                    color = barcolorlight2
                                )
                            }
                        }
                    }

                    Image(
                        painter = painterResource(id = R.drawable.remove_icon),
                        contentDescription = "delete",
                        modifier = Modifier
                            .padding(5.dp)
                            .size(50.dp)
                            .clickable {
                                showDeleteDialog = true
                            }
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "${orderEntity.fullNameKala1 ?: "عدد"} :",
                            style = Typography.titleSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            CounterButtonNew(
                                value = value1.toString(),
                                onValueIncreaseClick = {},
                                onValueDecreaseClick = {},
                                onValueClearClick = {
                                    updateQuantity(newValue1 = 0)
                                },
                                onValue = {
                                    updateQuantity(
                                        newValue1 = it.toIntOrNull()?.coerceAtLeast(0) ?: 0
                                    )
                                }
                            )
                        }
                    }
                }

                if (!orderEntity.fullNameKala2.isNullOrBlank() && orderEntity.convertFactor2 > 0) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "${orderEntity.fullNameKala2} :",
                                style = Typography.titleSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                            Box(
                                modifier = Modifier.weight(1f)
                            ) {
                                CounterButtonNew(
                                    value = value2.toString(),
                                    onValueIncreaseClick = {},
                                    onValueDecreaseClick = {},
                                    onValueClearClick = {
                                        updateQuantity(newValue2 = 0)
                                    },
                                    onValue = {
                                        updateQuantity(
                                            newValue2 = it.toIntOrNull()?.coerceAtLeast(0) ?: 0
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(horizontal = 3.dp, vertical = 4.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "مبلغ ناخالص:",
                        style = Typography.titleSmall
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Text(
                        text = Currency(totalPrice).toFormattedString(),
                        style = Typography.titleSmall
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Text(
                        text = "ریال",
                        style = Typography.titleSmall
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun BasketCardPreview() {
    Column(
        modifier = Modifier.background(Color.White)
    ) {
        BasketCard(
            orderEntity = OrderEntity(
                id = "11",
                convertFactor1 = 1,
                convertFactor2 = 12,
                fullNameKala1 = "عدد (1)",
                fullNameKala2 = "کارتن (12)",
                productCode = 659985,
                productGroupCode = 5,
                productName = "روغن ذرت زر",
                unit1 = "EA",
                unit2 = "KAR",
                unitid1 = "54654",
                unitid2 = "4565",
                price = 98563,
                productImage = "",
                numberOrder1 = 4,
                unitOrder = "",
                numberOrder2 = 2,
                numberOrder = 28
            ),
            onQuantityChange = { _, _ -> },
            onDelete = {}
        )
    }
}