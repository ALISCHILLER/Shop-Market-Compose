@file:OptIn(ExperimentalAnimationApi::class)

package com.msa.eshop.ui.navigation.bottomNav

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.msa.eshop.R
import com.msa.eshop.ui.acticity.MainViewModel
import com.msa.eshop.ui.navigation.Route
import com.msa.eshop.ui.theme.Typography
import com.msa.eshop.ui.theme.barcolorDark

@Preview
@Composable
fun BottomNavNoAnimationPreview() {
    Scaffold(
        bottomBar = {
            BottomNavNoAnimation(
                currentRoute = "",
                onClick = {}
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .background(Color.Red)
        ) {}
    }
}

@ExperimentalAnimationApi
@Composable
fun BottomNavNoAnimation(
    currentRoute: String?,
    onClick: (String) -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val allOrder by viewModel.allOrder.collectAsState()
    val basketCount = allOrder.size

    val screens = listOf(
        Screen(
            title = "صفحه اصلی",
            activeIcon = Icons.Filled.Home,
            inactiveIcon = Icons.Outlined.Home,
            route = Route.HomeScreen.route
        ),
        Screen(
            title = "گزارش خرید",
            activeIcon = ImageVector.vectorResource(R.drawable.ic_invoice),
            inactiveIcon = ImageVector.vectorResource(R.drawable.ic_invoice),
            route = Route.OrderStatusReportScreen.route
        ),
        Screen(
            title = "سبد",
            activeIcon = ImageVector.vectorResource(R.drawable.ic_basket),
            inactiveIcon = ImageVector.vectorResource(R.drawable.ic_basket),
            route = Route.BasketScreen.route
        ),
        Screen(
            title = "پروفایل",
            activeIcon = ImageVector.vectorResource(R.drawable.ic_profile),
            inactiveIcon = ImageVector.vectorResource(R.drawable.ic_profile),
            route = Route.ProfileScreen.route
        )
    )

    Box(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 5.dp)
            .shadow(5.dp, shape = RoundedCornerShape(10.dp))
            .background(color = colors.surface)
            .height(64.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            screens.forEach { screen ->
                val isSelected = screen.route == currentRoute
                val animatedWeight by animateFloatAsState(
                    targetValue = if (isSelected) 1.5f else 1f
                )

                Box(
                    modifier = Modifier.weight(animatedWeight),
                    contentAlignment = Alignment.Center
                ) {
                    val interactionSource = remember {
                        MutableInteractionSource()
                    }

                    BottomNavItem(
                        modifier = Modifier.clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            onClick(screen.route)
                        },
                        item = screen,
                        isSelected = isSelected,
                        basketCount = basketCount
                    )
                }
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
private fun BottomNavItem(
    modifier: Modifier = Modifier,
    item: Screen,
    isSelected: Boolean,
    basketCount: Int
) {
    val animatedHeight by animateDpAsState(
        targetValue = if (isSelected) 36.dp else 26.dp
    )

    val animatedElevation by animateDpAsState(
        targetValue = if (isSelected) 15.dp else 0.dp
    )

    val animatedAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else .5f
    )

    val animatedIconSize by animateDpAsState(
        targetValue = if (isSelected) 26.dp else 20.dp,
        animationSpec = spring(
            stiffness = Spring.StiffnessLow,
            dampingRatio = Spring.DampingRatioMediumBouncy
        )
    )

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .height(animatedHeight)
                .shadow(
                    elevation = animatedElevation,
                    shape = RoundedCornerShape(20.dp)
                )
                .background(
                    color = if (isSelected) Color.Red else colors.surface,
                    shape = RoundedCornerShape(22.dp)
                )
                .padding(top = 1.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                FlipIcon(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(horizontal = 1.dp, vertical = 1.dp)
                        .fillMaxHeight()
                        .alpha(animatedAlpha)
                        .size(animatedIconSize),
                    isActive = isSelected,
                    activeIcon = item.activeIcon,
                    inactiveIcon = item.inactiveIcon,
                    contentDescription = item.title
                )

                AnimatedVisibility(visible = isSelected) {
                    Text(
                        text = item.title,
                        modifier = Modifier.padding(start = 1.dp, end = 1.dp),
                        maxLines = 1,
                        color = Color.White,
                        style = Typography.labelSmall
                    )
                }
            }

            if (item.route == Route.BasketScreen.route && basketCount > 0) {
                val infiniteTransition = rememberInfiniteTransition()
                val textSize by infiniteTransition.animateFloat(
                    initialValue = 10f,
                    targetValue = 16f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 2000),
                        repeatMode = RepeatMode.Reverse
                    )
                )

                Text(
                    text = basketCount.toString(),
                    fontSize = textSize.sp,
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .align(Alignment.TopStart)
                        .background(
                            color = Color.Red,
                            shape = CircleShape
                        )
                        .padding(horizontal = 5.dp),
                    color = Color.White,
                    style = Typography.labelSmall
                )
            }
        }
    }
}

@Composable
fun FlipIcon(
    modifier: Modifier = Modifier,
    isActive: Boolean,
    activeIcon: ImageVector,
    inactiveIcon: ImageVector,
    contentDescription: String
) {
    val animationRotation by animateFloatAsState(
        targetValue = if (isActive) 180f else 0f,
        animationSpec = spring(
            stiffness = Spring.StiffnessLow,
            dampingRatio = Spring.DampingRatioMediumBouncy
        )
    )

    Box(
        modifier = modifier.graphicsLayer {
            rotationY = animationRotation
        },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = rememberVectorPainter(
                image = if (animationRotation > 90f) activeIcon else inactiveIcon
            ),
            contentDescription = contentDescription,
            tint = if (isActive) Color.White else barcolorDark
        )
    }
}

data class Screen(
    val title: String,
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector,
    val route: String
)