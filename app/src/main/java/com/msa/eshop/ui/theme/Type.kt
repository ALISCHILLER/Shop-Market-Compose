package com.msa.eshop.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.msa.eshop.R

val EShopFontFamily = FontFamily(
    Font(R.font.iransans_medium, FontWeight.Normal),
    Font(R.font.iransans_medium, FontWeight.Medium),
    Font(R.font.vazirmatn_semibold, FontWeight.SemiBold),
    Font(R.font.kalameh_bold, FontWeight.Bold)
)

val iranianSansFont = Font(R.font.iransans_medium)

private val BaseTextStyle = TextStyle(
    fontFamily = EShopFontFamily,
    letterSpacing = 0.sp
)

val Typography = Typography(
    displayLarge = BaseTextStyle.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        lineHeight = 44.sp
    ),
    displayMedium = BaseTextStyle.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        lineHeight = 40.sp
    ),
    displaySmall = BaseTextStyle.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 36.sp
    ),

    headlineLarge = BaseTextStyle.copy(
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 34.sp
    ),
    headlineMedium = BaseTextStyle.copy(
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 32.sp
    ),
    headlineSmall = BaseTextStyle.copy(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 30.sp
    ),

    titleLarge = BaseTextStyle.copy(
        fontWeight = FontWeight.SemiBold,
        fontSize = 19.sp,
        lineHeight = 28.sp
    ),
    titleMedium = BaseTextStyle.copy(
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 25.sp
    ),
    titleSmall = BaseTextStyle.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 22.sp
    ),

    bodyLarge = BaseTextStyle.copy(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 27.sp
    ),
    bodyMedium = BaseTextStyle.copy(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 23.sp
    ),
    bodySmall = BaseTextStyle.copy(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 19.sp
    ),

    labelLarge = BaseTextStyle.copy(
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        lineHeight = 22.sp
    ),
    labelMedium = BaseTextStyle.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 20.sp
    ),
    labelSmall = BaseTextStyle.copy(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 18.sp
    )
)