package com.voicerpg.engine.ui.minigame

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voicerpg.engine.model.MiniGameConfig
import kotlinx.coroutines.delay
import kotlin.math.*
import kotlin.random.Random

enum class ShopperType(
    val label: String,
    val speed: Float,
    val maxHits: Int,
    val color: Color,
    val sizeRadius: Float,
    val icon: String
) {
    CASUAL("Casual", 1.0f, 1, Color(0xFF64B5F6), 20f, "🛒"),
    DAD("Power Dad", 2.2f, 2, Color(0xFFFFB74D), 22f, "🏃"),
    COUPON("Coupon Pro", 1.3f, 4, Color(0xFFBA68C8), 24f, "📁"),
    SCOOTER("Scooter", 0.7f, 6, Color(0xFFE57373), 30f, "🛵"),
    TODDLER("Toddler Swarm", 2.8f, 1, Color(0xFFFFD54F), 16f, "⚡")
}

data class ShopperEntity(
    val id: Int,
    val type: ShopperType,
    var x: Float, // Normalized 0f - 1f
    var y: Float, // Normalized 0f - 1f (0 is top entrance, 1 is register)
    var currentHits: Int,
    var isFrozen: Boolean = false,
    var freezeTimer: Float = 0f
)

data class LaserBeam(
    val startX: Float,
    val startY: Float,
    val targetX: Float,
    val targetY: Float,
    val timestamp: Long
)

data class ScanParticle(
    val x: Float,
    val y: Float,
    val text: String,
    val color: Color,
    val timestamp: Long
)

