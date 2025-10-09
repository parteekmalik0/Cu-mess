package com.malikparteek.cumess.navigation

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.malikparteek.cumess.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Public

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

    object International : BottomNavItem(
        route = "international",
        title = "Intl",
        drawableRes = R.drawable.globe
    )

    object Timings : BottomNavItem(
        route = "timings",
        title = "Review",
        drawableRes = R.drawable.review
    )
    
}
