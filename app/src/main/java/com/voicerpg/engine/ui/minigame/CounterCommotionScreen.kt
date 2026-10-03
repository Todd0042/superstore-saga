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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.voicerpg.engine.model.MiniGameConfig
import kotlinx.coroutines.delay
import kotlin.math.*
import kotlin.random.Random

enum class SpecialtyStationType(val label: String, val icon: String) {
    DELI("Gourmet Deli Slicer", "🧀"),
    JEWELRY_ELECTRONICS("Display Showcase Lock", "📱"),
    WARRANTY_PITCH("Extended Warranty Pitch", "💳")
}

data class CustomerOrder(
    val id: Int,
    val station: SpecialtyStationType,
    val customerDialogue: String,
    val targetWeight: Float = 0.65f, // for DELI
    val targetKeyIndex: Int = 2, // for DISPLAY CASE (0..4)
    var patience: Float = 100f // 100% down to 0%
)

data class CommotionParticle(
    val x: Float,
    val y: Float,
    val text: String,
    val color: Color,
    val timestamp: Long
)

@Composable
fun CounterCommotionScreen(
    config: MiniGameConfig,
    onScoreUpdated: (score: Float, quotaDone: Int, primaryMetric: Float) -> Unit,
    onFinish: (finalScore: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var customersServed by remember { mutableIntStateOf(0) }
    var currentCustomerIndex by remember { mutableIntStateOf(1) }
    var warrantyUpsellCount by remember { mutableIntStateOf(0) }
    var totalPatiencePreserved by remember { mutableFloatStateOf(0f) }

    // Current Customer Order State
    var currentStation by remember { mutableStateOf(SpecialtyStationType.DELI) }
    var currentDialogue by remember { mutableStateOf("I need exactly 0.65 lbs of smoked gouda, sliced thin!") }
    var targetWeight by remember { mutableFloatStateOf(0.65f) }
    var currentWeight by remember { mutableFloatStateOf(0.0f) }
    var isSlicingHold by remember { mutableStateOf(false) }

    var targetKeyIndex by remember { mutableIntStateOf(2) }
    var customerPatience by remember { mutableFloatStateOf(100f) }

    // Warranty slider needle (-1f to +1f)
    var warrantyNeedle by remember { mutableFloatStateOf(0f) }
    var warrantyNeedleSpeed by remember { mutableFloatStateOf(3.0f) }

    val particles = remember { mutableStateListOf<CommotionParticle>() }
    var isGameOver by remember { mutableStateOf(false) }

    fun generateNextCustomer() {
        val nextStation = when (Random.nextInt(3)) {
            0 -> SpecialtyStationType.DELI
            1 -> SpecialtyStationType.JEWELRY_ELECTRONICS
            else -> SpecialtyStationType.WARRANTY_PITCH
        }
        currentStation = nextStation
        customerPatience = 100f
        currentWeight = 0f

        when (nextStation) {
            SpecialtyStationType.DELI -> {
                val targets = listOf(0.45f, 0.65f, 0.75f, 1.0f, 1.25f)
                targetWeight = targets.random()
                val meats = listOf("smoked gouda", "black forest ham", "roast beef", "prosciutto", "aged cheddar")
                currentDialogue = "I need exactly ${String.format("%.2f", targetWeight)} lbs of ${meats.random()}, thin!"
            }
            SpecialtyStationType.JEWELRY_ELECTRONICS -> {
                targetKeyIndex = Random.nextInt(5)
                val items = listOf("the $1,200 OLED smartphone", "the diamond pendant", "the wireless noise-canceling headphones")
                currentDialogue = "Can you unlock the showcase for ${items.random()}? Key shape #${targetKeyIndex + 1}!"
            }
            SpecialtyStationType.WARRANTY_PITCH -> {
                currentDialogue = "I'm buying this $800 appliance. Hit the warranty sweet spot before I swipe my card!"
            }
        }
    }

    // Main game loop
    LaunchedEffect(isGameOver) {
        if (isGameOver) return@LaunchedEffect
        val quota = config.quota.coerceAtLeast(1)

        while (!isGameOver) {
            delay(33) // ~30 FPS
            val dt = 0.033f

            // Patience drain
            customerPatience = (customerPatience - dt * 6.5f).coerceAtLeast(0f)

            // Deli Slicer Hold progression
            if (isSlicingHold && currentStation == SpecialtyStationType.DELI) {
                currentWeight += dt * 0.45f
            }

            // Warranty needle oscillation
            if (currentStation == SpecialtyStationType.WARRANTY_PITCH) {
                warrantyNeedle += warrantyNeedleSpeed * dt
                if (warrantyNeedle > 1f) {
                    warrantyNeedle = 1f
                    warrantyNeedleSpeed = -abs(warrantyNeedleSpeed)
                } else if (warrantyNeedle < -1f) {
                    warrantyNeedle = -1f
                    warrantyNeedleSpeed = abs(warrantyNeedleSpeed)
                }
            }

            // Check patience expiry
            if (customerPatience <= 0f) {
                particles.add(
                    CommotionParticle(
                        x = 0.5f,
                        y = 0.4f,
                        text = "😤 CUSTOMER WALKED OUT IN DISGUST!",
                        color = Color(0xFFFF5252),
                        timestamp = System.currentTimeMillis()
                    )
                )
                generateNextCustomer()
            }

            // Clean particles
            val now = System.currentTimeMillis()
            particles.removeAll { now - it.timestamp > 800 }

            // Score calculation
            val avgPatience = if (customersServed > 0) (totalPatiencePreserved / customersServed.toFloat()) else 70f
            val currentScore = ((customersServed.toFloat() / quota.toFloat()).coerceAtMost(1f) * 60f + (avgPatience / 100f) * 25f + (warrantyUpsellCount * 5f).coerceAtMost(15f)).coerceIn(0f, 100f)
            onScoreUpdated(currentScore, customersServed, customerPatience)

            if (customersServed >= quota) {
                isGameOver = true
                onFinish(currentScore)
            }
        }
    }

    // Submit Deli Slicer portion
    fun finalizeSlice() {
        if (isGameOver) return
        val delta = abs(currentWeight - targetWeight)
        if (delta <= 0.04f) {
            customersServed++
            totalPatiencePreserved += customerPatience
            particles.add(
                CommotionParticle(
                    x = 0.5f,
                    y = 0.4f,
                    text = "🎯 PERFECT PORTION! (${String.format("%.2f", currentWeight)} LBS)",
                    color = Color(0xFF69F0AE),
                    timestamp = System.currentTimeMillis()
                )
            )
            generateNextCustomer()
        } else {
            customerPatience = (customerPatience - 35f).coerceAtLeast(0f)
            particles.add(
                CommotionParticle(
                    x = 0.5f,
                    y = 0.4f,
                    text = "❌ OFF WEIGHT! Target: ${String.format("%.2f", targetWeight)}, Got: ${String.format("%.2f", currentWeight)}",
                    color = Color(0xFFFF5252),
                    timestamp = System.currentTimeMillis()
                )
            )
            currentWeight = 0f
        }
    }

    // Choose Showcase Key
    fun selectKey(keyIdx: Int) {
        if (isGameOver) return
        if (keyIdx == targetKeyIndex) {
            customersServed++
            totalPatiencePreserved += customerPatience
            particles.add(
                CommotionParticle(
                    x = 0.5f,
                    y = 0.4f,
                    text = "🔑 CLICK! SHOWCASE UNLOCKED!",
                    color = Color(0xFF69F0AE),
                    timestamp = System.currentTimeMillis()
                )
            )
            generateNextCustomer()
        } else {
            customerPatience = (customerPatience - 30f).coerceAtLeast(0f)
            particles.add(
                CommotionParticle(
                    x = 0.5f,
                    y = 0.4f,
                    text = "🔒 WRONG KEY SHAPE! TINK TINK!",
                    color = Color(0xFFFF5252),
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    // Pitch Warranty QTE
    fun pitchWarrantyTap() {
        if (isGameOver) return
        val isHit = abs(warrantyNeedle) < 0.35f
        if (isHit) {
            warrantyUpsellCount++
            customersServed++
            totalPatiencePreserved += customerPatience
            particles.add(
                CommotionParticle(
                    x = 0.5f,
                    y = 0.4f,
                    text = "💳 UPSELL SUCCESS! +29% APR WARRANTY SOLD!",
                    color = Color(0xFFFFD54F),
                    timestamp = System.currentTimeMillis()
                )
            )
            generateNextCustomer()
        } else {
            customerPatience = (customerPatience - 40f).coerceAtLeast(0f)
            particles.add(
                CommotionParticle(
                    x = 0.5f,
                    y = 0.4f,
                    text = "🚫 CUSTOMER SAID NO TO WARRANTY!",
                    color = Color(0xFFFF5252),
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
        // Customer Order & Station Display
        Surface(
            color = Color(0xFF1E293B),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${currentStation.icon} ${currentStation.label}",
                        color = Color(0xFFFFD54F),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "PATIENCE: ${customerPatience.toInt()}%",
                        color = if (customerPatience > 40f) Color(0xFF4CAF50) else Color(0xFFFF5252),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                LinearProgressIndicator(
                    progress = { (customerPatience / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 8.dp)
                        .height(6.dp),
                    color = if (customerPatience > 40f) Color(0xFF4CAF50) else Color(0xFFFF5252),
                    trackColor = Color(0xFF0F172A)
                )

                Text(
                    text = "\"$currentDialogue\"",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Active Interactive Specialty Station Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            when (currentStation) {
                SpecialtyStationType.DELI -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .background(Color(0xFF1E293B), RoundedCornerShape(12.dp))
                            .padding(20.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "⚖️ DIGITAL SLICER SCALE",
                            color = Color(0xFFFFD54F),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "${String.format("%.2f", currentWeight)} LBS",
                            color = if (abs(currentWeight - targetWeight) <= 0.04f) Color(0xFF69F0AE) else Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "TARGET: ${String.format("%.2f", targetWeight)} LBS (±0.04)",
                            color = Color.LightGray,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = { },
                                modifier = Modifier
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onPress = {
                                                isSlicingHold = true
                                                tryAwaitRelease()
                                                isSlicingHold = false
                                            }
                                        )
                                    },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (isSlicingHold) "🔪 SLICING..." else "🔪 HOLD TO SLICE",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = { finalizeSlice() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "🏷️ WEIGH & WRAP",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                SpecialtyStationType.JEWELRY_ELECTRONICS -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .background(Color(0xFF1E293B), RoundedCornerShape(12.dp))
                            .padding(20.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "🔑 BRASS SHOWCASE KEY RING",
                            color = Color(0xFFFFD54F),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "SELECT MATCHING LOCK SHAPE #${targetKeyIndex + 1}",
                            color = Color.LightGray,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 5 Keys Array
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            for (k in 0..4) {
                                Button(
                                    onClick = { selectKey(k) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFFFB300)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp)
                                ) {
                                    Text(
                                        text = "🔑 #${k + 1}",
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }

                SpecialtyStationType.WARRANTY_PITCH -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .background(Color(0xFF1E293B), RoundedCornerShape(12.dp))
                            .padding(20.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "💳 29% APR EXTENDED WARRANTY PITCH",
                            color = Color(0xFFFFD54F),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "TAP [PITCH] WHEN NEEDLE HITS CENTER!",
                            color = Color.LightGray,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Needle Track
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp)
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
                            val needleXOffset = (warrantyNeedle * 110).dp
                            Box(
                                modifier = Modifier
                                    .offset(x = needleXOffset)
                                    .width(6.dp)
                                    .fillMaxHeight()
                                    .background(Color(0xFFFF1744))
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { pitchWarrantyTap() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD54F)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "🤝 PITCH WARRANTY NOW!",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // Floating feedback particles
            for (p in particles) {
                Box(
                    modifier = Modifier
                        .offset(x = (p.x * 240).dp, y = (p.y * 300).dp)
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
    }
}
