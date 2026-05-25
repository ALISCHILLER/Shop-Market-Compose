package com.msa.eshop.ui.screen.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.msa.eshop.R
import com.msa.eshop.ui.theme.EShopRed
import com.msa.eshop.ui.theme.EShopRedDark
import com.msa.eshop.ui.theme.EShopRedDarker
import com.msa.eshop.ui.theme.EShopTheme

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.startSplashFlow()
    }

    SplashContent()
}

@Composable
private fun SplashContent(
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition()

    val logoScale by transition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.045f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    val glowAlpha by transition.animateFloat(
        initialValue = 0.16f,
        targetValue = 0.38f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1500,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    val logoOffsetY by transition.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1600,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(splashBackgroundBrush()),
            contentAlignment = Alignment.Center
        ) {
            SplashBackgroundDecorations(
                glowAlpha = glowAlpha
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(0.75f))

                BrandLogoCard(
                    logoScale = logoScale,
                    glowAlpha = glowAlpha,
                    logoOffsetY = logoOffsetY
                )

                Spacer(modifier = Modifier.size(28.dp))

                Text(
                    text = "زر مارکت",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.22f),
                            blurRadius = 10f
                        )
                    ),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "خرید سریع، ساده و مطمئن",
                    modifier = Modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.84f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.weight(1f))

                LoadingSection()

                Text(
                    text = "در حال آماده‌سازی فروشگاه...",
                    modifier = Modifier.padding(top = 14.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.78f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun BrandLogoCard(
    logoScale: Float,
    glowAlpha: Float,
    logoOffsetY: Float
) {
    Box(
        modifier = Modifier.size(238.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .size(226.dp)
                .alpha(glowAlpha),
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.32f)
        ) {}

        Surface(
            modifier = Modifier
                .size(188.dp)
                .alpha(0.16f),
            shape = CircleShape,
            color = Color.White
        ) {}

        Surface(
            modifier = Modifier
                .size(166.dp)
                .offset(y = logoOffsetY.dp)
                .scale(logoScale),
            shape = RoundedCornerShape(42.dp),
            color = Color.White,
            tonalElevation = 10.dp,
            shadowElevation = 18.dp
        ) {
            Box(
                modifier = Modifier.padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.zar_market),
                    contentDescription = "زر مارکت",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun LoadingSection() {
    Surface(
        modifier = Modifier.size(width = 180.dp, height = 6.dp),
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.18f)
    ) {
        LinearProgressIndicator(
            modifier = Modifier.fillMaxSize(),
            color = Color.White,
            trackColor = Color.Transparent
        )
    }
}

@Composable
private fun SplashBackgroundDecorations(
    glowAlpha: Float
) {
    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        drawSoftCircle(
            color = Color.White.copy(alpha = 0.10f),
            radius = size.minDimension * 0.36f,
            center = Offset(
                x = size.width * 0.10f,
                y = size.height * 0.14f
            )
        )

        drawSoftCircle(
            color = Color.White.copy(alpha = 0.08f),
            radius = size.minDimension * 0.42f,
            center = Offset(
                x = size.width * 0.96f,
                y = size.height * 0.82f
            )
        )

        drawCircle(
            color = Color.White.copy(alpha = glowAlpha * 0.32f),
            radius = size.minDimension * 0.29f,
            center = Offset(
                x = size.width * 0.50f,
                y = size.height * 0.43f
            ),
            style = Stroke(width = 1.5.dp.toPx())
        )
    }
}

private fun DrawScope.drawSoftCircle(
    color: Color,
    radius: Float,
    center: Offset
) {
    drawCircle(
        color = color,
        radius = radius,
        center = center
    )
}

private fun splashBackgroundBrush(): Brush {
    return Brush.linearGradient(
        colors = listOf(
            EShopRedDarker,
            EShopRedDark,
            EShopRed,
            Color(0xFFFF4056)
        ),
        start = Offset.Zero,
        end = Offset.Infinite
    )
}

@Preview(showBackground = true)
@Composable
private fun SplashContentPreview() {
    EShopTheme(
        darkTheme = false
    ) {
        SplashContent()
    }
}