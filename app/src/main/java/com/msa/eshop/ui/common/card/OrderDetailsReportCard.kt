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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.msa.eshop.R
import com.msa.eshop.data.Model.response.ReportCartDetailsModel
import com.msa.eshop.utils.Currency

@Composable
fun OrderDetailsReportCard(
    modifier: Modifier = Modifier,
    reportCart: ReportCartDetailsModel
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(18.dp)
                            )
                            .aspectRatio(1f)
                    ) {
                        AsyncImage(
                            model = reportCart.productImageUrl,
                            contentDescription = reportCart.productName,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit,
                            error = painterResource(id = R.drawable.not_load_image),
                            placeholder = painterResource(id = R.drawable.not_load_image)
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = reportCart.productName,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = "کد کالا: ${reportCart.productCode}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline)

                DetailRow(
                    title = "تعداد سفارش",
                    value = reportCart.quantity.toString()
                )

                DetailRow(
                    title = "قیمت واحد",
                    value = "${Currency(reportCart.price).toFormattedString()} ریال"
                )

                if (reportCart.discount > 0) {
                    DetailRow(
                        title = "تخفیف",
                        value = "${Currency(reportCart.discount).toFormattedString()} ریال"
                    )
                }

                if (reportCart.tax > 0) {
                    DetailRow(
                        title = "مالیات",
                        value = "${Currency(reportCart.tax).toFormattedString()} ریال"
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline)

                DetailRow(
                    title = "جمع کل",
                    value = "${Currency(reportCart.total).toFormattedString()} ریال",
                    highlight = true
                )
            }
        }
    }
}

@Composable
private fun DetailRow(
    title: String,
    value: String,
    highlight: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = value,
            style = if (highlight) {
                MaterialTheme.typography.titleMedium
            } else {
                MaterialTheme.typography.bodyMedium
            },
            color = if (highlight) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
}