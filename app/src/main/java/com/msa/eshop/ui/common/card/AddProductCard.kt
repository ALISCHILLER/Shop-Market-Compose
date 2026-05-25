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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.msa.eshop.R
import com.msa.eshop.data.local.entity.OrderEntity
import com.msa.eshop.data.local.entity.ProductModelEntity
import com.msa.eshop.ui.component.weightC.CounterButtonNew
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
    var value1 by rememberSaveable(product.id, order?.numberOrder1) {
        mutableIntStateOf(order?.numberOrder1 ?: 0)
    }

    var value2 by rememberSaveable(product.id, order?.numberOrder2) {
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

    val hasSecondUnit = !product.fullNameKala2.isNullOrBlank() && product.convertFactor2 > 0
    val canSubmit = totalValue > 0 || order != null

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .aspectRatio(1f)
                ) {
                    AsyncImage(
                        model = product.productImage,
                        contentDescription = product.productName,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                        error = painterResource(id = R.drawable.not_load_image),
                        placeholder = painterResource(id = R.drawable.not_load_image)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = product.productName.orEmpty(),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    PriceLine(
                        price = product.price,
                        label = "فی:"
                    )
                }
            }

            QuantityRow(
                title = product.fullNameKala1 ?: "عدد",
                value = value1,
                onValueChange = { value1 = it.coerceAtLeast(0) }
            )

            if (hasSecondUnit) {
                QuantityRow(
                    title = product.fullNameKala2.orEmpty(),
                    value = value2,
                    onValueChange = { value2 = it.coerceAtLeast(0) }
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "مبلغ ناخالص:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = Currency(totalPrice).toFormattedString(),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "ریال",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = {
                    onSaveOrder(product, value1, value2)
                    onDismissRequest()
                },
                enabled = canSubmit,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (totalValue == 0 && order != null) {
                        "حذف از سبد خرید"
                    } else {
                        stringResource(id = R.string.save_Order)
                    },
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}

@Composable
private fun QuantityRow(
    title: String,
    value: Int,
    onValueChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$title:",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            CounterButtonNew(
                value = value.toString(),
                onValueClearClick = {
                    onValueChange(0)
                },
                onValue = {
                    onValueChange(it.toIntOrNull()?.coerceAtLeast(0) ?: 0)
                }
            )
        }
    }
}

@Composable
private fun PriceLine(
    price: Int,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = Currency(price).toFormattedString(),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = "ریال",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}