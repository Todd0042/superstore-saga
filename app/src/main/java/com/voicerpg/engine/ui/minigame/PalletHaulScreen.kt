package com.voicerpg.engine.ui.minigame

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voicerpg.engine.model.MiniGameConfig
import kotlinx.coroutines.delay
import kotlin.math.*
import kotlin.random.Random

enum class HazardType(val label: String, val color: Color, val widthNorm: Float, val damage: Float) {
    CONE("Caution Cone", Color(0xFFFFB300), 0.08f, 15f),
    CART("Abandoned Cart", Color(0xFF90A4AE), 0.14f, 20f),
    PUDDLE("Detergent Spill", Color(0xFF00E5FF), 0.16f, 5f), // Causes steering slip!
    SHOPPER("Wandering Shopper", Color(0xFFE57373), 0.12f, 15f)
}

data class HazardEntity(
    val id: Int,
    val type: HazardType,
    var x: Float, // Normalized 0f - 1f across aisle
    var y: Float, // Normalized 0f - 1f down screen
    var isCleared: Boolean = false
)

data class HaulParticle(
    val x: Float,
    val y: Float,
    val text: String,
    val color: Color,
    val timestamp: Long
)

@Composable
fun PalletHaulScreen(
    config: MiniGameConfig,
    onScoreUpdated: (score: Float, quotaDone: Int, primaryMetric: Float) -> Unit,
    onFinish: (finalScore: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var freightIntegrity by remember { mutableFloatStateOf(100f) }
    var routeProgress by remember { mutableFloatStateOf(0f) } // 0% to 100%
    var playerX by remember { mutableFloatStateOf(0.5f) }
    var playerVelocityX by remember { mutableFloatStateOf(0f) }
    var targetSteerX by remember { mutableFloatStateOf(0.5f) }

    // Stability needle: -1f (hard left) to +1f (hard right). Safe zone is between -0.4f and +0.4f
    var stabilityNeedle by remember { mutableFloatStateOf(0f) }
    var slipTimer by remember { mutableFloatStateOf(0f) }
    var hornCooldown by remember { mutableFloatStateOf(0f) }
    var brakeActiveTimer by remember { mutableFloatStateOf(0f) }

    val hazards = remember { mutableStateListOf<HazardEntity>() }
    val particles = remember { mutableStateListOf<HaulParticle>() }
    var nextHazardId by remember { mutableIntStateOf(1) }
    var isGameOver by remember { mutableStateOf(false) }

    // Main physics and scroll loop
    LaunchedEffect(isGameOver) {
        if (isGameOver) return@LaunchedEffect

        while (!isGameOver) {
            delay(33) // ~30 FPS
            val dt = 0.033f

            if (hornCooldown > 0f) hornCooldown = (hornCooldown - dt).coerceAtLeast(0f)
            if (brakeActiveTimer > 0f) brakeActiveTimer = (brakeActiveTimer - dt).coerceAtLeast(0f)
            if (slipTimer > 0f) slipTimer = (slipTimer - dt).coerceAtLeast(0f)

            // Calculate forward speed
            val forwardSpeed = if (brakeActiveTimer > 0f) 0.02f else 0.12f
            routeProgress = (routeProgress + forwardSpeed * dt * 25f).coerceAtMost(100f)

            // Inertia & lateral steering physics
            val effectiveTargetX = if (slipTimer > 0f) {
                // Steering hydroplane slipping: erratic drift
                (playerX + sin(System.currentTimeMillis() / 80f) * 0.1f).coerceIn(0.15f, 0.85f)
            } else {
                targetSteerX
            }

            val steerDelta = effectiveTargetX - playerX
            playerVelocityX = (playerVelocityX * 0.75f) + (steerDelta * 0.25f)
            playerX = (playerX + playerVelocityX * 0.4f).coerceIn(0.12f, 0.88f)

            // Stability needle swings with lateral acceleration
            stabilityNeedle = (stabilityNeedle * 0.8f + (playerVelocityX * 8f) * 0.2f).coerceIn(-1f, 1f)
            if (abs(stabilityNeedle) > 0.65f) {
                // Red zone: freight is wobbling!
                freightIntegrity = (freightIntegrity - 0.25f).coerceAtLeast(0f)
            }

            // Spawn hazards moving down the aisle
            if (hazards.size < 5 && Random.nextFloat() < 0.06f) {
                val type = when (Random.nextInt(100)) {
                    in 0..34 -> HazardType.CONE
                    in 35..59 -> HazardType.CART
                    in 60..79 -> HazardType.PUDDLE
                    else -> HazardType.SHOPPER
                }
                hazards.add(
                    HazardEntity(
                        id = nextHazardId++,
                        type = type,
                        x = Random.nextFloat().coerceIn(0.2f, 0.8f),
                        y = -0.1f
                    )
                )
            }

            // Update hazards and check collision with player at y = 0.82f
            val playerY = 0.82f
            val playerRadius = 0.08f
            val toRemove = mutableListOf<HazardEntity>()

            for (hazard in hazards) {
                hazard.y += forwardSpeed * dt * 2.2f

                // Collision detection
                if (!hazard.isCleared && abs(hazard.y - playerY) < 0.07f && abs(hazard.x - playerX) < (hazard.type.widthNorm + playerRadius) / 2f) {
                    hazard.isCleared = true
                    if (hazard.type == HazardType.PUDDLE) {
                        slipTimer = 1.5f
                        particles.add(
                            HaulParticle(
                                x = playerX,
                                y = playerY,
                                text = "💧 HYDROPLANE SLIP!",
                                color = Color(0xFF00E5FF),
                                timestamp = System.currentTimeMillis()
                            )
                        )
                    } else {
                        freightIntegrity = (freightIntegrity - hazard.type.damage).coerceAtLeast(0f)
                        particles.add(
                            HaulParticle(
                                x = hazard.x,
                                y = hazard.y,
                                text = "💥 CRASH! -${hazard.type.damage.toInt()}%",
                                color = Color(0xFFFF5252),
                                timestamp = System.currentTimeMillis()
                            )
                        )
                    }
                }

                if (hazard.y > 1.1f) {
                    toRemove.add(hazard)
                }
            }
            hazards.removeAll(toRemove)

            // Clean expired particles
            val now = System.currentTimeMillis()
            particles.removeAll { now - it.timestamp > 800 }

            // Score update
            val currentScore = ((freightIntegrity / 100f) * 60f + (routeProgress / 100f) * 40f).coerceIn(0f, 100f)
            onScoreUpdated(currentScore, routeProgress.toInt(), freightIntegrity)

            // Finish check
            if (routeProgress >= 100f || freightIntegrity <= 0f) {
                isGameOver = true
                onFinish(currentScore)
            }
        }
    }

    // Horn action clears shoppers ahead
    fun honkHorn() {
        if (hornCooldown <= 0f && !isGameOver) {
            hornCooldown = 3.5f
            particles.add(
                HaulParticle(
                    x = playerX,
                    y = 0.7f,
                    text = "📢 EXCUSE ME! BEEP BEEP!",
                    color = Color(0xFFFFD54F),
                    timestamp = System.currentTimeMillis()
                )
            )
            // Move shoppers aside
            for (h in hazards) {
                if (h.type == HazardType.SHOPPER && h.y < 0.8f) {
                    h.x = if (h.x < 0.5f) 0.05f else 0.95f
                    h.isCleared = true
                }
            }
        }
    }

    // Emergency Handbrake
    fun engageHandbrake() {
        if (!isGameOver) {
            brakeActiveTimer = 1.2f
            particles.add(
                HaulParticle(
                    x = playerX,
                    y = 0.85f,
                    text = "🛑 EMERGENCY HANDBRAKE ENGAGED!",
                    color = Color(0xFFFF1744),
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        // Main Corridor Aisle Playfield
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Draw aisle shelves on left and right borders
                val shelfW = w * 0.12f
                drawRect(color = Color(0xFF1E293B), topLeft = Offset(0f, 0f), size = Size(shelfW, h))
                drawRect(color = Color(0xFF1E293B), topLeft = Offset(w - shelfW, 0f), size = Size(shelfW, h))

                // Shelf shelf markers / yellow safety stripe line
                drawLine(
                    color = Color(0xFFFFB300),
                    start = Offset(shelfW, 0f),
                    end = Offset(shelfW, h),
                    strokeWidth = 4f
                )
                drawLine(
                    color = Color(0xFFFFB300),
                    start = Offset(w - shelfW, 0f),
                    end = Offset(w - shelfW, h),
                    strokeWidth = 4f
                )

                // Moving floor lines to indicate forward speed
                val scrollOffset = ((routeProgress * 30f) % 60.dp.toPx())
                var yLine = scrollOffset
                while (yLine < h) {
                    drawLine(
                        color = Color(0xFF334155).copy(alpha = 0.5f),
                        start = Offset(shelfW, yLine),
                        end = Offset(w - shelfW, yLine),
                        strokeWidth = 2f
                    )
                    yLine += 60.dp.toPx()
                }

                // Draw Hazards
                for (hazard in hazards) {
                    val hx = hazard.x * w
                    val hy = hazard.y * h

                    when (hazard.type) {
                        HazardType.CONE -> {
                            val path = Path().apply {
                                moveTo(hx, hy - 16.dp.toPx())
                                lineTo(hx - 12.dp.toPx(), hy + 16.dp.toPx())
                                lineTo(hx + 12.dp.toPx(), hy + 16.dp.toPx())
                                close()
                            }
                            drawPath(path, color = hazard.type.color)
                        }
                        HazardType.PUDDLE -> {
                            drawOval(
                                color = hazard.type.color.copy(alpha = 0.6f),
                                topLeft = Offset(hx - 24.dp.toPx(), hy - 14.dp.toPx()),
                                size = Size(48.dp.toPx(), 28.dp.toPx())
                            )
                        }
                        HazardType.CART -> {
                            drawRect(
                                color = hazard.type.color,
                                topLeft = Offset(hx - 16.dp.toPx(), hy - 16.dp.toPx()),
                                size = Size(32.dp.toPx(), 32.dp.toPx())
                            )
                        }
                        HazardType.SHOPPER -> {
                            drawCircle(
                                color = hazard.type.color,
                                radius = 16.dp.toPx(),
                                center = Offset(hx, hy)
                            )
                        }
                    }
                }

                // Draw 2,000-lb Heavy Hydraulic Pallet Jack & Player
                val px = playerX * w
                val py = 0.82f * h
                val palletW = 44.dp.toPx()
                val palletH = 64.dp.toPx()

                // Pallet wooden base & boxes stacked high
                drawRect(
                    color = Color(0xFF8D6E63), // Wooden pallet
                    topLeft = Offset(px - palletW / 2f, py - palletH / 2f),
                    size = Size(palletW, palletH)
                )
                // Stacked fragile boxes
                drawRect(
                    color = Color(0xFFFFCA28), // Fragile yellow cargo box
                    topLeft = Offset(px - palletW / 2.4f, py - palletH / 2.2f),
                    size = Size(palletW * 0.83f, palletH * 0.45f)
                )
                // Hydraulic forks & steering handle
                drawCircle(
                    color = Color(0xFFD32F2F), // Red hydraulic pump handle
                    radius = 9.dp.toPx(),
                    center = Offset(px, py + palletH / 2f + 8.dp.toPx())
                )
                // Player Stocker in Green Vest pulling handle
                drawCircle(
                    color = Color(0xFF2E7D32),
                    radius = 16.dp.toPx(),
                    center = Offset(px, py + palletH / 2f + 26.dp.toPx())
                )
            }

            // Render floating text particles
            for (p in particles) {
                Box(
                    modifier = Modifier
                        .offset(x = (p.x * 280).dp, y = (p.y * 360).dp)
                        .padding(4.dp)
                ) {
                    Text(
                        text = p.text,
                        color = p.color,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Bottom Dashboard: Stability Needle, Horn & Brake Controls
        Surface(
            color = Color(0xFF0B1120),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Stability Needle Gauge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CARGO STABILITY:",
                        color = Color.LightGray,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = if (abs(stabilityNeedle) > 0.65f) "⚠️ UNSTABLE / WOBBLING!" else "STABLE (OK)",
                        color = if (abs(stabilityNeedle) > 0.65f) Color(0xFFFF5252) else Color(0xFF4CAF50),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Visual Stability needle bar
                LinearProgressIndicator(
                    progress = { ((stabilityNeedle + 1f) / 2f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = if (abs(stabilityNeedle) > 0.65f) Color(0xFFFF5252) else Color(0xFF00E676),
                    trackColor = Color(0xFF1E293B)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Lateral Steering Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "◀ STEER", color = Color.Gray, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Slider(
                        value = targetSteerX,
                        onValueChange = { targetSteerX = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFFFCA28),
                            activeTrackColor = Color(0xFFFFA000)
                        )
                    )
                    Text(text = "STEER ▶", color = Color.Gray, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Action Buttons: Excuse Me Horn & Emergency Handbrake
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { honkHorn() },
                        enabled = hornCooldown <= 0f && !isGameOver,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0288D1),
                            disabledContainerColor = Color(0xFF424242)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        val hornText = if (hornCooldown > 0f) "HORN (${hornCooldown.toInt()}s)" else "📢 'EXCUSE ME' HORN"
                        Text(
                            text = hornText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White
                        )
                    }

                    Button(
                        onClick = { engageHandbrake() },
                        enabled = brakeActiveTimer <= 0f && !isGameOver,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD32F2F),
                            disabledContainerColor = Color(0xFF424242)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "🛑 HANDBRAKE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
