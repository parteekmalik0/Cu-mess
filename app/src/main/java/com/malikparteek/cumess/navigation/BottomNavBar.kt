package com.malikparteek.cumess.navigation

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.malikparteek.cumess.advertisement.InterstitialHelper
import com.malikparteek.cumess.viewmodel.UpdatesViewModel

@Composable
fun BottomNavBar(navController: NavController) {
    val context = LocalContext.current
    val activity = context as? Activity

    // 👉 Preload ad immediately when BottomBar loads
    LaunchedEffect(Unit) {
        activity?.let { InterstitialHelper.preload(it) }
    }

    val items = listOf(
        BottomNavItem.Today,
        BottomNavItem.Tomorrow,
        BottomNavItem.Overmorrow,
        BottomNavItem.Updates,
        BottomNavItem.Timings
    )

    val isDark = isSystemInDarkTheme()
    val bg = MaterialTheme.colorScheme.background
    val sel = MaterialTheme.colorScheme.primary
    val unSel = if (isDark) Color.LightGray else Color.DarkGray

    val updatesVM: UpdatesViewModel = viewModel()
    val updates by updatesVM.updates.collectAsState()

    NavigationBar(
        modifier = Modifier.navigationBarsPadding(),
        containerColor = bg,
        contentColor = unSel
    ) {
        val currentRoute = navController
            .currentBackStackEntryAsState().value?.destination?.route

        items.forEach { item ->
            val selected = currentRoute == item.route

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {

                        // 🔥 If this tab requires ad → show instantly
                        if (item.showAd && activity != null && InterstitialHelper.isLoaded()) {
                            InterstitialHelper.showOncePerSession(activity) {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        } else {
                            // Normal navigation
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }

                        //Always start preloading again
                        activity?.let { InterstitialHelper.preload(it) }
                    }
                },
                icon = {
                    val isUpdatesTab = item.route == BottomNavItem.Updates.route

                    if (isUpdatesTab && updates.isNotEmpty()) {
                        Box(Modifier.size(28.dp)) {
                            Icon(
                                painter = painterResource(id = item.drawableRes!!),
                                contentDescription = item.title,
                                modifier = Modifier.align(Alignment.Center).size(20.dp),
                                tint = if (selected) sel else unSel
                            )

                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .align(Alignment.TopEnd)
                                    .offset(x = 2.dp, y = (-2).dp)
                                    .clip(CircleShape)
                                    .background(Color.Red)
                            )
                        }
                    } else {
                        Icon(
                            painter = painterResource(id = item.drawableRes!!),
                            contentDescription = item.title,
                            modifier = Modifier.size(20.dp),
                            tint = if (selected) sel else unSel
                        )
                    }
                },
                label = {
                    Text(
                        text = item.title,
                        color = if (selected) sel else unSel
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = sel,
                    unselectedIconColor = unSel,
                    selectedTextColor = sel,
                    unselectedTextColor = unSel,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
