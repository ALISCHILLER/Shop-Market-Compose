package com.msa.eshop.ui.common.card

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.msa.eshop.R
import com.msa.eshop.data.local.entity.ProductGroupEntity
import com.msa.eshop.ui.theme.Typography
import com.msa.eshop.ui.theme.barcolorlight

@Composable
fun ProductGroupCard(
    productGroupEntity: ProductGroupEntity,
    onClick: (ProductGroupEntity) -> Unit,
    isSelected: Boolean
) {
    val backgroundColor = if (isSelected) Color.Red else barcolorlight
    val contentColor = if (isSelected) Color.White else Color.Red

    val iconUrl = if (isSelected) {
        productGroupEntity.productCategoryImageUnselect
            ?: productGroupEntity.productCategoryImage
    } else {
        productGroupEntity.productCategoryImage
            ?: productGroupEntity.productCategoryImageUnselect
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = Modifier
                .width(72.dp)
                .clickable {
                    onClick(productGroupEntity)
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier.size(54.dp),
                color = backgroundColor
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (iconUrl.isNullOrBlank()) {
                        Text(
                            text = productGroupEntity.productCategoryName ?: "همه",
                            color = contentColor,
                            style = Typography.labelSmall,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                    } else {
                        AsyncImage(
                            model = iconUrl,
                            contentDescription = productGroupEntity.productCategoryName,
                            modifier = Modifier.size(30.dp),
                            error = painterResource(id = R.drawable.not_load_image)
                        )
                    }
                }
            }

            Text(
                text = productGroupEntity.productCategoryName ?: "",
                color = contentColor,
                style = Typography.labelSmall,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductGroupCardPreview() {
    ProductGroupCard(
        productGroupEntity = ProductGroupEntity(
            productCategoryCode = 1,
            productCategoryName = "آرد",
            productCategoryImage = "",
            productCategoryImageUnselect = ""
        ),
        onClick = {},
        isSelected = false
    )
}