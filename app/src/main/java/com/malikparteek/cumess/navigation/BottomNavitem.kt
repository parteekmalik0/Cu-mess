package com.malikparteek.cumess.navigation

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.malikparteek.cumess.R

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector? = null,
    @DrawableRes val drawableRes: Int? = null
) {
    object Today : BottomNavItem(
        route = "today",
        title = "Today",
        drawableRes = R.drawable.home_updated // 👈 Replace with your custom PNG file name
    )

    object Tomorrow : BottomNavItem(
        route = "tomorrow",
        title = "Tomorrow",
        drawableRes = R.drawable.calendar
    )

    object Timings : BottomNavItem(
        route = "timings",
        title = "Review",
        drawableRes = R.drawable.review
    )

    object Play : BottomNavItem(
        route = "Play",
        title = "Play",
        drawableRes = R.drawable.gaming
    )

    
}
