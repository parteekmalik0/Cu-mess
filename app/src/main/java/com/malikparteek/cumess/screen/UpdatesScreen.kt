package com.malikparteek.cumess.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.malikparteek.cumess.data.MenuUpdate
import com.malikparteek.cumess.viewmodel.UpdatesViewModel
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.malikparteek.cumess.advertisement.NativeAdComposable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdatesScreen(
    viewModel: UpdatesViewModel = viewModel()
) {
    val updates by viewModel.updates.collectAsState()
    val error by viewModel.errorMessage.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mess Updates",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
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
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Single elevated card that holds all updates (styled like meal cards)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                ) {
                    // Header row: title + optional unread count
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Latest Updates",
                            fontSize = 25.sp,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        if (unreadCount > 0) {
                            Text(
                                text = "$unreadCount new",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Error inline
                    if (error != null) {
                        Text(
                            text = "Error: ${error ?: "Unknown"}",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }


                    // Updates content (all inside same card)
                    if (updates.isEmpty()) {
                        Text(
                            text = "No updates",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        updates.forEachIndexed { index, item: MenuUpdate ->
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.content,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 5,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            if (index < updates.lastIndex) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Divider()
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }
            }
        }

    }
}
