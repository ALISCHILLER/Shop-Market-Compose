package com.msa.eshop.ui.screen.login

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.msa.eshop.R
import com.msa.eshop.ui.component.dialog.ErrorDialog
import com.msa.eshop.ui.component.drawLineC.BezierCurve
import com.msa.eshop.ui.component.drawLineC.BezierCurveStyle
import com.msa.eshop.ui.component.lottiefile.LoadingAnimate
import com.msa.eshop.ui.theme.EShopTheme
import com.msa.eshop.ui.theme.PlatinumSilver
import com.msa.eshop.ui.theme.RedMain

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity
    val fragmentActivity = context as? FragmentActivity
    val focusManager = LocalFocusManager.current

    BackHandler {
        activity?.finish()
    }

    uiState.errorMessage?.let { error ->
        ErrorDialog(
            error,
            { viewModel.clearError() },
            false
        )
    }

    LoginContent(
        modifier = modifier,
        uiState = uiState,
        onUsernameChange = viewModel::onUsernameChange,
        onPasswordChange = viewModel::onPasswordChange,
        onRememberMeChange = viewModel::onRememberMeChange,
        onPasswordVisibilityChange = viewModel::togglePasswordVisibility,
        onLoginClick = {
            focusManager.clearFocus()
            viewModel.login()
        },
        onBiometricClick = {
            focusManager.clearFocus()
            fragmentActivity?.let(viewModel::biometricDialog)
        }
    )

    if (uiState.isLoading) {
        LoadingAnimate()
    }
}

@Composable
private fun LoginContent(
    modifier: Modifier = Modifier,
    uiState: LoginUiState,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRememberMeChange: (Boolean) -> Unit,
    onPasswordVisibilityChange: () -> Unit,
    onLoginClick: () -> Unit,
    onBiometricClick: () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(PlatinumSilver)
        ) {
            LoginDecorations()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(28.dp))

                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "لوگو",
                    modifier = Modifier.size(132.dp)
                )

                Spacer(modifier = Modifier.height(22.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "ورود به حساب کاربری",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "برای ادامه، کد مشتری و رمز عبور خود را وارد کنید.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        LoginTextField(
                            value = uiState.username,
                            onValueChange = onUsernameChange,
                            label = "کد مشتری",
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null
                                )
                            },
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        )

                        LoginTextField(
                            value = uiState.password,
                            onValueChange = onPasswordChange,
                            label = "رمز عبور",
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null
                                )
                            },
                            trailingIcon = {
                                TextButton(
                                    onClick = onPasswordVisibilityChange
                                ) {
                                    Text(
                                        text = if (uiState.isPasswordVisible) "مخفی" else "نمایش",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            },
                            visualTransformation = if (uiState.isPasswordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done,
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    onLoginClick()
                                }
                            )
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onRememberMeChange(!uiState.rememberMe) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = uiState.rememberMe,
                                onCheckedChange = onRememberMeChange,
                                colors = CheckboxDefaults.colors(
                                    checkedColor = MaterialTheme.colorScheme.primary
                                )
                            )

                            Text(
                                text = "مرا به خاطر بسپار",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Text(
                                text = "فراموشی رمز عبور",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Button(
                            onClick = onLoginClick,
                            enabled = uiState.canSubmit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RedMain,
                                disabledContainerColor = RedMain.copy(alpha = 0.42f)
                            )
                        ) {
                            Text(
                                text = stringResource(id = R.string.login),
                                style = MaterialTheme.typography.titleSmall
                            )
                        }

                        BiometricLoginButton(
                            enabled = uiState.canUseBiometric,
                            onClick = onBiometricClick
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: (@Composable () -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = {
            Text(text = label)
        },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        keyboardActions = keyboardActions,
        textStyle = LocalTextStyle.current.copy(
            textAlign = TextAlign.Start
        ),
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
private fun BiometricLoginButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(58.dp)
            .clickable(enabled = enabled) { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (enabled) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        }
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Fingerprint,
                contentDescription = "ورود با اثر انگشت",
                tint = if (enabled) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
                },
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun LoginDecorations() {
    val strokeWidth = 1.dp

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        BezierCurve(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .rotate(180f)
                .align(Alignment.TopCenter),
            points = listOf(30F, 60F, 40f, 100F, 50F),
            minPoint = 0F,
            maxPoint = 100F,
            style = BezierCurveStyle.StrokeAndFill(
                strokeBrush = Brush.horizontalGradient(
                    listOf(
                        Color(0xFFE3152F),
                        Color(0xFFE3152F)
                    )
                ),
                fillBrush = Brush.verticalGradient(
                    listOf(
                        Color(0xFFB9081F),
                        Color(0xFFE3152F)
                    )
                ),
                stroke = Stroke(width = strokeWidth.value)
            )
        )

        BezierCurve(
            modifier = Modifier
                .size(width = 220.dp, height = 220.dp)
                .rotate(90f)
                .align(Alignment.BottomEnd),
            points = listOf(0f, 40f, 30F, 80f),
            minPoint = 0F,
            maxPoint = 100F,
            style = BezierCurveStyle.StrokeAndFill(
                strokeBrush = Brush.horizontalGradient(
                    listOf(
                        Color(0xFFE3152F),
                        Color(0xFFE3152F)
                    )
                ),
                fillBrush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFB9081F),
                        Color(0xFFE3152F)
                    ),
                    start = Offset.Zero,
                    end = Offset.Infinite
                ),
                stroke = Stroke(width = strokeWidth.value)
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginContentPreview() {
    EShopTheme {
        LoginContent(
            uiState = LoginUiState(
                username = "1001",
                password = "123456",
                rememberMe = true,
                canUseBiometric = true
            ),
            onUsernameChange = {},
            onPasswordChange = {},
            onRememberMeChange = {},
            onPasswordVisibilityChange = {},
            onLoginClick = {},
            onBiometricClick = {}
        )
    }
}