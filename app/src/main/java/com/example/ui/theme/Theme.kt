package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.data.model.AppThemeColor

fun getThemeColorScheme(themeColor: AppThemeColor, isDark: Boolean) = if (isDark) {
    when (themeColor) {
        AppThemeColor.ROYAL_INDIGO -> darkColorScheme(
            primary = Color(0xFF818CF8),
            onPrimary = Color(0xFF1E1B4B),
            primaryContainer = Color(0xFF312E81),
            onPrimaryContainer = Color(0xFFE0E7FF),
            secondary = Color(0xFFA5B4FC),
            tertiary = Color(0xFF67E8F9),
            background = DarkBackground,
            surface = DarkSurface,
            surfaceVariant = DarkSurfaceVariant,
            onBackground = DarkTextPrimary,
            onSurface = DarkTextPrimary
        )
        AppThemeColor.EMERALD_GREEN -> darkColorScheme(
            primary = Color(0xFF34D399),
            onPrimary = Color(0xFF064E3B),
            primaryContainer = Color(0xFF065F46),
            onPrimaryContainer = Color(0xFFD1FAE5),
            secondary = Color(0xFF6EE7B7),
            tertiary = Color(0xFFFCD34D),
            background = DarkBackground,
            surface = DarkSurface,
            surfaceVariant = DarkSurfaceVariant,
            onBackground = DarkTextPrimary,
            onSurface = DarkTextPrimary
        )
        AppThemeColor.SUNSET_CORAL -> darkColorScheme(
            primary = Color(0xFFFB923C),
            onPrimary = Color(0xFF7C2D12),
            primaryContainer = Color(0xFF9A3412),
            onPrimaryContainer = Color(0xFFFFEDD5),
            secondary = Color(0xFFFDBA74),
            tertiary = Color(0xFFFDA4AF),
            background = DarkBackground,
            surface = DarkSurface,
            surfaceVariant = DarkSurfaceVariant,
            onBackground = DarkTextPrimary,
            onSurface = DarkTextPrimary
        )
        AppThemeColor.CYBER_VIOLET -> darkColorScheme(
            primary = Color(0xFFA78BFA),
            onPrimary = Color(0xFF4C1D95),
            primaryContainer = Color(0xFF5B21B6),
            onPrimaryContainer = Color(0xFFEDE9FE),
            secondary = Color(0xFFC4B5FD),
            tertiary = Color(0xFFF472B6),
            background = DarkBackground,
            surface = DarkSurface,
            surfaceVariant = DarkSurfaceVariant,
            onBackground = DarkTextPrimary,
            onSurface = DarkTextPrimary
        )
        AppThemeColor.MIDNIGHT_CYAN -> darkColorScheme(
            primary = Color(0xFF22D3EE),
            onPrimary = Color(0xFF164E63),
            primaryContainer = Color(0xFF155E75),
            onPrimaryContainer = Color(0xFFCFFAFE),
            secondary = Color(0xFF67E8F9),
            tertiary = Color(0xFF60A5FA),
            background = DarkBackground,
            surface = DarkSurface,
            surfaceVariant = DarkSurfaceVariant,
            onBackground = DarkTextPrimary,
            onSurface = DarkTextPrimary
        )
    }
} else {
    when (themeColor) {
        AppThemeColor.ROYAL_INDIGO -> lightColorScheme(
            primary = IndigoPrimary,
            onPrimary = IndigoOnPrimary,
            primaryContainer = IndigoPrimaryContainer,
            onPrimaryContainer = IndigoOnPrimaryContainer,
            secondary = IndigoSecondary,
            tertiary = IndigoTertiary,
            background = LightBackground,
            surface = LightSurface,
            surfaceVariant = LightSurfaceVariant,
            onBackground = LightTextPrimary,
            onSurface = LightTextPrimary
        )
        AppThemeColor.EMERALD_GREEN -> lightColorScheme(
            primary = EmeraldPrimary,
            onPrimary = EmeraldOnPrimary,
            primaryContainer = EmeraldPrimaryContainer,
            onPrimaryContainer = EmeraldOnPrimaryContainer,
            secondary = EmeraldSecondary,
            tertiary = EmeraldTertiary,
            background = LightBackground,
            surface = LightSurface,
            surfaceVariant = LightSurfaceVariant,
            onBackground = LightTextPrimary,
            onSurface = LightTextPrimary
        )
        AppThemeColor.SUNSET_CORAL -> lightColorScheme(
            primary = CoralPrimary,
            onPrimary = CoralOnPrimary,
            primaryContainer = CoralPrimaryContainer,
            onPrimaryContainer = CoralOnPrimaryContainer,
            secondary = CoralSecondary,
            tertiary = CoralTertiary,
            background = LightBackground,
            surface = LightSurface,
            surfaceVariant = LightSurfaceVariant,
            onBackground = LightTextPrimary,
            onSurface = LightTextPrimary
        )
        AppThemeColor.CYBER_VIOLET -> lightColorScheme(
            primary = VioletPrimary,
            onPrimary = VioletOnPrimary,
            primaryContainer = VioletPrimaryContainer,
            onPrimaryContainer = VioletOnPrimaryContainer,
            secondary = VioletSecondary,
            tertiary = VioletTertiary,
            background = LightBackground,
            surface = LightSurface,
            surfaceVariant = LightSurfaceVariant,
            onBackground = LightTextPrimary,
            onSurface = LightTextPrimary
        )
        AppThemeColor.MIDNIGHT_CYAN -> lightColorScheme(
            primary = CyanPrimary,
            onPrimary = CyanOnPrimary,
            primaryContainer = CyanPrimaryContainer,
            onPrimaryContainer = CyanOnPrimaryContainer,
            secondary = CyanSecondary,
            tertiary = CyanTertiary,
            background = LightBackground,
            surface = LightSurface,
            surfaceVariant = LightSurfaceVariant,
            onBackground = LightTextPrimary,
            onSurface = LightTextPrimary
        )
    }
}

@Composable
fun MyApplicationTheme(
    themeColor: AppThemeColor = AppThemeColor.ROYAL_INDIGO,
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = getThemeColorScheme(themeColor, darkTheme)
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
