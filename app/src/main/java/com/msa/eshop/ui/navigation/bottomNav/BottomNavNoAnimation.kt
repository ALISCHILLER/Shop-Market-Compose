package com.msa.eshop.ui.navigation.bottomNav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.msa.eshop.R
import com.msa.eshop.ui.navigation.Route
import com.msa.eshop.ui.theme.EShopTheme

@Composable
fun BottomNavNoAnimation(
    currentRoute: String?,
    basketCount: Int,
    onClick: (String) -> Unit
) {
    val invoiceIcon = ImageVector.vectorResource(id = R.drawable.ic_invoice)
    val basketIcon = ImageVector.vectorResource(id = R.drawable.ic_basket)
    val profileIcon = ImageVector.vectorResource(id = R.drawable.ic_profile)

    val screens = remember(
        invoiceIcon,
        basketIcon,
        profileIcon
    ) {
        listOf(
            BottomNavScreen(
                title = "صفحه اصلی",
                activeIcon = Icons.Filled.Home,
                inactiveIcon = Icons.Outlined.Home,
                route = Route.HomeScreen.route
            ),
            BottomNavScreen(
                title = "گزارش خرید",
                activeIcon = invoiceIcon,
                inactiveIcon = invoiceIcon,
                route = Route.OrderStatusReportScreen.route
            ),
            BottomNavScreen(
                title = "سبد",
                activeIcon = basketIcon,
                inactiveIcon = basketIcon,
                route = Route.BasketScreen.route
            ),
            BottomNavScreen(
                title = "پروفایل",
                activeIcon = profileIcon,
                inactiveIcon = profileIcon,
                route = Route.ProfileScreen.route
            )
        )
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        NavigationBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            screens.forEach { screen ->
                val selected = screen.route == currentRoute
                val icon = if (selected) screen.activeIcon else screen.inactiveIcon

                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        if (!selected) {
                            onClick(screen.route)
                        }
                    },
                    icon = {
                        BottomNavIcon(
                            screen = screen,
                            icon = icon,
                            basketCount = basketCount
                        )
                    },
                    label = {
                        Text(
                            text = screen.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    alwaysShowLabel = true,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}

@Composable
private fun BottomNavIcon(
    screen: BottomNavScreen,
    icon: ImageVector,
    basketCount: Int
) {
    val showBadge = screen.route == Route.BasketScreen.route && basketCount > 0

    if (showBadge) {
        BadgedBox(
            badge = {
                Badge(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Text(
                        text = basketCount.toBadgeText(),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        ) {
            Icon(
                imageVector = icon,
                contentDescription = screen.title
            )
        }
    } else {
        Icon(
            imageVector = icon,
            contentDescription = screen.title
        )
    }
}

private fun Int.toBadgeText(): String {
    return if (this > 99) "99+" else toString()
}

@Stable
private data class BottomNavScreen(
    val title: String,
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector,
    val route: String
)

@Preview(showBackground = true)
@Composable
private fun BottomNavNoAnimationPreview() {
    EShopTheme {
        Box {
            BottomNavNoAnimation(
                currentRoute = Route.HomeScreen.route,
                basketCount = 3,
                onClick = {}
            )
        }
    }
}