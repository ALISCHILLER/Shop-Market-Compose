package com.msa.eshop.ui.common.card

import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.msa.eshop.R
import com.msa.eshop.data.local.entity.OrderEntity
import com.msa.eshop.data.local.entity.ProductModelEntity
import com.msa.eshop.ui.component.weightC.CounterButtonNew
import com.msa.eshop.ui.theme.PlatinumSilver
import com.msa.eshop.ui.theme.Typography
import com.msa.eshop.ui.theme.barcolorlight2
import com.msa.eshop.utils.Currency
import com.msa.eshop.utils.calculateSalePrice
import com.msa.eshop.utils.calculateTotalValue

@Composable
fun AddProduct(
    modifier: Modifier = Modifier,
    product: ProductModelEntity,
    order: OrderEntity?,
    onSaveOrder: (ProductModelEntity, Int, Int) -> Unit,
    onDismissRequest: () -> Unit
) {
    var value1 by remember(product.id, order?.numberOrder1) {
        mutableIntStateOf(order?.numberOrder1 ?: 0)
    }

    var value2 by remember(product.id, order?.numberOrder2) {
        mutableIntStateOf(order?.numberOrder2 ?: 0)
    }

    val totalValue = calculateTotalValue(
        value1 = value1,
        value2 = value2,
        convertFactor2 = product.convertFactor2
    )

    val totalPrice = calculateSalePrice(
        totalValue = totalValue,
        price = product.price
    )

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier
                .background(Color.White)
                .padding(17.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .padding(5.dp)
                        .size(width = 100.dp, height = 75.dp)
                        .background(
                            color = PlatinumSilver,
                            shape = RoundedCornerShape(18.dp)
                        )
                        .aspectRatio(1f)
                ) {
                    AsyncImage(
                        model = product.productImage,
                        contentDescription = product.productName,
                        modifier = Modifier.fillMaxSize(),
                        error = painterResource(id = R.drawable.not_load_image),
                        placeholder = painterResource(id = R.drawable.not_load_image)
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White)
                ) {
                    Text(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        text = product.productName.orEmpty(),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = Typography.titleSmall
                    )

                    Row(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "فی:",
                            style = Typography.titleSmall
                        )

                        Spacer(modifier = Modifier.padding(5.dp))

                        Text(
                            text = Currency(product.price).toFormattedString(),
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

            Row(
                modifier = Modifier
                    .padding(7.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${product.fullNameKala1 ?: "عدد"} :",
                    style = Typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    CounterButtonNew(
                        value = value1.toString(),
                        onValueIncreaseClick = {},
                        onValueDecreaseClick = {},
                        onValueClearClick = {
                            value1 = 0
                        },
                        onValue = {
                            value1 = it.toIntOrNull()?.coerceAtLeast(0) ?: 0
                        }
                    )
                }
            }

            if (!product.fullNameKala2.isNullOrBlank() && product.convertFactor2 > 0) {
                Row(
                    modifier = Modifier
                        .padding(7.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${product.fullNameKala2} :",
                        style = Typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        CounterButtonNew(
                            value = value2.toString(),
                            onValueIncreaseClick = {},
                            onValueDecreaseClick = {},
                            onValueClearClick = {
                                value2 = 0
                            },
                            onValue = {
                                value2 = it.toIntOrNull()?.coerceAtLeast(0) ?: 0
                            }
                        )
                    }
                }
            }

            HorizontalDivider(
                color = Color.Gray,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Row(
                modifier = Modifier
                    .padding(7.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "مبلغ ناخالص:",
                    style = Typography.titleSmall
                )

                Spacer(modifier = Modifier.width(5.dp))

                Text(
                    text = Currency(totalPrice).toFormattedString(),
                    style = Typography.titleLarge
                )

                Spacer(modifier = Modifier.width(5.dp))

                Text(
                    text = "ریال",
                    style = Typography.titleSmall
                )
            }

            Button(
                onClick = {
                    onSaveOrder(product, value1, value2)
                    onDismissRequest()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.save_Order),
                    style = Typography.titleSmall
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddProductPreview() {
    AddProduct(
        product = ProductModelEntity(
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
            isDiscounts = true,
            productImage = ""
        ),
        order = null,
        onSaveOrder = { _, _, _ -> },
        onDismissRequest = {}
    )
}