package com.malikparteek.cumess.navigation

import androidx.annotation.DrawableRes
import com.malikparteek.cumess.R

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val showAd: Boolean = false,
    @DrawableRes val drawableRes: Int? = null
) {
    object Today : BottomNavItem(
        route = "today",
        title = "Today",
        drawableRes = R.drawable.new_home
    )

    object Tomorrow : BottomNavItem(
        route = "tomorrow",
        title = "Morrow",
        showAd = true,   // 🔥 show interstitial when clicked
        drawableRes = R.drawable.new_tomorrow
    )

    object Overmorrow : BottomNavItem(
        route = "overmorrow",
        title = "NextDay",
        drawableRes = R.drawable.new_overmorrow
    )

    object Updates : BottomNavItem(
        route = "updates",
        title = "PaperPilot",
        drawableRes = R.drawable.pdf
    )

    object Timings : BottomNavItem(
        route = "timings",
        title = "Review",
        drawableRes = R.drawable.review
    )
}
