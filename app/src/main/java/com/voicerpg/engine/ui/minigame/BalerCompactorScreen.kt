package com.voicerpg.engine.ui.minigame

import androidx.compose.animation.core.*
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voicerpg.engine.model.MiniGameConfig
import kotlinx.coroutines.delay
import kotlin.math.*
import kotlin.random.Random

enum class BalerItemType(val label: String, val isCardboard: Boolean, val color: Color) {
    CORRUGATED_BOX("Corrugated Box", true, Color(0xFFBCAAA4)),
    APPLIANCE_CARTON("Heavy Carton", true, Color(0xFFA1887F)),
    CRATE("Dense Crate", true, Color(0xFF8D6E63)),
    PLASTIC_WRAP("⚠️ Plastic Shrink Wrap", false, Color(0xFF80DEEA)),
    METAL_BANDING("⚠️ Metal Strapping Band", false, Color(0xFFFF5252)),
    WOOD_CHUNK("⚠️ Wood Pallet Chunk", false, Color(0xFFFFB74D))
}

data class BalerConveyorItem(
    val id: Int,
    val type: BalerItemType,
    var x: Float // 0f (left) to 1f (right entry into baler)
)

data class BalerParticle(
    val x: Float,
    val y: Float,
    val text: String,
    val color: Color,
    val timestamp: Long
)

