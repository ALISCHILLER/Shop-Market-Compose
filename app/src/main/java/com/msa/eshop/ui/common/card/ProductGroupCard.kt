package com.msa.eshop.ui.common.card

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.msa.eshop.R
import com.msa.eshop.data.local.entity.ProductGroupEntity

@Composable
fun ProductGroupCard(
    productGroupEntity: ProductGroupEntity,
    onClick: (ProductGroupEntity) -> Unit,
    isSelected: Boolean
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.primary
    }

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
                .width(76.dp)
                .clickable { onClick(productGroupEntity) },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                shape = CircleShape,
                modifier = Modifier.size(56.dp),
                color = backgroundColor,
                tonalElevation = if (isSelected) 4.dp else 0.dp
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (iconUrl.isNullOrBlank()) {
                        Text(
                            text = productGroupEntity.productCategoryName ?: "همه",
                            color = contentColor,
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else {
                        AsyncImage(
                            model = iconUrl,
                            contentDescription = productGroupEntity.productCategoryName,
                            modifier = Modifier.size(30.dp),
                            error = painterResource(id = R.drawable.not_load_image),
                            placeholder = painterResource(id = R.drawable.not_load_image)
                        )
                    }
                }
            }

            Text(
                text = productGroupEntity.productCategoryName ?: "",
                color = contentColor,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}