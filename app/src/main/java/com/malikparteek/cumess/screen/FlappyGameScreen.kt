// FlappyGameScreen.kt
package com.malikparteek.cumess.ui.game

import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.isActive
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random
import kotlin.math.sin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlappyGameScreen(
    modifier: Modifier = Modifier.fillMaxSize(),
    birdRadiusDp: Dp = 14.dp,
    background: Color = Color(0xFF8ED6FF) // sky
) {
    val density = LocalDensity.current
    val birdRadiusPx = with(density) { birdRadiusDp.toPx() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Flappy – Mini Game", fontSize = 18.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(background),
            contentAlignment = Alignment.Center
        ) {
            GameCanvas(
                birdRadius = birdRadiusPx,
                background = background,
                onGameEnd = { /* hook for analytics */ }
            )
        }
    }
}

private enum class GameState { Ready, Playing, Paused, GameOver }

private data class Pipe(
    val id: Int,
    val x: Float,          // left
    val width: Float,
    val gapTop: Float,     // top of gap
    val gapHeight: Float,
    val scored: Boolean = false
)

private data class Cloud(
    val x: Float,
    val y: Float,
    val w: Float,
    val h: Float,
    val speed: Float,
    val alpha: Float
)

private data class Particle(
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val life: Float,      // seconds remaining
    val radius: Float
)

