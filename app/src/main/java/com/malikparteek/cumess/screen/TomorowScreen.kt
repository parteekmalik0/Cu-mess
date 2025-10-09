package com.malikparteek.cumess.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TomorrowScreen(menuViewModel: MenuViewModel = viewModel()) {
    val menu by menuViewModel.tomorrowMenu.observeAsState()

    // Fetch tomorrow menu
    LaunchedEffect(Unit) { menuViewModel.fetchTomorrowMenu() }

    val isLoading = menu == null
    val resolvedMenu = menu ?: Menu()

    val meals = listOf(
        Triple("Breakfast", resolvedMenu.breakfast ?: emptyList(), R.drawable.breakfast),
        Triple("Lunch", resolvedMenu.lunch ?: emptyList(), R.drawable.lunch_0),
        Triple("Snacks (Boys)", resolvedMenu.snacks ?: emptyList(), R.drawable.boy_snacks),
        Triple("Snacks (Girls)", resolvedMenu.snacks_girls ?: emptyList(), R.drawable.girl_snacks),
        Triple("Dinner", resolvedMenu.dinner ?: emptyList(), R.drawable.christmas_dinner),
        Triple("Dinner (South Indian)", resolvedMenu.dinner_south ?: emptyList(), R.drawable.christmas_dinner)
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Tomorrow's Menu",
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
                .padding(horizontal = 16.dp, vertical = 16.dp),
            contentPadding = PaddingValues(bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(meals) { _, (title, items, image) ->
                MealCard(
                    mealName = title,
                    items = items,
                    imageRes = image,
                    isLoading = isLoading,
                    isToday = false
                )
            }
        }
    }
}
