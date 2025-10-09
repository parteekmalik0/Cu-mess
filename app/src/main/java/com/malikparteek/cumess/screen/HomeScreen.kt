package com.malikparteek.cumess.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.malikparteek.cumess.R
import com.malikparteek.cumess.viewmodel.MenuViewModel
import androidx.compose.foundation.layout.navigationBarsPadding
import BannerAdView
import androidx.compose.ui.Alignment
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(menuViewModel: MenuViewModel = viewModel()) {
    val menu by menuViewModel.menu.observeAsState(Menu())
    val isLoading by menuViewModel.isLoading.observeAsState(true)

    LaunchedEffect(Unit) {
        menuViewModel.fetchTodayMenu()
    }

    val meals = listOf(
        Triple("Breakfast", menu.breakfast ?: emptyList(), R.drawable.breakfast),
        Triple("Lunch", menu.lunch ?: emptyList(), R.drawable.lunch_0),
        Triple("Snacks (Boys)", menu.snacks ?: emptyList(), R.drawable.boy_snacks),
        Triple("Snacks (Girls)", menu.snacks_girls ?: emptyList(), R.drawable.girl_snacks),
        Triple("Dinner", menu.dinner ?: emptyList(), R.drawable.christmas_dinner),
        Triple("Dinner (South Indian)", menu.dinner_south ?: emptyList(), R.drawable.christmas_dinner),
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Today's Menu",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                .navigationBarsPadding(),
            contentPadding = PaddingValues(bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(meals.size) { index ->
                val (title, items, image) = meals[index]

                MealCard(
                    mealName = title,
                    items = items,
                    imageRes = image,
                    isLoading = isLoading,
                    isToday = true
                )

                // Show Ad after 3rd card (index == 2 because list is 0-based)
                if (index == 2) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                      //  BannerAdView()
                    }
                }
            }
        }
    }
}