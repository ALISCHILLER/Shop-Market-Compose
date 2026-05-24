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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.msa.eshop.R
import com.msa.eshop.data.Model.response.DiscountResultModel
import com.msa.eshop.data.local.entity.ProductModelEntity
import com.msa.eshop.ui.theme.PlatinumSilver
import com.msa.eshop.ui.theme.RedMain
import com.msa.eshop.ui.theme.Typography
import com.msa.eshop.ui.theme.barcolorlight

@Composable
fun DiscountsProductCard(
    modifier: Modifier = Modifier,
    product: ProductModelEntity,
    discounts: List<DiscountResultModel>,
    isLoading: Boolean,
    onDismissRequest: () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = modifier
                .background(Color.White)
                .padding(17.dp)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "در حال دریافت تخفیف‌ها...",
                            style = Typography.labelSmall
                        )
                    }
                }

                discounts.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "تخفیفی برای این کالا ثبت نشده است",
                            style = Typography.labelSmall
                        )
                    }
                }

                else -> {
                    LazyColumn {
                        itemsIndexed(discounts) { _, item ->
                            Column {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            vertical = 5.dp,
                                            horizontal = 20.dp
                                        ),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .padding(5.dp)
                                            .size(50.dp, 50.dp)
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

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(
                                        text = stringResource(
                                            id = R.string.discounts_list_data,
                                            item.fromNumber,
                                            item.endNumber
                                        ),
                                        modifier = Modifier.padding(end = 5.dp),
                                        style = Typography.labelSmall
                                    )

                                    Text(
                                        text = "${item.discountPercent}%",
                                        modifier = Modifier.padding(end = 5.dp),
                                        style = Typography.labelSmall,
                                        color = RedMain
                                    )

                                    Text(
                                        text = "تخفیف",
                                        modifier = Modifier.padding(end = 5.dp),
                                        style = Typography.labelSmall
                                    )
                                }

                                HorizontalDivider(
                                    color = barcolorlight,
                                    thickness = 1.dp,
                                    modifier = Modifier.padding(vertical = 9.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}