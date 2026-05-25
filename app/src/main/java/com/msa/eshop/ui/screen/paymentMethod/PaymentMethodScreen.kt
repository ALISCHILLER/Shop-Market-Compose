package com.msa.eshop.ui.screen.paymentMethod

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.msa.eshop.R
import com.msa.eshop.ui.common.topBar.TopBarDetails
import com.msa.eshop.ui.component.dialog.ErrorDialog
import com.msa.eshop.ui.component.lottiefile.LoadingAnimate
import com.msa.eshop.ui.component.radioC.RadioGroupColumn
import com.msa.eshop.ui.theme.PlatinumSilver

@Composable
fun PaymentMethodScreen(
    modifier: Modifier = Modifier,
    viewModel: PaymentMethodViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    uiState.errorMessage?.let { error ->
        ErrorDialog(
            error,
            { viewModel.clearError() },
            false
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PlatinumSilver)
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = PlatinumSilver,
            topBar = {
                TopBarDetails(stringResource(id = R.string.payment_method))
            },
            bottomBar = {
                PaymentBottomBar(
                    enabled = uiState.canSubmit,
                    onSubmit = viewModel::submitPayment
                )
            }
        ) { innerPadding ->
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                PaymentMethodContent(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    uiState = uiState,
                    onPaymentSelected = viewModel::onPaymentSelected,
                    onReceiveSelected = viewModel::onReceiveSelected
                )
            }
        }

        if (uiState.isLoading) {
            LoadingAnimate()
        }
    }
}

@Composable
private fun PaymentMethodContent(
    modifier: Modifier = Modifier,
    uiState: PaymentMethodUiState,
    onPaymentSelected: (String) -> Unit,
    onReceiveSelected: (String) -> Unit
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AddressSection(uiState = uiState)

        InfoSection {
            InfoRow(
                title = "تاریخ ارسال",
                message = "1403/02/03"
            )

            InfoRow(
                title = "تعداد کل سفارش",
                message = uiState.totalItems.toString()
            )
        }

        SelectionSection(
            title = "روش محاسبه مبلغ",
            options = uiState.paymentOptions,
            selectedOption = uiState.paymentOptions.firstOrNull {
                it.extractPaymentTitle() == uiState.selectedPaymentTitle
            }.orEmpty(),
            onOptionSelected = onPaymentSelected,
            emptyMessage = "روش پرداختی برای نمایش وجود ندارد"
        )

        SelectionSection(
            title = "نوع پرداخت",
            options = uiState.receiveOptions,
            selectedOption = uiState.selectedReceiveTitle,
            onOptionSelected = onReceiveSelected,
            emptyMessage = "نوع پرداختی برای نمایش وجود ندارد"
        )
    }
}

@Composable
private fun AddressSection(
    uiState: PaymentMethodUiState
) {
    InfoSection(
        title = "آدرس تحویل"
    ) {
        val address = uiState.selectedAddress

        if (address == null) {
            Text(
                text = "آدرسی انتخاب نشده است",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            InfoRow(
                title = "آدرس",
                message = address.customerAddress.orEmpty().ifBlank { "-" }
            )

            InfoRow(
                title = "شماره تماس",
                message = address.customerMobile.orEmpty().ifBlank { "-" }
            )

            if (!address.centerName.isNullOrBlank()) {
                InfoRow(
                    title = "مرکز",
                    message = address.centerName
                )
            }
        }
    }
}

@Composable
private fun SelectionSection(
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    emptyMessage: String
) {
    InfoSection(
        title = title
    ) {
        if (options.isEmpty()) {
            Text(
                text = emptyMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            RadioGroupColumn(
                options = options,
                selectedOption = selectedOption,
                onOptionSelected = onOptionSelected
            )
        }
    }
}

@Composable
private fun InfoSection(
    title: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (!title.isNullOrBlank()) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            }

            content()
        }
    }
}

@Composable
private fun PaymentBottomBar(
    enabled: Boolean,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)

        Button(
            onClick = onSubmit,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.the_payment),
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}

@Composable
fun RowText(
    modifier: Modifier = Modifier,
    title: String,
    message: String
) {
    InfoRow(
        modifier = modifier,
        title = title,
        message = message
    )
}

@Composable
private fun InfoRow(
    modifier: Modifier = Modifier,
    title: String,
    message: String
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "$title:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }
}