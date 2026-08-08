package org.revelar.photoviewer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val RevelarColors = darkColorScheme(
    primary = AccentDefault,
    onPrimary = Bg,
    secondary = InkSoft,
    background = Bg,
    onBackground = Ink,
    surface = BgElev,
    onSurface = Ink,
    surfaceVariant = BgElev,
    onSurfaceVariant = InkSoft,
    outline = InkFaint,
)

@Composable
fun RevelarTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = RevelarColors,
        typography = RevelarTypography,
        content = content
    )
}
