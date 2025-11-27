package com.malikparteek.cumess.screen

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import BannerAdView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.platform.LocalContext
import com.malikparteek.cumess.advertisement.InterstitialHelper
import androidx.compose.foundation.layout.navigationBarsPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OvermorrowScreen(menuViewModel: MenuViewModel = viewModel()) {
    val target = remember { LocalDate.now().plusDays(2) }
    val dateIso = remember { target.format(DateTimeFormatter.ISO_DATE) }

    // Observe ViewModel LiveData (same pattern as Home/Tomorrow)
    val menu by menuViewModel.overmorrowMenu.observeAsState()
    val isLoading by menuViewModel.isLoading.observeAsState(true)

    val context = LocalContext.current
    val activity = context as? Activity

    // Load overmorrow menu and preload ad
    LaunchedEffect(dateIso) {
        menuViewModel.fetchOvermorrowMenu()
        activity?.let { InterstitialHelper.preload(it) }
    }

    // Optional banner load simulation (copying HomeScreen behavior)
    var isBannerLoaded by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        // small delay so UI doesn't show blank gap instantly
        delay(800)
        isBannerLoaded = true
    }

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
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Overmorrow's Menu",
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
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .navigationBarsPadding(),
            contentPadding = PaddingValues(bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Emit items explicitly so we can inject Banner as a separate item
            for (index in meals.indices) {
                val (title, itemsList, image) = meals[index]

                item {
                    MealCard(
                        mealName = title,
                        items = itemsList,
                        imageRes = image,
                        isLoading = isLoading,
                        isToday = false
                    )
                }

                // Show banner only after 5th meal (index 4) — same as HomeScreen
                if (index == 1 && isBannerLoaded) {
                    item {
                        BannerAdView(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }

    // Show interstitial after loading (similar to TomorrowScreen behavior)
    LaunchedEffect(menu) {
        if (!isLoading && InterstitialHelper.isLoaded()) {
            activity?.let { InterstitialHelper.showOncePerSession(it) }
        }
    }
}
