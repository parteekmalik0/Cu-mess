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
        drawableRes = R.drawable.home_updated
    )

    object Tomorrow : BottomNavItem(
        route = "tomorrow",
        title = "Morrow",
        showAd = true,   // 🔥 show interstitial when clicked
        drawableRes = R.drawable.calendar
    )

    object Overmorrow : BottomNavItem(
        route = "overmorrow",
        title = "Next Day",
        drawableRes = R.drawable.overmorrow
    )

    object Updates : BottomNavItem(
        route = "updates",
        title = "Updates",
        drawableRes = R.drawable.bell
    )

    object Timings : BottomNavItem(
        route = "timings",
        title = "Review",
        drawableRes = R.drawable.review
    )
}
