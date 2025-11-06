package com.malikparteek.cumess.navigation

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.malikparteek.cumess.advertisement.InterstitialHelper

@Composable
fun BottomNavBar(navController: NavController) {
    val context = LocalContext.current
    val activity = context as? Activity

    val items = listOf(
        BottomNavItem.Today,
        BottomNavItem.Tomorrow,
        BottomNavItem.Timings,
        BottomNavItem.Play
    )

    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = MaterialTheme.colorScheme.background
    val selectedColor = MaterialTheme.colorScheme.primary
    val unselectedColor = if (isDarkTheme) Color.LightGray else Color.DarkGray

    NavigationBar(
        modifier = Modifier.navigationBarsPadding(),
        containerColor = backgroundColor,
        contentColor = unselectedColor
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            val selected = currentRoute == item.route

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        // Preload ad in background for next potential screen
                        activity?.let { InterstitialHelper.preload(it) }

                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                label = {
                    Text(
                        text = item.title,
                        color = if (selected) selectedColor else unselectedColor
                    )
                },
                icon = {
                    item.drawableRes?.let {
                        Icon(
                            painter = painterResource(id = it),
                            contentDescription = item.title,
                            modifier = Modifier.size(22.dp),
                            tint = if (selected) selectedColor else unselectedColor
                        )
                    } ?: item.icon?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = item.title,
                            modifier = Modifier.size(22.dp),
                            tint = if (selected) selectedColor else unselectedColor
                        )
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = selectedColor,
                    unselectedIconColor = unselectedColor,
                    selectedTextColor = selectedColor,
                    unselectedTextColor = unselectedColor,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
