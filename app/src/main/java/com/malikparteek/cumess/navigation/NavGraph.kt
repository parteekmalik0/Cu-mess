package com.malikparteek.cumess.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.malikparteek.cumess.screen.HomeScreen
import com.malikparteek.cumess.screen.TimingsScreen
import com.malikparteek.cumess.screen.TomorrowScreen
import com.malikparteek.cumess.viewmodel.MenuViewModel
import com.malikparteek.cumess.ui.game.FlappyGameScreen

@Composable
fun NavGraph(navController: NavHostController) {
    val menuViewModel: MenuViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Today.route
    ) {
        composable(BottomNavItem.Today.route) {
            HomeScreen(menuViewModel = menuViewModel)
        }
        composable(BottomNavItem.Tomorrow.route) {
            TomorrowScreen(menuViewModel = menuViewModel)
        }

        composable(BottomNavItem.Timings.route) {
            TimingsScreen()
        }

        composable(BottomNavItem.Play.route){
            FlappyGameScreen()

        }
    }
}
