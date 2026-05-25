package com.msa.eshop.ui.screen.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.msa.eshop.R
import com.msa.eshop.ui.common.card.ProfileCard
import com.msa.eshop.ui.common.topBar.TopBarDetails
import com.msa.eshop.ui.component.dialog.AlertDialogConfirm
import com.msa.eshop.ui.component.expandable.ExpandableCard
import com.msa.eshop.ui.screen.accountInformation.AccountInformationScreen
import com.msa.eshop.ui.screen.profileRestPassword.ProfileRestPasswordScreen
import com.msa.eshop.ui.theme.PlatinumSilver

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    if (uiState.showLogoutConfirm) {
        AlertDialogConfirm(
            onConfirmation = viewModel::logout,
            onDismissRequest = viewModel::dismissLogoutDialog,
            message = "آیا می‌خواهید از حساب کاربری خارج شوید؟"
        )
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(PlatinumSilver),
        containerColor = PlatinumSilver,
        topBar = {
            TopBarDetails("پروفایل")
        }
    ) { innerPadding ->
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProfileHeader(
                    uiState = uiState,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
                )

                ExpandableCard(
                    title = "اطلاعات حساب کاربری",
                    painter = painterResource(id = R.drawable.ic_profile)
                ) {
                    AccountInformationScreen()
                }

                ExpandableCard(
                    title = "تغییر رمز عبور",
                    painter = painterResource(id = R.drawable.ic_key)
                ) {
                    ProfileRestPasswordScreen()
                }

                ProfileCard(
                    title = "آدرس‌ها",
                    painter = painterResource(id = R.drawable.ic_location),
                    onClick = viewModel::navigateToAddressRegistration
                )

                ProfileCard(
                    title = "خروج از حساب کاربری",
                    painter = painterResource(id = R.drawable.ic_exite),
                    onClick = viewModel::showLogoutDialog
                )
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    uiState: ProfileUiState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.iprofile),
                contentDescription = "تصویر پروفایل",
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = uiState.customerName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "کد مشتری: ${uiState.customerCode}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}