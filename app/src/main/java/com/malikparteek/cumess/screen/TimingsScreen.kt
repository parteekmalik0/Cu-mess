package com.malikparteek.cumess.screen

import BannerAdView
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.malikparteek.cumess.R
import com.malikparteek.cumess.advertisement.NativeAdComposable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimingsScreen() {
    val isDark = isSystemInDarkTheme()
    val context = LocalContext.current
    val feedbackUrl =
        "https://sodexo-diy-surveys.esaas.inmoment.eu/cgi-bin/qwebcorporate?idx=DV3M8V&m=1"

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mess Timings & Review",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            BannerAdView()

            // 🍽️ Weekday Timings
            TimingCard(
                title = "Monday to Friday",
                timings = listOf(
                    "Breakfast: 7:30 AM – 9:00 AM",
                    "Lunch: 12:00 PM – 1:45 PM",
                    "Snacks: 4:30 PM – 5:15 PM",
                    "Dinner: 7:30 PM – 9:00 PM"
                ),
                isDark = isDark
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🍽️ Weekend Timings
            TimingCard(
                title = "Weekend & Holiday",
                timings = listOf(
                    "Breakfast: 8:00 AM – 9:30 AM",
                    "Lunch: 12:30 PM – 2:00 PM",
                    "Snacks: 4:30 PM – 5:15 PM",
                    "Dinner: 7:30 PM – 9:00 PM"
                ),
                isDark = isDark
            )

            Spacer(modifier = Modifier.height(15.dp))

            // 🙌 Feedback Prompt
            Text(
                text = "Click for meal feedback",
                fontSize = 16.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                textAlign = TextAlign.Center
            )

            TextButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(feedbackUrl))
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.satisfaction),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.Unspecified
                )

                Spacer(modifier = Modifier.width(5.dp))

                Text(
                    text = "Give Feedback",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    textDecoration = TextDecoration.Underline
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

                NativeAdComposable(context)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Made with ❤️ For You - N",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun TimingCard(title: String, timings: List<String>, isDark: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            timings.forEach {
                Text(
                    text = it,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }}