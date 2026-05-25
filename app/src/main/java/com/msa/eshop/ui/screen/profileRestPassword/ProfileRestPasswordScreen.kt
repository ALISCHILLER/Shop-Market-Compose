package com.msa.eshop.ui.screen.profileRestPassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.msa.eshop.ui.component.dialog.ErrorDialog
import com.msa.eshop.ui.component.dialog.InfoDialog
import com.msa.eshop.ui.component.lottiefile.LoadingAnimate
import com.msa.eshop.ui.component.weightC.RoundedIconTextField

@Composable
fun ProfileRestPasswordScreen(
    modifier: Modifier = Modifier,
    viewModel: RestPasswordViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    uiState.errorMessage?.let { error ->
        ErrorDialog(
            error,
            { viewModel.clearState() },
            false
        )
    }

    uiState.message?.let { message ->
        InfoDialog(
            message,
            { viewModel.clearState() },
            false
        )
    }

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Card(
                modifier = Modifier
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
                        .imePadding()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "تغییر رمز عبور",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    RoundedIconTextField(
                        value = uiState.currentPassword,
                        onValueChange = viewModel::onCurrentPasswordChange,
                        label = "رمز عبور فعلی",
                        icon = Icons.Default.Lock,
                        isPassword = true
                    )

                    RoundedIconTextField(
                        value = uiState.newPassword,
                        onValueChange = viewModel::onNewPasswordChange,
                        label = "رمز عبور جدید",
                        icon = Icons.Default.Lock,
                        isPassword = true
                    )

                    RoundedIconTextField(
                        value = uiState.repeatNewPassword,
                        onValueChange = viewModel::onRepeatNewPasswordChange,
                        label = "تکرار رمز عبور جدید",
                        icon = Icons.Default.Lock,
                        isPassword = true
                    )

                    Button(
                        onClick = viewModel::submit,
                        enabled = uiState.canSubmit,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "ثبت تغییرات",
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }
            }
        }

        if (uiState.isLoading) {
            LoadingAnimate()
        }
    }
}