@Composable
private fun GameCanvas(
    birdRadius: Float,
    background: Color,
    onGameEnd: (score: Int) -> Unit
) {
    // Baseline tunables
    val gravity = 1800f                 // px/s^2
    val flapImpulse = -520f             // px/s
    val pipeSpawnIntervalMs = 1350L
    val basePipeWidthFraction = 0.14f
    val basePipeGapFraction = 0.28f
    val minPipeGapFraction = 0.20f
    val baseSpeedFraction = 0.34f
    val maxSpeedFraction = 0.52f
    val birdTiltMaxDeg = 48f
    val tiltResponse = 0.12f
    val groundHeightFraction = 0.10f

    // Runtime dimensions
    var W by remember { mutableStateOf(0f) }
    var H by remember { mutableStateOf(0f) }

    // Game state
    var gameState by remember { mutableStateOf(GameState.Ready) }
    var birdY by remember { mutableStateOf(0f) }
    var birdVy by remember { mutableStateOf(0f) }
    var birdTilt by remember { mutableStateOf(0f) }
    var pipes by remember { mutableStateOf(listOf<Pipe>()) }
    var nextPipeId by remember { mutableStateOf(0) }
    var lastSpawnTime by remember { mutableStateOf(0L) }
    var score by remember { mutableStateOf(0) }
    var best by remember { mutableStateOf(0) }

    // Cosmetics
    var clouds by remember { mutableStateOf(emptyList<Cloud>()) }
    var groundScroll by remember { mutableStateOf(0f) }
    var particles by remember { mutableStateOf(emptyList<Particle>()) }

    // New: pipe continuity + adaptive diff
    var lastGapTop by remember { mutableStateOf<Float?>(null) }
    var recentCrashes by remember { mutableStateOf(0) }
    var lastCrashTime by remember { mutableStateOf(0L) }

    val haptics = LocalHapticFeedback.current

    fun resetAll() {
        birdY = H * 0.45f
        birdVy = 0f
        birdTilt = 0f
        pipes = emptyList()
        nextPipeId = 0
        lastSpawnTime = 0L
        groundScroll = 0f
        score = 0
        particles = emptyList()
        lastGapTop = null
        // regen clouds
        clouds = List(6) {
            Cloud(
                x = Random.nextFloat() * W,
                y = Random.nextFloat() * H * 0.4f,
                w = W * (0.10f + Random.nextFloat() * 0.12f),
                h = H * (0.05f + Random.nextFloat() * 0.04f),
                speed = (W * 0.02f) * (0.6f + Random.nextFloat() * 0.8f),
                alpha = 0.35f + Random.nextFloat() * 0.25f
            )
        }
    }

    fun start() {
        if (W <= 0f || H <= 0f) return
        if (gameState == GameState.Ready || gameState == GameState.GameOver) {
            resetAll()
            gameState = GameState.Playing
        }
    }

    fun flap() {
        when (gameState) {
            GameState.Ready -> {
                start()
                birdVy = flapImpulse
                particles = particles + makePoof(W * 0.28f, birdY, 8)
            }
            GameState.Playing -> {
                birdVy = flapImpulse
                particles = particles + makePoof(W * 0.28f, birdY, 6)
            }
            GameState.Paused -> Unit
            GameState.GameOver -> {
                start()
                birdVy = flapImpulse
            }
        }
    }

    fun togglePause() {
        if (gameState == GameState.Playing) gameState = GameState.Paused
        else if (gameState == GameState.Paused) gameState = GameState.Playing
    }

    fun crash() {
        gameState = GameState.GameOver
        best = max(best, score)
        onGameEnd(score)
        particles = particles + makeCrashBurst(W * 0.28f, birdY, 18)
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)

        // adaptive difficulty tracking
        val now = System.currentTimeMillis()
        if (now - lastCrashTime < 20_000L) {
            recentCrashes += 1
        } else {
            recentCrashes = 1
        }
        lastCrashTime = now
    }

    // ---------- Difficulty curves ----------
    // Gap progression: smooth (easeInOut) + adaptive mercy after multiple quick fails
    fun pipeGapFraction(): Float {
        val t = (score / 25f).coerceIn(0f, 1f)
        var baseGap = lerp(basePipeGapFraction, minPipeGapFraction, t * t * (3 - 2 * t))

        // Adaptive mercy (same as before)
        if (recentCrashes >= 3) baseGap *= 1.15f
        else if (recentCrashes >= 2) baseGap *= 1.08f

        // 👇 NEW: rhythmic variance for engagement
        val wave = sin((score / 2f) * PI.toFloat()) * 0.06f  // oscillates ±6%
        val randomJitter = (Random.nextFloat() - 0.5f) * 0.03f  // tiny randomness ±3%
        baseGap += wave + randomJitter

        // Clamp so it never becomes impossible or too easy
        return baseGap.coerceIn(minPipeGapFraction * 0.95f, basePipeGapFraction * 1.1f)
    }



    // Speed progression: a touch slower to avoid compounding with gap shrink
    fun pipeSpeed(): Float {
        val t = min(1f, score / 40f)
        return W * lerp(baseSpeedFraction, maxSpeedFraction, t)
    }

    // ---------- Main game loop ----------
    LaunchedEffect(Unit) {
        var lastFrame = 0L
        while (isActive) {
            val now = withInfiniteAnimationFrameMillis { it }
            if (lastFrame == 0L) lastFrame = now
            val dtMs = (now - lastFrame).coerceAtMost(50L)
            val dt = dtMs / 1000f
            lastFrame = now

            if (W > 0f && H > 0f && gameState == GameState.Playing) {
                // Physics
                birdVy += gravity * dt
                birdY += birdVy * dt
                val targetTilt = (birdVy / 800f).coerceIn(-1.25f, 0.8f) * birdTiltMaxDeg
                birdTilt = approach(birdTilt, targetTilt, tiltResponse, dt)

                // Spawn pipes with bounded vertical shift (hard but fair)
                if (now - lastSpawnTime >= pipeSpawnIntervalMs) {
                    val gapH = H * pipeGapFraction()
                    val topMargin = H * 0.10f
                    val bottomMargin = H * 0.10f
                    val minTop = topMargin
                    val maxTop = H - bottomMargin - gapH

                    // limit vertical delta from previous gap; scales down slightly with score
                    val maxShift = (H * 0.12f - score * 2f).coerceAtLeast(H * 0.04f)
                    val baseTop = lastGapTop ?: ((maxTop + minTop) / 2f)
                    val rawTop = baseTop + (Random.nextFloat() - 0.5f) * 2f * maxShift
                    val gapTop = rawTop.coerceIn(minTop, maxTop)
                    lastGapTop = gapTop

                    val newPipe = Pipe(
                        id = nextPipeId++,
                        x = W + (W * basePipeWidthFraction),
                        width = W * basePipeWidthFraction,
                        gapTop = gapTop,
                        gapHeight = gapH
                    )
                    pipes = pipes + newPipe
                    lastSpawnTime = now
                }

                // Move & cull pipes
                val speed = pipeSpeed()
                pipes = pipes.map { it.copy(x = it.x - speed * dt) }
                    .filter { it.x + it.width > -64f }

                // Scoring
                val birdX = W * 0.28f
                var scoredNow = false
                pipes = pipes.map {
                    if (!it.scored && it.x + it.width <= birdX) {
                        scoredNow = true
                        it.copy(scored = true)
                    } else it
                }
                if (scoredNow) {
                    score += 1
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }

                // Particles
                particles = particles
                    .map { p ->
                        p.copy(
                            x = p.x + p.vx * dt,
                            y = p.y + p.vy * dt,
                            vy = p.vy + gravity * 0.25f * dt,
                            life = p.life - dt
                        )
                    }
                    .filter { it.life > 0f }

                // Clouds & ground
                clouds = clouds.map { c ->
                    var nx = c.x - c.speed * dt
                    if (nx + c.w < -20f) nx = W + 20f
                    c.copy(x = nx)
                }
                groundScroll = (groundScroll + speed * dt) % W

                // Collisions
                val birdRect = Rect(
                    left = birdX - birdRadius,
                    top = birdY - birdRadius,
                    right = birdX + birdRadius,
                    bottom = birdY + birdRadius
                )

                val groundH = H * groundHeightFraction
                if (birdRect.top <= 0f || birdRect.bottom >= H - 0.0001f - groundH) {
                    crash()
                } else {
                    for (p in pipes) {
                        val gapBottom = p.gapTop + p.gapHeight
                        val topRect = Rect(p.x, 0f, p.x + p.width, p.gapTop)
                        val botRect = Rect(p.x, gapBottom, p.x + p.width, H - groundH)
                        if (circleIntersectsRect(birdX, birdY, birdRadius * 0.92f, topRect)
                            || circleIntersectsRect(birdX, birdY, birdRadius * 0.92f, botRect)
                        ) {
                            crash()
                            break
                        }
                    }
                }
            }
        }
    }

    // ---------- Input & Rendering ----------
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                while (true) {
                    awaitPointerEventScope {
                        val ev = awaitPointerEvent()
                        if (ev.changes.any { it.pressed }) {
                            flap()
                            ev.changes.forEach { it.consume() }
                        }
                    }
                }
            }
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { c ->
                    W = c.size.width.toFloat()
                    H = c.size.height.toFloat()
                    if (gameState == GameState.Ready && birdY == 0f && H > 0f) {
                        resetAll()
                    }
                }
        ) {
            // Sky
            drawRect(color = background, size = size)
            drawRect(
                color = Color.White.copy(alpha = 0.10f),
                topLeft = Offset(0f, 0f),
                size = Size(size.width, size.height * 0.36f)
            )

            // Clouds
            clouds.forEach { drawCloud(it) }

            // Pipes
            val groundH = size.height * groundHeightFraction
            pipes.forEach { drawPipe(it, pipeGapFraction(), groundH) }

            // Bird
            val birdX = size.width * 0.28f
            rotate(degrees = birdTilt, pivot = Offset(birdX, birdY)) {
                drawCircle(
                    color = Color(0xFFFFD54F),
                    center = Offset(birdX, birdY),
                    radius = birdRadius
                )
                // wing stripe
                drawRoundRect(
                    color = Color(0xFFF9A825),
                    topLeft = Offset(birdX - birdRadius * 0.9f, birdY - birdRadius * 0.18f),
                    size = Size(birdRadius * 1.0f, birdRadius * 0.36f),
                    cornerRadius = CornerRadius(birdRadius * 0.18f, birdRadius * 0.18f)
                )
                // eye
                drawCircle(
                    color = Color.Black,
                    center = Offset(birdX + birdRadius * 0.28f, birdY - birdRadius * 0.22f),
                    radius = birdRadius * 0.22f
                )
                drawCircle(
                    color = Color.White,
                    center = Offset(birdX + birdRadius * 0.25f, birdY - birdRadius * 0.24f),
                    radius = birdRadius * 0.10f
                )
                // beak
                drawRoundRect(
                    color = Color(0xFFFF7043),
                    topLeft = Offset(birdX + birdRadius * 0.55f, birdY - birdRadius * 0.10f),
                    size = Size(birdRadius * 0.8f, birdRadius * 0.20f),
                    cornerRadius = CornerRadius(birdRadius * 0.08f, birdRadius * 0.08f)
                )
            }

            // Particles
            particles.forEach { p ->
                drawCircle(
                    color = Color.White.copy(alpha = (p.life).coerceIn(0f, 1f) * 0.8f),
                    center = Offset(p.x, p.y),
                    radius = p.radius
                )
            }

            // Ground
            drawGround(groundH, groundScroll)

            // Subtle horizon
            drawLine(
                color = Color.White.copy(alpha = 0.08f),
                start = Offset(0f, size.height * 0.36f),
                end = Offset(size.width, size.height * 0.36f),
                strokeWidth = 2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f)
            )
        }

        // HUD
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Score", fontSize = 12.sp, color = Color.White.copy(alpha = 0.9f))
                    Text("$score", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Row {
                    when (gameState) {
                        GameState.Playing -> TextButton(onClick = { togglePause() }) { Text("Pause") }
                        GameState.Paused -> TextButton(onClick = { togglePause() }) { Text("Resume") }
                        GameState.GameOver -> TextButton(onClick = {
                            gameState = GameState.Ready
                            resetAll()
                        }) { Text("Restart") }
                        GameState.Ready -> TextButton(onClick = { start() }) { Text("Start") }
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            when (gameState) {
                GameState.Ready -> HelpCard(
                    title = "Tap to flap",
                    subtitle = "Avoid pipes • Pass pipes to score • Tilt follows speed"
                )
                GameState.Paused -> HelpCard(
                    title = "Paused",
                    subtitle = "Tap Resume or anywhere to flap"
                )
                GameState.GameOver -> HelpCard(
                    title = "Game Over",
                    subtitle = "Score: $score  •  Best: $best\nTap anywhere to retry"
                )
                else -> {}
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

/* ---------- Drawing helpers ---------- */

private fun DrawScope.drawCloud(c: Cloud) {
    val base = Color.White.copy(alpha = c.alpha)
    drawRoundRect(
        color = base,
        topLeft = Offset(c.x, c.y),
        size = Size(c.w, c.h),
        cornerRadius = CornerRadius(c.h * 0.5f, c.h * 0.5f)
    )
    drawRoundRect(
        color = base,
        topLeft = Offset(c.x + c.w * 0.18f, c.y - c.h * 0.35f),
        size = Size(c.w * 0.46f, c.h * 0.85f),
        cornerRadius = CornerRadius(c.h * 0.5f, c.h * 0.5f)
    )
    drawRoundRect(
        color = base,
        topLeft = Offset(c.x + c.w * 0.5f, c.y - c.h * 0.22f),
        size = Size(c.w * 0.38f, c.h * 0.75f),
        cornerRadius = CornerRadius(c.h * 0.5f, c.h * 0.5f)
    )
}

private fun DrawScope.drawPipe(p: Pipe, gapFraction: Float, groundH: Float) {
    val bodyColor = Color(0xFF2E7D32)
    val capColor = Color(0xFF388E3C)
    val shadow = Color.Black.copy(alpha = 0.12f)

    val gapTop = p.gapTop
    val gapBottom = p.gapTop + p.gapHeight

    // shadows
    drawRoundRect(
        color = shadow,
        topLeft = Offset(p.x + 6f, 0f),
        size = Size(p.width, gapTop),
        cornerRadius = CornerRadius(12f, 12f)
    )
    drawRoundRect(
        color = shadow,
        topLeft = Offset(p.x + 6f, gapBottom),
        size = Size(p.width, size.height - groundH - gapBottom),
        cornerRadius = CornerRadius(12f, 12f)
    )

    // top body
    drawRoundRect(
        color = bodyColor,
        topLeft = Offset(p.x, 0f),
        size = Size(p.width, gapTop),
        cornerRadius = CornerRadius(12f, 12f)
    )
    // top cap
    drawRoundRect(
        color = capColor,
        topLeft = Offset(p.x - 6f, gapTop - 26f),
        size = Size(p.width + 12f, 26f),
        cornerRadius = CornerRadius(10f, 10f)
    )

    // bottom body
    drawRoundRect(
        color = bodyColor,
        topLeft = Offset(p.x, gapBottom),
        size = Size(p.width, size.height - groundH - gapBottom),
        cornerRadius = CornerRadius(12f, 12f)
    )
    // bottom cap
    drawRoundRect(
        color = capColor,
        topLeft = Offset(p.x - 6f, gapBottom),
        size = Size(p.width + 12f, 26f),
        cornerRadius = CornerRadius(10f, 10f)
    )
}

private fun DrawScope.drawGround(groundH: Float, scroll: Float) {
    val groundTop = size.height - groundH
    // base
    drawRect(
        color = Color(0xFF6ABF69),
        topLeft = Offset(0f, groundTop),
        size = Size(size.width, groundH)
    )
    // stripes (tiles)
    val tileW = size.width * 0.12f
    val count = (size.width / tileW).toInt() + 3
    for (i in 0 until count) {
        val x = ((i * tileW) - (scroll % tileW))
        drawRoundRect(
            color = Color(0xFF5AAE58),
            topLeft = Offset(x, groundTop + groundH * 0.25f),
            size = Size(tileW * 0.9f, groundH * 0.25f),
            cornerRadius = CornerRadius(6f, 6f)
        )
    }
}

/* ---------- Math & collision helpers ---------- */

private fun circleIntersectsRect(cx: Float, cy: Float, r: Float, rect: Rect): Boolean {
    val closestX = cx.coerceIn(rect.left, rect.right)
    val closestY = cy.coerceIn(rect.top, rect.bottom)
    val dx = cx - closestX
    val dy = cy - closestY
    return dx * dx + dy * dy <= r * r
}

private fun lerp(a: Float, b: Float, t: Float) = a + (b - a) * t

private fun approach(current: Float, target: Float, response: Float, dt: Float): Float {
    // critically-damped-like smoothing
    val diff = target - current
    return current + diff * (1f - exp(-response * 10f * dt))
}

/* ---------- Particles ---------- */

private fun makePoof(birdX: Float, birdY: Float, count: Int): List<Particle> {
    return List(count) {
        val angle = Random.nextFloat() * (PI.toFloat() * 2f)
        val speed = 80f + Random.nextFloat() * 160f
        Particle(
            x = birdX - 6f,
            y = birdY + 2f,
            vx = -abs(speed * kotlin.math.cos(angle)),
            vy = speed * kotlin.math.sin(angle) * 0.2f,
            life = 0.35f + Random.nextFloat() * 0.3f,
            radius = 2.5f + Random.nextFloat() * 2.5f
        )
    }
}

private fun makeCrashBurst(x: Float, y: Float, count: Int): List<Particle> {
    return List(count) {
        val angle = Random.nextFloat() * (PI.toFloat() * 2f)
        val speed = 120f + Random.nextFloat() * 260f
        Particle(
            x = x,
            y = y,
            vx = speed * kotlin.math.cos(angle),
            vy = speed * kotlin.math.sin(angle),
            life = 0.6f + Random.nextFloat() * 0.5f,
            radius = 3f + Random.nextFloat() * 3f
        )
    }
}

/* ---------- Small UI piece ---------- */

@Composable
private fun HelpCard(title: String, subtitle: String) {
    Card(
        modifier = Modifier
            .wrapContentSize()
            .padding(8.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(subtitle, color = Color.White.copy(alpha = 0.92f), fontSize = 12.sp)
        }
    }
}
