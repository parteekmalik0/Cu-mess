package com.malikparteek.cumess.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.malikparteek.cumess.screen.HomeScreen
import com.malikparteek.cumess.screen.TimingsScreen
import com.malikparteek.cumess.screen.TomorrowScreen
import com.malikparteek.cumess.screen.OvermorrowScreen
import com.malikparteek.cumess.screen.UpdatesScreen
import com.malikparteek.cumess.viewmodel.MenuViewModel
import com.malikparteek.cumess.screen.pdf.ImageToPdfScreen
import com.malikparteek.cumess.screen.pdf.PdfDashboardScreen

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
        // Overmorrow (day after tomorrow)
        composable(BottomNavItem.Overmorrow.route) {
            OvermorrowScreen(menuViewModel = menuViewModel)
        }
        // Updates screen
        composable(BottomNavItem.Updates.route) {
            PdfDashboardScreen(navController)
        }
        composable(BottomNavItem.Timings.route) {
            TimingsScreen()
        }
        composable("pdf_dashboard") {
            PdfDashboardScreen(navController)
        }

        composable("image_to_pdf") {
            ImageToPdfScreen()
        }

//        composable("merge_pdf") {
//            MergePdfScreen()
//        }
//
//        composable("edit_pdf") {
//            EditPdfScreen()
//        }
    }
}
