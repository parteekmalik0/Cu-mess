package com.malikparteek.cumess.screen.pdf

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.malikparteek.cumess.R
import com.malikparteek.cumess.screen.pdf.components.PdfToolCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfDashboardScreen(navController: NavController) {

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "PaperPilot",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Smart document tools for students.",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Convert, merge, edit and manage PDFs easily.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            PdfToolCard(
                title = "Image to PDF",
                description = "Convert images into submission-ready PDF.",
                imageRes = R.drawable.imgtopdf
            ) {
                navController.navigate("image_to_pdf")
            }

            Spacer(modifier = Modifier.height(16.dp))

            PdfToolCard(
                title = "Merge PDFs",
                description = "Combine multiple PDFs into one file.",
                imageRes = R.drawable.mergepdff
            ) {
                navController.navigate("merge_pdf")
            }

            Spacer(modifier = Modifier.height(16.dp))

            PdfToolCard(
                title = "Edit PDF",
                description = "Add text, signature, or watermark.",
                imageRes = R.drawable.imgtopdf
            ) {
                navController.navigate("edit_pdf")
            }
        }
    }
}