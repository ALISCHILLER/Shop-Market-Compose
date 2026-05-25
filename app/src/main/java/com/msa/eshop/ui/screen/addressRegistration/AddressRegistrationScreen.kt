package com.msa.eshop.ui.screen.addressRegistration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.msa.eshop.R
import com.msa.eshop.ui.common.topBar.TopBarDetails
import com.msa.eshop.ui.component.dialog.ErrorDialog
import com.msa.eshop.ui.theme.PlatinumSilver

@Composable
fun AddressRegistrationScreen(
    modifier: Modifier = Modifier,
    viewModel: RegistrationViewModel = hiltViewModel()
) {
    var addressText by rememberSaveable { mutableStateOf("") }
    var phoneText by rememberSaveable { mutableStateOf("") }
    var titleText by rememberSaveable { mutableStateOf("") }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    errorMessage?.let { error ->
        ErrorDialog(
            error,
            { errorMessage = null },
            false
        )
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(PlatinumSilver),
        containerColor = PlatinumSilver,
        topBar = {
            TopBarDetails("آدرس جدید")
        },
        bottomBar = {
            AddressRegistrationBottomBar(
                onRegisterClick = {
                    val validationError = validateAddressForm(
                        title = titleText,
                        address = addressText,
                        phone = phoneText
                    )

                    if (validationError != null) {
                        errorMessage = validationError
                    } else {
                        /*
                         * اینجا آماده اتصال به API/Repository است.
                         * وقتی متد ذخیره آدرس در Repository داشتی، اینجا باید به ViewModel وصل شود.
                         */
                        errorMessage = "ثبت آدرس هنوز به سرویس ذخیره‌سازی متصل نشده است"
                    }
                },
                onMapClick = {
                    viewModel.navigateToLocationRegistration()
                }
            )
        }
    ) { innerPadding ->
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "مشخصات آدرس",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        AddressInputField(
                            value = titleText,
                            onValueChange = { titleText = it },
                            label = "عنوان آدرس",
                            placeholder = "مثلاً خانه، محل کار، فروشگاه",
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Home,
                                    contentDescription = null
                                )
                            }
                        )

                        AddressInputField(
                            value = phoneText,
                            onValueChange = { phoneText = it.filter(Char::isDigit) },
                            label = "شماره تماس",
                            placeholder = "شماره تماس گیرنده",
                            keyboardType = KeyboardType.Phone,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Phone,
                                    contentDescription = null
                                )
                            }
                        )

                        AddressInputField(
                            value = addressText,
                            onValueChange = { addressText = it },
                            label = "متن کامل آدرس",
                            placeholder = "استان، شهر، خیابان، پلاک، واحد و توضیحات تکمیلی",
                            minLines = 5,
                            maxLines = 8,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null
                                )
                            }
                        )

                        Text(
                            text = "برای دقت بیشتر می‌توانید موقعیت مکانی را از روی نقشه انتخاب کنید.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddressInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    minLines: Int = 1,
    maxLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(text = label) },
        placeholder = { Text(text = placeholder) },
        leadingIcon = leadingIcon,
        minLines = minLines,
        maxLines = maxLines,
        textStyle = LocalTextStyle.current.copy(
            textAlign = TextAlign.Start
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun AddressRegistrationBottomBar(
    onRegisterClick: () -> Unit,
    onMapClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .navigationBarsPadding()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outline)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onRegisterClick,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.address_registration),
                    style = MaterialTheme.typography.titleSmall
                )
            }

            OutlinedButton(
                onClick = onMapClick,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = stringResource(id = R.string.map),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

private fun validateAddressForm(
    title: String,
    address: String,
    phone: String
): String? {
    return when {
        title.trim().length < 2 -> "عنوان آدرس را وارد کنید"
        phone.trim().length < 8 -> "شماره تماس معتبر نیست"
        address.trim().length < 10 -> "آدرس کامل را وارد کنید"
        else -> null
    }
}