@Composable
fun BalerCompactorScreen(
    config: MiniGameConfig,
    onScoreUpdated: (score: Float, quotaDone: Int, primaryMetric: Float) -> Unit,
    onFinish: (finalScore: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var chamberPsi by remember { mutableFloatStateOf(600f) } // Target: 2,800 PSI
    var balesTied by remember { mutableIntStateOf(0) }
    var hadJam by remember { mutableStateOf(false) }
    var currentWireTyingIndex by remember { mutableIntStateOf(0) } // 0 to 4 (wire 1..4)
    var isTyingPhase by remember { mutableStateOf(false) }

    // Wire QTE oscillating needle position (-1f to +1f)
    var wireNeedlePos by remember { mutableFloatStateOf(0f) }
    var wireNeedleSpeed by remember { mutableFloatStateOf(2.5f) }
    var successfulWireTaps by remember { mutableIntStateOf(0) }

    val conveyorItems = remember { mutableStateListOf<BalerConveyorItem>() }
    val particles = remember { mutableStateListOf<BalerParticle>() }
    var nextItemId by remember { mutableIntStateOf(1) }
    var isGameOver by remember { mutableStateOf(false) }

    // Main animation and conveyor loop
    LaunchedEffect(isGameOver, isTyingPhase) {
        if (isGameOver) return@LaunchedEffect
        val quota = config.quota.coerceAtLeast(1)

        while (!isGameOver) {
            delay(33) // ~30 FPS
            val dt = 0.033f

            if (isTyingPhase) {
                // Oscillate wire needle
                wireNeedlePos += wireNeedleSpeed * dt
                if (wireNeedlePos > 1f) {
                    wireNeedlePos = 1f
                    wireNeedleSpeed = -abs(wireNeedleSpeed)
                } else if (wireNeedlePos < -1f) {
                    wireNeedlePos = -1f
                    wireNeedleSpeed = abs(wireNeedleSpeed)
                }
            } else {
                // Conveyor Belt Phase
                // Spawn items if empty or spaced
                if (conveyorItems.size < 4 && Random.nextFloat() < 0.07f) {
                    val isHazard = Random.nextFloat() < 0.35f
                    val type = if (isHazard) {
                        when (Random.nextInt(3)) {
                            0 -> BalerItemType.PLASTIC_WRAP
                            1 -> BalerItemType.METAL_BANDING
                            else -> BalerItemType.WOOD_CHUNK
                        }
                    } else {
                        when (Random.nextInt(3)) {
                            0 -> BalerItemType.CORRUGATED_BOX
                            1 -> BalerItemType.APPLIANCE_CARTON
                            else -> BalerItemType.CRATE
                        }
                    }
                    conveyorItems.add(BalerConveyorItem(id = nextItemId++, type = type, x = 0f))
                }

                // Advance conveyor items
                val toRemove = mutableListOf<BalerConveyorItem>()
                for (item in conveyorItems) {
                    item.x += 0.22f * dt
                    // Auto-enter chamber when reaching right edge (x >= 0.85f)
                    if (item.x >= 0.85f) {
                        toRemove.add(item)
                        if (item.type.isCardboard) {
                            chamberPsi = (chamberPsi + 350f).coerceAtMost(2800f)
                            particles.add(
                                BalerParticle(
                                    x = 0.85f,
                                    y = 0.5f,
                                    text = "📦 CRUSH! +350 PSI",
                                    color = Color(0xFF69F0AE),
                                    timestamp = System.currentTimeMillis()
                                )
                            )
                        } else {
                            // Foreign hazard entered chamber -> JAM!
                            hadJam = true
                            chamberPsi = (chamberPsi - 400f).coerceAtLeast(400f)
                            particles.add(
                                BalerParticle(
                                    x = 0.85f,
                                    y = 0.5f,
                                    text = "🚨 HYDRAULIC JAM! -400 PSI",
                                    color = Color(0xFFFF1744),
                                    timestamp = System.currentTimeMillis()
                                )
                            )
                        }
                    }
                }
                conveyorItems.removeAll(toRemove)

                // Check if chamber reached maximum pressure (2,800 PSI) -> trigger tying phase
                if (chamberPsi >= 2800f) {
                    isTyingPhase = true
                    currentWireTyingIndex = 0
                    particles.add(
                        BalerParticle(
                            x = 0.5f,
                            y = 0.35f,
                            text = "🔒 CHAMBER LOCKED! THREAD 4 WIRES!",
                            color = Color(0xFFFFD54F),
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
            }

            // Clean expired particles
            val now = System.currentTimeMillis()
            particles.removeAll { now - it.timestamp > 800 }

            // Score calculation
            val wirePrecisionPct = if (currentWireTyingIndex > 0) (successfulWireTaps.toFloat() / currentWireTyingIndex.toFloat()) else 1f
            val jamBonus = if (!hadJam) 30f else 0f
            val currentScore = ((balesTied.toFloat() / quota.toFloat()).coerceAtMost(1f) * 50f + jamBonus + wirePrecisionPct * 20f).coerceIn(0f, 100f)
            onScoreUpdated(currentScore, balesTied, chamberPsi)

            if (balesTied >= quota) {
                isGameOver = true
                onFinish(currentScore)
            }
        }
    }

    // Action: Swipe/Reject Foreign Item from conveyor
    fun rejectFirstItem() {
        val first = conveyorItems.firstOrNull() ?: return
        conveyorItems.remove(first)
        if (!first.type.isCardboard) {
            particles.add(
                BalerParticle(
                    x = first.x,
                    y = 0.65f,
                    text = "🗑️ REJECTED HAZARD! (+OSHA SAFE)",
                    color = Color(0xFF00E5FF),
                    timestamp = System.currentTimeMillis()
                )
            )
        } else {
            particles.add(
                BalerParticle(
                    x = first.x,
                    y = 0.65f,
                    text = "⚠️ REJECTED CLEAN CARDBOARD",
                    color = Color(0xFFFFB74D),
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    // Action: Fast-Load First Cardboard into baler
    fun loadFirstItem() {
        val first = conveyorItems.firstOrNull() ?: return
        conveyorItems.remove(first)
        if (first.type.isCardboard) {
            chamberPsi = (chamberPsi + 450f).coerceAtMost(2800f)
            particles.add(
                BalerParticle(
                    x = 0.85f,
                    y = 0.5f,
                    text = "⬇️ FAST-LOAD! +450 PSI",
                    color = Color(0xFF69F0AE),
                    timestamp = System.currentTimeMillis()
                )
            )
        } else {
            hadJam = true
            chamberPsi = (chamberPsi - 400f).coerceAtLeast(400f)
            particles.add(
                BalerParticle(
                    x = 0.85f,
                    y = 0.5f,
                    text = "🚨 HYDRAULIC JAM! -400 PSI",
                    color = Color(0xFFFF1744),
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    // Action: Wire Threading QTE Tap
    fun threadWireTap() {
        if (!isTyingPhase || isGameOver) return
        val isHit = abs(wireNeedlePos) < 0.35f
        if (isHit) {
            successfulWireTaps++
            particles.add(
                BalerParticle(
                    x = 0.5f,
                    y = 0.4f,
                    text = "🧵 WIRE ${currentWireTyingIndex + 1} TIED! (PERFECT)",
                    color = Color(0xFF69F0AE),
                    timestamp = System.currentTimeMillis()
                )
            )
        } else {
            particles.add(
                BalerParticle(
                    x = 0.5f,
                    y = 0.4f,
                    text = "⚠️ WIRE SLOPPY / LOOSE",
                    color = Color(0xFFFFB74D),
                    timestamp = System.currentTimeMillis()
                )
            )
        }

        currentWireTyingIndex++
        if (currentWireTyingIndex >= 4) {
            // Bale complete! Eject and reset chamber
            balesTied++
            isTyingPhase = false
            chamberPsi = 600f
            currentWireTyingIndex = 0
            particles.add(
                BalerParticle(
                    x = 0.5f,
                    y = 0.3f,
                    text = "🎉 BALE EJECTED & STACKED! (+1 BALE)",
                    color = Color(0xFFFFD54F),
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
        // Baler Chamber & Conveyor Canvas
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Draw Backroom Concrete & Baler Housing
                val chamberLeft = w * 0.45f
                val chamberRight = w * 0.95f
                val chamberTop = h * 0.1f
                val chamberBottom = h * 0.72f

                // Massive Green Baler Housing
                drawRect(
                    color = Color(0xFF1B5E20), // Industrial dark green
                    topLeft = Offset(chamberLeft, chamberTop),
                    size = Size(chamberRight - chamberLeft, chamberBottom - chamberTop)
                )

                // Yellow Warning hazard stripes on baler top
                drawLine(
                    color = Color(0xFFFFD600),
                    start = Offset(chamberLeft, chamberTop),
                    end = Offset(chamberRight, chamberTop),
                    strokeWidth = 8f
                )

                // Hydraulic Ram crushing down proportional to PSI
                val ramProgress = (chamberPsi / 2800f).coerceIn(0.1f, 0.95f)
                val ramY = chamberTop + (chamberBottom - chamberTop) * (1f - ramProgress) * 0.5f
                drawRect(
                    color = Color(0xFF424242), // Steel ram
                    topLeft = Offset(chamberLeft + 6.dp.toPx(), chamberTop),
                    size = Size(chamberRight - chamberLeft - 12.dp.toPx(), ramY - chamberTop)
                )

                // Compressed Cardboard Bale inside chamber
                drawRect(
                    color = Color(0xFF8D6E63),
                    topLeft = Offset(chamberLeft + 8.dp.toPx(), ramY),
                    size = Size(chamberRight - chamberLeft - 16.dp.toPx(), chamberBottom - ramY)
                )

                // 4 Vertical Steel Wire Guides across baler chamber
                val wireSpacing = (chamberRight - chamberLeft) / 5f
                for (wireIdx in 1..4) {
                    val wireX = chamberLeft + wireSpacing * wireIdx
                    val isTied = isTyingPhase && (wireIdx <= currentWireTyingIndex)
                    drawLine(
                        color = if (isTied) Color(0xFFFFD54F) else Color(0xFFB0BEC5).copy(alpha = 0.6f),
                        start = Offset(wireX, chamberTop),
                        end = Offset(wireX, chamberBottom),
                        strokeWidth = if (isTied) 4f else 2f
                    )
                }

                // Draw Conveyor belt feed from left into baler (bottom half)
                val beltY = h * 0.76f
                val beltHeight = 36.dp.toPx()
                drawRect(
                    color = Color(0xFF263238),
                    topLeft = Offset(0f, beltY),
                    size = Size(chamberLeft, beltHeight)
                )
                // Conveyor rollers
                var rollerX = 0f
                while (rollerX < chamberLeft) {
                    drawLine(
                        color = Color(0xFF455A64),
                        start = Offset(rollerX, beltY),
                        end = Offset(rollerX, beltY + beltHeight),
                        strokeWidth = 2f
                    )
                    rollerX += 20.dp.toPx()
                }

                // Draw Conveyor Items
                for (item in conveyorItems) {
                    val ix = item.x * chamberLeft
                    val iy = beltY - 18.dp.toPx()
                    drawRect(
                        color = item.type.color,
                        topLeft = Offset(ix - 18.dp.toPx(), iy),
                        size = Size(36.dp.toPx(), 26.dp.toPx())
                    )
                }
            }

            // Wire Threading QTE Overlay if in tying phase
            if (isTyingPhase) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .background(Color(0xFF1E293B), RoundedCornerShape(12.dp))
                            .padding(16.dp)
                            .width(280.dp)
                    ) {
                        Text(
                            text = "🧵 WIRE ${currentWireTyingIndex + 1} OF 4",
                            color = Color(0xFFFFD54F),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "TAP [THREAD] WHEN NEEDLE HITS CENTER!",
                            color = Color.LightGray,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Oscillating Needle Track
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(28.dp)
                                .background(Color(0xFF0F172A), RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            // Green Sweet Spot in Center
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(80.dp)
                                    .background(Color(0xFF00C853).copy(alpha = 0.5f))
                            )

                            // Moving Needle
                            val needleXOffset = (wireNeedlePos * 100).dp
                            Box(
                                modifier = Modifier
                                    .offset(x = needleXOffset)
                                    .width(6.dp)
                                    .fillMaxHeight()
                                    .background(Color(0xFFFF1744))
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { threadWireTap() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD54F)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "🧵 THREAD WIRE NOW!",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // Floating text particles
            for (p in particles) {
                Box(
                    modifier = Modifier
                        .offset(x = (p.x * 260).dp, y = (p.y * 340).dp)
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

        // Bottom Dashboard: Pressure Gauge & Conveyor Actions
        Surface(
            color = Color(0xFF0B1120),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Pressure PSI Gauge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "HYDRAULIC PRESSURE: ${chamberPsi.toInt()} / 2,800 PSI",
                        color = if (chamberPsi >= 2400f) Color(0xFFFFD54F) else Color(0xFF69F0AE),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = if (hadJam) "⚠️ JAM DETECTED" else "OSHA: SAFE",
                        color = if (hadJam) Color(0xFFFF5252) else Color(0xFF4CAF50),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                LinearProgressIndicator(
                    progress = { (chamberPsi / 2800f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = if (chamberPsi >= 2400f) Color(0xFFFFD54F) else Color(0xFF00E676),
                    trackColor = Color(0xFF1E293B)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Actions: Reject foreign hazard vs Fast Load
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { rejectFirstItem() },
                        enabled = !isTyingPhase && conveyorItems.isNotEmpty() && !isGameOver,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD32F2F),
                            disabledContainerColor = Color(0xFF424242)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "🗑️ REJECT ITEM",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White
                        )
                    }

                    Button(
                        onClick = { loadFirstItem() },
                        enabled = !isTyingPhase && conveyorItems.isNotEmpty() && !isGameOver,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32),
                            disabledContainerColor = Color(0xFF424242)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "⬇️ FAST-LOAD",
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