@Composable
fun BarcodeBlasterScreen(
    config: MiniGameConfig,
    onScoreUpdated: (score: Float, quotaDone: Int, primaryMetric: Float) -> Unit,
    onFinish: (finalScore: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var registerHealth by remember { mutableFloatStateOf(100f) }
    var itemsScanned by remember { mutableIntStateOf(0) }
    var playerLaneX by remember { mutableFloatStateOf(0.5f) } // 0f to 1f
    var empCooldown by remember { mutableFloatStateOf(0f) }
    var empActiveTimer by remember { mutableFloatStateOf(0f) }

    val shoppers = remember { mutableStateListOf<ShopperEntity>() }
    val laserBeams = remember { mutableStateListOf<LaserBeam>() }
    val particles = remember { mutableStateListOf<ScanParticle>() }
    var nextShopperId by remember { mutableIntStateOf(1) }
    var isGameOver by remember { mutableStateOf(false) }

    // Game loop running at ~30 FPS
    LaunchedEffect(isGameOver) {
        if (isGameOver) return@LaunchedEffect
        val quota = config.quota.coerceAtLeast(1)

        while (!isGameOver) {
            delay(33) // ~30 FPS
            val dt = 0.033f

            // Update EMP cooldown & active timer
            if (empCooldown > 0f) empCooldown = (empCooldown - dt).coerceAtLeast(0f)
            if (empActiveTimer > 0f) empActiveTimer = (empActiveTimer - dt).coerceAtLeast(0f)

            // Spawn new shoppers
            if (shoppers.size < 6 && Random.nextFloat() < 0.06f) {
                val type = when (Random.nextInt(100)) {
                    in 0..34 -> ShopperType.CASUAL
                    in 35..59 -> ShopperType.DAD
                    in 60..79 -> ShopperType.COUPON
                    in 80..89 -> ShopperType.SCOOTER
                    else -> ShopperType.TODDLER
                }
                shoppers.add(
                    ShopperEntity(
                        id = nextShopperId++,
                        type = type,
                        x = Random.nextFloat().coerceIn(0.15f, 0.85f),
                        y = 0.05f,
                        currentHits = 0
                    )
                )
            }

            // Move shoppers
            val toRemove = mutableListOf<ShopperEntity>()
            for (shopper in shoppers) {
                if (empActiveTimer > 0f) {
                    shopper.isFrozen = true
                } else {
                    shopper.isFrozen = false
                    val speedScale = 0.08f * shopper.type.speed
                    shopper.y += speedScale * dt

                    // Slight horizontal weave for toddlers
                    if (shopper.type == ShopperType.TODDLER) {
                        shopper.x = (shopper.x + sin(shopper.y * 20f) * 0.005f).coerceIn(0.1f, 0.9f)
                    }

                    // Check if shopper reached register barricade (y >= 0.85f)
                    if (shopper.y >= 0.85f) {
                        toRemove.add(shopper)
                        val damage = if (shopper.type == ShopperType.SCOOTER) 20f else 10f
                        registerHealth = (registerHealth - damage).coerceAtLeast(0f)
                        particles.add(
                            ScanParticle(
                                x = shopper.x,
                                y = 0.85f,
                                text = "💥 CRASH! -${damage.toInt()}%",
                                color = Color(0xFFFF5252),
                                timestamp = System.currentTimeMillis()
                            )
                        )
                    }
                }
            }
            shoppers.removeAll(toRemove)

            // Clean expired beams and particles
            val now = System.currentTimeMillis()
            laserBeams.removeAll { now - it.timestamp > 120 }
            particles.removeAll { now - it.timestamp > 900 }

            // Check game over condition
            val currentScore = ((itemsScanned.toFloat() / quota.toFloat()).coerceAtMost(1f) * 70f + (registerHealth / 100f) * 30f).coerceIn(0f, 100f)
            onScoreUpdated(currentScore, itemsScanned, registerHealth)

            if (itemsScanned >= quota || registerHealth <= 0f) {
                isGameOver = true
                onFinish(currentScore)
            }
        }
    }

    // Fire laser at target coordinates (normalized)
    fun fireLaserAt(targetX: Float, targetY: Float) {
        if (isGameOver) return
        val startX = playerLaneX
        val startY = 0.88f

        laserBeams.add(
            LaserBeam(
                startX = startX,
                startY = startY,
                targetX = targetX,
                targetY = targetY,
                timestamp = System.currentTimeMillis()
            )
        )

        // Hit detection on shoppers
        val hitShopper = shoppers.firstOrNull { s ->
            val dx = abs(s.x - targetX)
            val dy = abs(s.y - targetY)
            dx < 0.12f && dy < 0.12f
        }

        if (hitShopper != null) {
            hitShopper.currentHits++
            val price = "$${Random.nextInt(2, 45)}.${Random.nextInt(10, 99)}"
            particles.add(
                ScanParticle(
                    x = hitShopper.x,
                    y = hitShopper.y,
                    text = "BEEP! $price",
                    color = Color(0xFF69F0AE),
                    timestamp = System.currentTimeMillis()
                )
            )

            if (hitShopper.currentHits >= hitShopper.type.maxHits) {
                shoppers.remove(hitShopper)
                itemsScanned++
            }
        }
    }

    // Trigger Price Check EMP
    fun triggerEmp() {
        if (empCooldown <= 0f && !isGameOver) {
            empActiveTimer = 3.0f
            empCooldown = 10.0f
            particles.add(
                ScanParticle(
                    x = 0.5f,
                    y = 0.45f,
                    text = "⚡ PRICE CHECK EMP! ALL FREEZE! ⚡",
                    color = Color(0xFFFFEA00),
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
        // Main 2D Canvas Playfield
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .pointerInput(isGameOver) {
                    detectTapGestures { offset ->
                        val normX = (offset.x / size.width).coerceIn(0f, 1f)
                        val normY = (offset.y / size.height).coerceIn(0f, 1f)
                        fireLaserAt(normX, normY)
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Draw aisle checkout floor tiles
                val tileStep = 40.dp.toPx()
                var yGrid = 0f
                while (yGrid < h) {
                    drawLine(
                        color = Color(0xFF1E293B),
                        start = Offset(0f, yGrid),
                        end = Offset(w, yGrid),
                        strokeWidth = 1f
                    )
                    yGrid += tileStep
                }

                // Draw Register Barricade Belt at y = 0.85
                val beltY = h * 0.85f
                drawRect(
                    color = Color(0xFF334155),
                    topLeft = Offset(0f, beltY),
                    size = Size(w, h * 0.15f)
                )
                // Register conveyor belt texture line
                drawLine(
                    color = Color(0xFF64748B),
                    start = Offset(0f, beltY),
                    end = Offset(w, beltY),
                    strokeWidth = 6f
                )

                // Draw Register lane dividers
                val laneCount = 4
                for (i in 1 until laneCount) {
                    val laneX = w * (i.toFloat() / laneCount)
                    drawLine(
                        color = Color(0xFF475569),
                        start = Offset(laneX, 0f),
                        end = Offset(laneX, beltY),
                        strokeWidth = 2f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                    )
                }

                // Draw Active Laser Beams
                for (beam in laserBeams) {
                    val start = Offset(beam.startX * w, beam.startY * h)
                    val end = Offset(beam.targetX * w, beam.targetY * h)
                    // Outer glow
                    drawLine(
                        color = Color(0xFFFF1744).copy(alpha = 0.6f),
                        start = start,
                        end = end,
                        strokeWidth = 8f
                    )
                    // Core bright beam
                    drawLine(
                        color = Color(0xFFFFFFFF),
                        start = start,
                        end = end,
                        strokeWidth = 2.5f
                    )
                }

                // Draw Shoppers
                for (shopper in shoppers) {
                    val sx = shopper.x * w
                    val sy = shopper.y * h
                    val r = shopper.type.sizeRadius.dp.toPx()

                    // EMP Freeze Aura
                    if (shopper.isFrozen) {
                        drawCircle(
                            color = Color(0xFF00E5FF).copy(alpha = 0.4f),
                            radius = r * 1.5f,
                            center = Offset(sx, sy)
                        )
                    }

                    // Shopper Base Body
                    drawCircle(
                        color = shopper.type.color,
                        radius = r,
                        center = Offset(sx, sy)
                    )

                    // Shopper Health / Hit pips
                    val remaining = shopper.type.maxHits - shopper.currentHits
                    val pipRadius = 3.dp.toPx()
                    val totalPipsWidth = shopper.type.maxHits * pipRadius * 2.5f
                    var startPipX = sx - totalPipsWidth / 2f
                    for (p in 0 until shopper.type.maxHits) {
                        val pipColor = if (p < remaining) Color(0xFF00E676) else Color(0xFF757575)
                        drawCircle(
                            color = pipColor,
                            radius = pipRadius,
                            center = Offset(startPipX, sy - r - 8.dp.toPx())
                        )
                        startPipX += pipRadius * 2.5f
                    }
                }

                // Draw Player Cashier behind register belt
                val px = playerLaneX * w
                val py = beltY + (h * 0.06f)
                drawCircle(
                    color = Color(0xFF00C853), // Green Vest
                    radius = 22.dp.toPx(),
                    center = Offset(px, py)
                )
                // Laser Scanner Gun nozzle
                drawCircle(
                    color = Color(0xFFFF1744),
                    radius = 7.dp.toPx(),
                    center = Offset(px, beltY - 4.dp.toPx())
                )
            }

            // Render floating text particles
            for (p in particles) {
                Box(
                    modifier = Modifier
                        .offset(x = (p.x * 300).dp, y = (p.y * 400).dp)
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

        // Bottom Controls Toolbar
        Surface(
            color = Color(0xFF0B1120),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Register health & items scanned row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "REGISTER BARRICADE: ${registerHealth.toInt()}%",
                            color = if (registerHealth > 40f) Color(0xFF4CAF50) else Color(0xFFFF5252),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        LinearProgressIndicator(
                            progress = { (registerHealth / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .width(140.dp)
                                .height(6.dp),
                            color = if (registerHealth > 40f) Color(0xFF4CAF50) else Color(0xFFFF5252),
                            trackColor = Color(0xFF1E293B)
                        )
                    }

                    // Special EMP Shockwave Button
                    Button(
                        onClick = { triggerEmp() },
                        enabled = empCooldown <= 0f && !isGameOver,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFB300),
                            disabledContainerColor = Color(0xFF424242)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        val empLabel = if (empCooldown > 0f) "EMP (${empCooldown.toInt()}s)" else "⚡ PRICE CHECK EMP"
                        Text(
                            text = empLabel,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Cashier Lateral Movement Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "◀ LANE",
                        color = Color.Gray,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Slider(
                        value = playerLaneX,
                        onValueChange = { playerLaneX = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF00E676),
                            activeTrackColor = Color(0xFF00C853)
                        )
                    )
                    Text(
                        text = "LANE ▶",
                        color = Color.Gray,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
