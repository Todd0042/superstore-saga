package com.voicerpg.engine.ui.minigame

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.voicerpg.engine.engine.MiniGameSimulator
import com.voicerpg.engine.model.MiniGameConfig
import com.voicerpg.engine.model.MiniGameOutcome
import com.voicerpg.engine.model.MiniGameOutcomeTier
import kotlinx.coroutines.delay

@Composable
fun MiniGameContainerScreen(
    config: MiniGameConfig,
    onFinish: (outcome: MiniGameOutcome) -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    var timeRemainingSeconds by remember { mutableIntStateOf(config.durationSeconds.coerceAtLeast(15)) }
    var currentScore by remember { mutableFloatStateOf(0f) }
    var currentQuotaDone by remember { mutableIntStateOf(0) }
    var primaryMetric by remember { mutableFloatStateOf(100f) }
    var finalOutcome by remember { mutableStateOf<MiniGameOutcome?>(null) }

    // Countdown Timer Loop
    LaunchedEffect(finalOutcome) {
        if (finalOutcome != null) return@LaunchedEffect
        while (timeRemainingSeconds > 0 && finalOutcome == null) {
            delay(1000)
            timeRemainingSeconds--
            if (timeRemainingSeconds <= 0 && finalOutcome == null) {
                // Time up! Compute outcome from current score
                finalOutcome = MiniGameSimulator.outcomeFromScore(config, currentScore)
            }
        }
    }

    val minutes = timeRemainingSeconds / 60
    val seconds = timeRemainingSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Arcade Header Bar
            Surface(
                color = Color(0xFF1E293B),
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = config.title.uppercase(),
                            color = Color(0xFFFFD54F),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = if (config.department.isNotBlank()) config.department else "DEPARTMENT CHALLENGE",
                            color = Color.LightGray,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Timer Indicator
                        Surface(
                            color = if (timeRemainingSeconds <= 10) Color(0xFFB71C1C) else Color(0xFF0F172A),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "⏱️ $timeFormatted",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        // Skip / Auto-Play Button
                        Button(
                            onClick = {
                                if (finalOutcome == null) {
                                    val simulated = MiniGameSimulator.simulate(config)
                                    finalOutcome = simulated
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF374151),
                                contentColor = Color(0xFFFFD54F)
                            ),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "⏩ SKIP",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // Sub-header stats row (Quota and Score)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E293B).copy(alpha = 0.6f))
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "QUOTA PROGRESS: $currentQuotaDone / ${config.quota}",
                    color = Color(0xFF81D4FA),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "SCORE: ${currentScore.toInt()} / 100",
                    color = Color(0xFFA5D6A7),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Active Mini-Game View Dispatch
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (config.id) {
                    "barcode_blaster" -> {
                        BarcodeBlasterScreen(
                            config = config,
                            onScoreUpdated = { score, quotaDone, primary ->
                                currentScore = score
                                currentQuotaDone = quotaDone
                                primaryMetric = primary
                            },
                            onFinish = { finalScore ->
                                finalOutcome = MiniGameSimulator.outcomeFromScore(config, finalScore)
                            }
                        )
                    }

                    "pallet_haul", "cart_corral" -> {
                        PalletHaulScreen(
                            config = config,
                            onScoreUpdated = { score, quotaDone, primary ->
                                currentScore = score
                                currentQuotaDone = quotaDone
                                primaryMetric = primary
                            },
                            onFinish = { finalScore ->
                                finalOutcome = MiniGameSimulator.outcomeFromScore(config, finalScore)
                            }
                        )
                    }

                    "baler_compactor" -> {
                        BalerCompactorScreen(
                            config = config,
                            onScoreUpdated = { score, quotaDone, primary ->
                                currentScore = score
                                currentQuotaDone = quotaDone
                                primaryMetric = primary
                            },
                            onFinish = { finalScore ->
                                finalOutcome = MiniGameSimulator.outcomeFromScore(config, finalScore)
                            }
                        )
                    }

                    "counter_commotion" -> {
                        CounterCommotionScreen(
                            config = config,
                            onScoreUpdated = { score, quotaDone, primary ->
                                currentScore = score
                                currentQuotaDone = quotaDone
                                primaryMetric = primary
                            },
                            onFinish = { finalScore ->
                                finalOutcome = MiniGameSimulator.outcomeFromScore(config, finalScore)
                            }
                        )
                    }

                    else -> {
                        // Fallback generic interactive screen
                        BarcodeBlasterScreen(
                            config = config,
                            onScoreUpdated = { score, quotaDone, primary ->
                                currentScore = score
                                currentQuotaDone = quotaDone
                                primaryMetric = primary
                            },
                            onFinish = { finalScore ->
                                finalOutcome = MiniGameSimulator.outcomeFromScore(config, finalScore)
                            }
                        )
                    }
                }
            }
        }

        // Outcome Summary Modal Dialog
        finalOutcome?.let { outcome ->
            Dialog(onDismissRequest = { /* Must click continue */ }) {
                Surface(
                    color = Color(0xFF1E293B),
                    shape = RoundedCornerShape(16.dp),
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val (tierTitle, tierColor, tierEmoji) = when (outcome.tier) {
                            MiniGameOutcomeTier.FLAWLESS -> Triple("FLAWLESS SPRINT", Color(0xFF00E676), "🏆")
                            MiniGameOutcomeTier.SOLID -> Triple("SOLID ROUTINE", Color(0xFF4CAF50), "✅")
                            MiniGameOutcomeTier.CLUMSY -> Triple("CLUMSY SCRAMBLE", Color(0xFFFFB74D), "⚠️")
                            MiniGameOutcomeTier.DISASTER -> Triple("MINOR DISASTER", Color(0xFFFF5252), "💥")
                        }

                        Text(
                            text = "$tierEmoji $tierTitle",
                            color = tierColor,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            fontFamily = FontFamily.Monospace
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "PRODUCTIVITY SCORE: ${outcome.score.toInt()} / 100",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Narrative Blurb
                        Surface(
                            color = Color(0xFF0F172A),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "\"${outcome.narrativeBlurb}\"",
                                color = Color.LightGray,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Stat adjustments display
                        if (outcome.metricDeltas.isNotEmpty()) {
                            Text(
                                text = "SHIFT METRIC ADJUSTMENTS:",
                                color = Color(0xFFFFD54F),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                outcome.metricDeltas.forEach { (metricId, delta) ->
                                    val sign = if (delta >= 0f) "+" else ""
                                    val deltaColor = if (delta >= 0f) Color(0xFF69F0AE) else Color(0xFFFF5252)
                                    val label = metricId.replace('_', ' ').uppercase()
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = label, color = Color.Gray, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                        Text(text = "$sign${delta.toInt()}", color = deltaColor, fontWeight = FontWeight.Bold, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        Button(
                            onClick = { onFinish(outcome) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD54F)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "CONTINUE SHIFT ➔",
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
    }
}
