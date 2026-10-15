package com.ohieku.newsapp.ui.compose.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ohieku.newsapp.R

val Archivo = FontFamily(
    Font(R.font.archivo_regular, FontWeight.Normal),
    Font(R.font.archivo_medium, FontWeight.Medium),
    Font(R.font.archivo_semibold, FontWeight.SemiBold),
    Font(R.font.archivo_extrabold, FontWeight.ExtraBold),
)

private val Default = Typography()

private fun TextStyle.archivo(weight: FontWeight, size: Int, lineHeight: Int, tracking: Double = 0.0) = copy(
    fontFamily = Archivo,
    fontWeight = weight,
    fontSize = size.sp,
    lineHeight = lineHeight.sp,
    letterSpacing = tracking.sp,
)

// Broadcast voice: ExtraBold headlines, SemiBold rows and labels, Regular body.
val Typography = Typography(
    displaySmall = Default.displaySmall.archivo(FontWeight.ExtraBold, 36, 44),
    headlineMedium = Default.headlineMedium.archivo(FontWeight.ExtraBold, 28, 34),
    // Lead story headline
    headlineSmall = Default.headlineSmall.archivo(FontWeight.ExtraBold, 24, 29, -0.2),
    // Top bar wordmark and screen titles
    titleLarge = Default.titleLarge.archivo(FontWeight.ExtraBold, 18, 24, 0.4),
    // Running-order headline
    titleMedium = Default.titleMedium.archivo(FontWeight.SemiBold, 15, 20),
    // Tabs and the time column
    titleSmall = Default.titleSmall.archivo(FontWeight.SemiBold, 14, 18, 0.1),
    bodyLarge = Default.bodyLarge.archivo(FontWeight.Normal, 16, 24),
    bodyMedium = Default.bodyMedium.archivo(FontWeight.Normal, 14, 20),
    bodySmall = Default.bodySmall.archivo(FontWeight.Normal, 12, 16),
    labelLarge = Default.labelLarge.archivo(FontWeight.SemiBold, 14, 20, 0.1),
    // Uppercase source and author lines
    labelMedium = Default.labelMedium.archivo(FontWeight.SemiBold, 12, 16, 0.6),
    // Source tag and time bug
    labelSmall = Default.labelSmall.archivo(FontWeight.ExtraBold, 11, 14, 0.9),
)
