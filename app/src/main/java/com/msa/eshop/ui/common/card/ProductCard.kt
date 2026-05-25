@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.msa.eshop.data.Model.response.DiscountResultModel
import com.msa.eshop.data.local.entity.OrderEntity
import com.msa.eshop.data.local.entity.ProductModelEntity
import com.msa.eshop.ui.component.bottomSheetC.BottomSheetExample
import com.msa.eshop.utils.Currency

@Composable
fun ProductCard(
    modifier: Modifier = Modifier,
    product: ProductModelEntity,
    order: OrderEntity?,
    discounts: List<DiscountResultModel>,
    isDiscountLoading: Boolean,
    onSaveOrder: (ProductModelEntity, Int, Int) -> Unit,
    onDiscountClick: (ProductModelEntity) -> Unit,
    onClick: (ProductModelEntity) -> Unit
) {
    var showAddSheet by rememberSaveable(product.id) {
        mutableStateOf(false)
    }

    var showDiscountSheet by rememberSaveable(product.id) {
        mutableStateOf(false)
    }

    if (showAddSheet) {
        BottomSheetExample(
            onDismissRequest = {
                showAddSheet = false
            }
        ) {
            AddProduct(
                product = product,
                order = order,
                onSaveOrder = onSaveOrder,
                onDismissRequest = {
                    showAddSheet = false
                }
            )
        }
    }

    if (showDiscountSheet) {
        BottomSheetExample(
            onDismissRequest = {
                showDiscountSheet = false
            }
        ) {
            DiscountsProductCard(
                product = product,
                discounts = discounts,
                isLoading = isDiscountLoading,
                onDismissRequest = {
                    showDiscountSheet = false
                }
            )
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .clickable { onClick(product) },
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(18.dp)
                        )
                ) {
                    AsyncImage(
                        model = product.productImage,
                        contentDescription = product.productName,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                        error = painterResource(id = R.drawable.not_load_image),
                        placeholder = painterResource(id = R.drawable.not_load_image)
                    )

                    if (product.isDiscounts) {
                        DiscountBadge(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp),
                            onClick = {
                                onDiscountClick(product)
                                showDiscountSheet = true
                            }
                        )
                    }
                }

                Text(
                    text = product.productName.orEmpty(),
                    maxLines = 2,
                    minLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                PriceRow(price = product.price)

                if (order != null) {
                    Text(
                        text = "در سبد: ${order.numberOrder} ${order.unitOrder.orEmpty()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Button(
                    onClick = {
                        showAddSheet = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.AddShoppingCart,
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = stringResource(id = R.string.add_to_cart),
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun DiscountBadge(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(20.dp),
                painter = painterResource(id = R.drawable.ic_discount),
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "تخفیف",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun PriceRow(
    price: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = Currency(price).toFormattedString(),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = "ریال",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}