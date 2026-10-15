package com.ohieku.newsapp.ui.compose.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/** Broadcast roles Material's scheme has no slot for. */
@Immutable
data class NewsColors(
    val chrome: Color,
    val onChrome: Color,
    val onChromeMuted: Color,
    // Brand red as a fill (source tag, tab indicator); identical in both themes
    val brandFill: Color,
    val onBrandFill: Color,
    val hairline: Color,
    val imageWell: Color,
)

private val LightNewsColors = NewsColors(
    chrome = Ink,
    onChrome = OnChrome,
    onChromeMuted = OnChromeMuted,
    brandFill = BrandRed,
    onBrandFill = OnChrome,
    hairline = Hairline,
    imageWell = ImageWell,
)

private val DarkNewsColors = NewsColors(
    chrome = ChromeDark,
    onChrome = OnChrome,
    onChromeMuted = OnChromeMuted,
    brandFill = BrandRed,
    onBrandFill = OnChrome,
    hairline = HairlineDark,
    imageWell = ImageWellDark,
)

val LocalNewsColors = staticCompositionLocalOf { LightNewsColors }

private val LightColorScheme = lightColorScheme(
    primary = BrandRed,
    onPrimary = OnChrome,
    secondary = Ink,
    onSecondary = OnChrome,
    tertiary = BrandRed,
    onTertiary = OnChrome,
    background = Ground,
    onBackground = Ink,
    surface = Sheet,
    onSurface = Ink,
    surfaceVariant = Ground,
    onSurfaceVariant = InkMuted,
    surfaceContainerLow = Sheet,
    outline = InkMuted,
    outlineVariant = Hairline,
    inverseSurface = Ink,
    inverseOnSurface = OnChrome,
    inversePrimary = BrandRedOnDark,
    // Keep elevated surfaces neutral instead of tinting them with the brand red
    surfaceTint = Color.Transparent,
)

private val DarkColorScheme = darkColorScheme(
    primary = BrandRedOnDark,
    onPrimary = OnBrandRedOnDark,
    secondary = TextOnDark,
    onSecondary = GroundDark,
    tertiary = BrandRedOnDark,
    onTertiary = OnBrandRedOnDark,
    background = GroundDark,
    onBackground = TextOnDark,
    surface = SheetDark,
    onSurface = TextOnDark,
    surfaceVariant = GroundDark,
    onSurfaceVariant = TextMutedOnDark,
    surfaceContainerLow = SheetDark,
    outline = TextMutedOnDark,
    outlineVariant = HairlineDark,
    inverseSurface = TextOnDark,
    inverseOnSurface = GroundDark,
    inversePrimary = BrandRed,
    surfaceTint = Color.Transparent,
)

@Composable
fun NewsAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val newsColors = if (darkTheme) DarkNewsColors else LightNewsColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // The status bar continues the broadcast chrome; the navigation bar continues the sheet below it
            window.statusBarColor = newsColors.chrome.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(LocalNewsColors provides newsColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
