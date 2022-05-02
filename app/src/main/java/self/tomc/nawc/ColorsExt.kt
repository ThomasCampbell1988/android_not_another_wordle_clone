package self.tomc.nawc

import androidx.compose.material.Colors
import androidx.compose.ui.graphics.Color

val MathcedGreen = Color(83, 141, 78)

val PresentYellow = Color(181, 159, 59)

val MismatchGrey = Color(57, 57, 60)

val ActiveTileGrey = Color(86, 87, 88)


val KeyTextWhite = Color(0xffd7dadc)
val KeyBackgroundGrey = Color(0xff818384)

val Colors.matchedGreen: Color
    get() = if (isLight) MathcedGreen else MathcedGreen

val Colors.presentYellow: Color
    get() = if (isLight) PresentYellow else PresentYellow

val Colors.mismatchGrey: Color
    get() = if (isLight) MismatchGrey else MismatchGrey

val Colors.activeTileGrey: Color
    get() = if (isLight) ActiveTileGrey else ActiveTileGrey


val Colors.keyboardText: Color
    get() = if (isLight) KeyTextWhite else KeyTextWhite

val Colors.keyboardBackground: Color
    get() = if (isLight) KeyBackgroundGrey else KeyBackgroundGrey
