package com.msa.eshop.ui.screen.accountInformation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AccountInformationScreen(
    modifier: Modifier = Modifier,
    viewModel: AccountInformationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    AccountInformationContent(
        modifier = modifier,
        uiState = uiState
    )
}

@Composable
private fun AccountInformationContent(
    modifier: Modifier = Modifier,
    uiState: AccountInformationUiState
) {
    val user = uiState.user

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "اطلاعات حساب",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                AccountInfoField(
                    value = user?.customerName.orEmpty(),
                    label = "نام و نام خانوادگی",
                    icon = Icons.Outlined.Person
                )

                AccountInfoField(
                    value = user?.mobile.orEmpty(),
                    label = "شماره تماس",
                    icon = Icons.Outlined.Phone
                )

                AccountInfoField(
                    value = user?.center.orEmpty(),
                    label = "شعبه پخش",
                    icon = Icons.Outlined.Business
                )

                AccountInfoField(
                    value = user?.center.orEmpty(),
                    label = "شهر",
                    icon = Icons.Outlined.LocationOn
                )
            }
        }
    }
}

@Composable
private fun AccountInfoField(
    value: String,
    label: String,
    icon: ImageVector
) {
    OutlinedTextField(
        value = value.ifBlank { "-" },
        onValueChange = {},
        modifier = Modifier.fillMaxWidth(),
        readOnly = true,
        enabled = false,
        singleLine = true,
        label = {
            Text(text = label)
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null
            )
        },
        textStyle = LocalTextStyle.current.copy(
            textAlign = TextAlign.Start
        ),
        shape = MaterialTheme.shapes.large,
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledLeadingIconColor = MaterialTheme.colorScheme.primary,
            disabledBorderColor = MaterialTheme.colorScheme.outline,
            disabledContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}