package com.malikparteek.cumess.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.random.Random
import androidx.navigation.NavHostController

// -------------------- Obstacle shapes --------------------
sealed class ObstacleShape {
    object Rectangle : ObstacleShape()
    object SemiCircle : ObstacleShape()
    object Triangle : ObstacleShape()
}

// -------------------- Obstacle data class --------------------
data class Obstacle(
    var x: Float,
    var y: Float,
    val width: Float,
    val height: Float,
    var speed: Float,
    val shape: ObstacleShape
)

// -------------------- GameScreen --------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(navController: NavHostController) {
    // Player (ball)
    var playerX by remember { mutableStateOf(500f) }
    val playerY = 1500f
    val playerRadius = 40f

    // Game state
    var obstacles by remember { mutableStateOf(generateObstacles(3)) }
    var isGameOver by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }
    var highScore by remember { mutableStateOf(0) }
    var level by remember { mutableStateOf(1) }

    // Background effect
    var bgOffset by remember { mutableStateOf(0f) }

    // -------------------- Game Loop --------------------
    LaunchedEffect(isGameOver) {
        while (!isGameOver) {
            delay(16L) // ~60 FPS

            // Move obstacles
            obstacles = obstacles.map { obs ->
                var newY = obs.y + obs.speed
                var newX = obs.x
                var newSpeed = obs.speed

                if (newY > 2000f) {
                    newY = -200f
                    newX = Random.nextInt(50, 800).toFloat()
                    newSpeed += 0.2f
                    score += 1

                    // Level up every 10 points
                    if (score % 10 == 0) {
                        level += 1
                        newSpeed += 1f
                    }
                }
                obs.copy(x = newX, y = newY, speed = newSpeed)
            }

            // Collision detection
            for (obs in obstacles) {
                val distX = abs(playerX - (obs.x + obs.width / 2))
                val distY = abs(playerY - (obs.y + obs.height / 2))
                if (distX < playerRadius + obs.width / 2 &&
                    distY < playerRadius + obs.height / 2
                ) {
                    isGameOver = true
                    if (score > highScore) highScore = score
                }
            }

            // Move background
            bgOffset += 2f
            if (bgOffset > 2000f) bgOffset = 0f
        }
    }

    // -------------------- UI --------------------
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Dodge Game 🎮",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = MaterialTheme.colorScheme.onPrimary
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF1E1E1E))
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        playerX += dragAmount.x
                        if (playerX < playerRadius) playerX = playerRadius
                        if (playerX > 1080f - playerRadius) playerX = 1080f - playerRadius
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Draw animated background circles
                for (i in 0..10) {
                    drawCircle(
                        color = Color(0xFF555555).copy(alpha = 0.2f),
                        radius = 50f,
                        center = Offset((i * 150f) % 1080, (bgOffset + i * 200f) % 2000)
                    )
                }

                // Draw player
                drawCircle(
                    color = Color.White,
                    radius = playerRadius,
                    center = Offset(playerX, playerY)
                )

                // Draw obstacles
                obstacles.forEach { obs ->
                    when (obs.shape) {
                        ObstacleShape.Rectangle -> drawRoundRect(
                            color = Color.White,
                            topLeft = Offset(obs.x, obs.y),
                            size = androidx.compose.ui.geometry.Size(obs.width, obs.height),
                            cornerRadius = CornerRadius(10f, 10f)
                        )
                        ObstacleShape.SemiCircle -> drawArc(
                            color = Color.White,
                            startAngle = 180f,
                            sweepAngle = 180f,
                            useCenter = true,
                            topLeft = Offset(obs.x, obs.y),
                            size = androidx.compose.ui.geometry.Size(obs.width, obs.height)
                        )
                        ObstacleShape.Triangle -> {
                            val path = Path().apply {
                                moveTo(obs.x + obs.width / 2, obs.y)
                                lineTo(obs.x, obs.y + obs.height)
                                lineTo(obs.x + obs.width, obs.y + obs.height)
                                close()
                            }
                            drawPath(path, color = Color.White)
                        }
                    }
                }
            }

            // Score, high score, level
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Score: $score", color = Color.White, style = MaterialTheme.typography.headlineMedium)
                Text("High Score: $highScore", color = Color.Yellow, style = MaterialTheme.typography.headlineSmall)
                Text("Level: $level", color = Color.Cyan, style = MaterialTheme.typography.headlineSmall)
            }

            // Game over overlay
            if (isGameOver) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("GAME OVER", color = Color.Red, style = MaterialTheme.typography.headlineLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        isGameOver = false
                        score = 0
                        level = 1
                        obstacles = generateObstacles(3)
                    }) {
                        Text("Restart")
                    }
                }
            }
        }
    }
}

// -------------------- Helper to generate obstacles --------------------
fun generateObstacles(count: Int): List<Obstacle> {
    val shapes = listOf(ObstacleShape.Rectangle, ObstacleShape.SemiCircle, ObstacleShape.Triangle)
    return List(count) {
        Obstacle(
            x = Random.nextInt(50, 800).toFloat(),
            y = -200f * it,
            width = Random.nextInt(100, 250).toFloat(),
            height = 40f,
            speed = Random.nextFloat() * 5 + 5,
            shape = shapes.random()
        )
    }
}
