package com.malikparteek.cumess.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdatesScreen() {

    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()

    // 🎨 Theme-based Colors
    val cardColor = if (isDarkTheme) {
        Color(0xFF3A3A3A)   // Light grey for dark mode
    } else {
        Color.White        // White for light mode
    }

    val textColor = if (isDarkTheme) {
        Color.White
    } else {
        Color.Black
    }

    val headingColor = if (isDarkTheme) {
        Color(0xFFD3D3D3)   // Soft light grey
    } else {
        Color(0xFF555555)   // Dark grey
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "PaperPilot",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = cardColor
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "🚀 Coming Soon - PaperPilot",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textColor
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "PaperPilot is a smart academic tool that will help you:\n\n" +
                                "• Convert images to PDF instantly\n" +
                                "• Merge the pdf instantly\n" +
                                "• Order prints directly from the app\n" +
                                "• And many more feature for you\n\n" +
                                "We are building this for students like you.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        color = headingColor
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://docs.google.com/forms/d/e/1FAIpQLSeN1AB3ptdUHIhlzq-Soxba4-75N-80IWJmClbTYszmn48MXQ/viewform?usp=publish-editor")
                            )
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("💬 Give Your suggestion - fill google form")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Your feedback will help us build something truly useful. " +
                                "Takes only 2 minutes - helpful for all",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = headingColor
                    )
                }
            }
        }
    }
}