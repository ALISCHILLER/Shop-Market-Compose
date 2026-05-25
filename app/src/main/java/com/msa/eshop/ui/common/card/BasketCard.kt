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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.msa.eshop.R
import com.msa.eshop.data.local.entity.OrderEntity
import com.msa.eshop.ui.component.dialog.AlertDialogExample
import com.msa.eshop.ui.component.weightC.CounterButtonNew
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
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(84.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
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
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = orderEntity.productName.orEmpty(),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "فی:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Text(
                                text = Currency(orderEntity.price).toFormattedString(),
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

                    IconButton(
                        onClick = {
                            showDeleteDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "حذف",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                BasketQuantityRow(
                    title = orderEntity.fullNameKala1 ?: "عدد",
                    value = value1,
                    onValueChange = {
                        updateQuantity(newValue1 = it)
                    }
                )

                if (!orderEntity.fullNameKala2.isNullOrBlank() && orderEntity.convertFactor2 > 0) {
                    BasketQuantityRow(
                        title = orderEntity.fullNameKala2,
                        value = value2,
                        onValueChange = {
                            updateQuantity(newValue2 = it)
                        }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "ریال",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun BasketQuantityRow(
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
            style = MaterialTheme.typography.bodyMedium,
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