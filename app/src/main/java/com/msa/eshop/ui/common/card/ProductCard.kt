@file:OptIn(ExperimentalMaterial3Api::class)

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
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
import com.msa.eshop.data.Model.response.DiscountResultModel
import com.msa.eshop.data.local.entity.OrderEntity
import com.msa.eshop.data.local.entity.ProductModelEntity
import com.msa.eshop.ui.component.bottomSheetC.BottomSheetExample
import com.msa.eshop.ui.theme.DIMENS_2dp
import com.msa.eshop.ui.theme.PlatinumSilver
import com.msa.eshop.ui.theme.RedMain
import com.msa.eshop.ui.theme.Typography
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
    var showBottomSheet by remember {
        mutableStateOf(false)
    }

    var showBottomSheetDiscounts by remember {
        mutableStateOf(false)
    }

    if (showBottomSheet) {
        BottomSheetExample(
            modifier = Modifier.fillMaxWidth(),
            onDismissRequest = {
                showBottomSheet = false
            }
        ) {
            AddProduct(
                product = product,
                order = order,
                onSaveOrder = onSaveOrder,
                onDismissRequest = {
                    showBottomSheet = false
                }
            )
        }
    }

    if (showBottomSheetDiscounts) {
        BottomSheetExample(
            onDismissRequest = {
                showBottomSheetDiscounts = false
            }
        ) {
            DiscountsProductCard(
                product = product,
                discounts = discounts,
                isLoading = isDiscountLoading,
                onDismissRequest = {
                    showBottomSheetDiscounts = false
                }
            )
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier
                .padding(4.dp)
                .shadow(
                    elevation = 5.dp,
                    shape = RoundedCornerShape(18.dp)
                )
                .clickable {
                    onClick(product)
                }
        ) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(1.dp),
                color = Color.White
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = PlatinumSilver,
                                shape = RoundedCornerShape(18.dp)
                            )
                            .aspectRatio(1f)
                            .fillMaxWidth()
                    ) {
                        AsyncImage(
                            model = product.productImage,
                            contentDescription = product.productName,
                            modifier = Modifier.fillMaxSize(),
                            error = painterResource(id = R.drawable.not_load_image),
                            placeholder = painterResource(id = R.drawable.not_load_image)
                        )

                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .alpha(if (product.isDiscounts) 1f else 0f)
                                .clickable(enabled = product.isDiscounts) {
                                    onDiscountClick(product)
                                    showBottomSheetDiscounts = true
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                modifier = Modifier.size(40.dp),
                                painter = painterResource(id = R.drawable.ic_discount),
                                contentDescription = "discount"
                            )
                        }
                    }

                    Text(
                        text = product.productName.orEmpty(),
                        modifier = Modifier.padding(vertical = 8.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = Typography.labelSmall
                    )

                    Column(
                        modifier = Modifier.padding(5.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(vertical = 3.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = Currency(product.price).toFormattedString(),
                                    style = Typography.labelSmall
                                )

                                Spacer(modifier = Modifier.padding(2.dp))

                                Text(
                                    text = "ریال",
                                    style = Typography.labelSmall
                                )
                            }
                        }

                        Button(
                            onClick = {
                                showBottomSheet = true
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RedMain
                            ),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddShoppingCart,
                                contentDescription = "add to cart"
                            )

                            Spacer(modifier = Modifier.width(DIMENS_2dp))

                            Text(
                                text = stringResource(id = R.string.add_to_cart),
                                style = Typography.labelSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductCardPreview() {
    ProductCard(
        product = ProductModelEntity(
            id = "11",
            productName = "روغن ذرت ۸۱۰ گرم زر",
            productCode = 659985,
            fullNameKala1 = "عدد",
            unit1 = "EA",
            unitid1 = "1",
            convertFactor1 = 1,
            fullNameKala2 = "کارتن",
            unit2 = "KAR",
            convertFactor2 = 12,
            unitid2 = "2",
            productGroupCode = 5,
            price = 98563,
            isDiscounts = true,
            productImage = ""
        ),
        order = null,
        discounts = emptyList(),
        isDiscountLoading = false,
        onSaveOrder = { _, _, _ -> },
        onDiscountClick = {},
        onClick = {}
    )
}