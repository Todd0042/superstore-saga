package com.voicerpg.engine.engine

import com.voicerpg.engine.content.GameContent
import com.voicerpg.engine.model.IncidentCondition
import com.voicerpg.engine.model.IncidentConditionType
import com.voicerpg.engine.model.IncidentDefinition
import com.voicerpg.engine.model.IncidentWeightMultiplier
import com.voicerpg.engine.model.MiniGameConfig
import com.voicerpg.engine.model.MiniGameOutcomeTier
import com.voicerpg.engine.viewmodel.StoryState
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class IncidentManagerTest {

    @Test
    fun testEvaluateConditionFlagsAndMetrics() {
        val state = StoryState(
            narrativeFlags = mapOf("flag_active" to true),
            dynamicMetrics = mapOf("metric_heat" to 75f, "metric_morale" to 30f)
        )

        // Flag checks
        assertTrue(IncidentManager.evaluateCondition(IncidentCondition(IncidentConditionType.HAS_FLAG, flag = "flag_active"), state))
        assertFalse(IncidentManager.evaluateCondition(IncidentCondition(IncidentConditionType.HAS_FLAG, flag = "missing_flag"), state))
        assertTrue(IncidentManager.evaluateCondition(IncidentCondition(IncidentConditionType.NOT_FLAG, flag = "missing_flag"), state))

        // Metric checks
        assertTrue(IncidentManager.evaluateCondition(IncidentCondition(IncidentConditionType.METRIC_GREATER_THAN, metricId = "metric_heat", threshold = 50f), state))
        assertFalse(IncidentManager.evaluateCondition(IncidentCondition(IncidentConditionType.METRIC_GREATER_THAN, metricId = "metric_heat", threshold = 80f), state))
        assertTrue(IncidentManager.evaluateCondition(IncidentCondition(IncidentConditionType.METRIC_LESS_THAN, metricId = "metric_morale", threshold = 50f), state))
    }

    @Test
    fun testWeightMultipliersScaleCorrectly() {
        val incident = IncidentDefinition(
            id = "test_event",
            title = "Test Emergency",
            baseWeight = 10f,
            conditions = listOf(
                IncidentCondition(IncidentConditionType.METRIC_GREATER_THAN, metricId = "risk", threshold = 20f)
            ),
            weightMultipliers = listOf(
                IncidentWeightMultiplier(metricId = "risk", threshold = 50f, multiplier = 2.0f)
            )
        )

        // Condition fails
        val lowState = StoryState(dynamicMetrics = mapOf("risk" to 10f))
        assertEquals(0f, IncidentManager.calculateWeight(incident, lowState), 0.01f)

        // Condition passes without multiplier
        val midState = StoryState(dynamicMetrics = mapOf("risk" to 30f))
        assertEquals(10f, IncidentManager.calculateWeight(incident, midState), 0.01f)

        // Condition passes with multiplier
        val highState = StoryState(dynamicMetrics = mapOf("risk" to 60f))
        assertEquals(20f, IncidentManager.calculateWeight(incident, highState), 0.01f)
    }

    @Test
    fun testGameContentLoadsOptionalIncidentsAndMetrics() {
        // In Retail Inferno, metrics and incidents are populated; in generic games, emptyMap is tolerated
        val metrics = GameContent.metrics
        assertTrue("GameContent should support optional data-driven metrics", metrics.isNotEmpty())

        val incidents = GameContent.incidents
        assertTrue("GameContent should support optional data-driven incidents", incidents.isNotEmpty())

        val state = StoryState(
            narrativeFlags = mapOf("shift_rush_survived" to true),
            dynamicMetrics = mapOf("labor_squeeze" to 75f, "karen_index" to 60f)
        )

        val selected = IncidentManager.selectRandomIncident(state, Random(42))
        assertNotNull("Eligible incident should be chosen when conditions match", selected)
    }

    @Test
    fun testMiniGameSimulationOutcomes() {
        val config = MiniGameConfig(
            id = "barcode_rush",
            title = "Barcode Rush",
            metricDeltasOnTier = mapOf(
                MiniGameOutcomeTier.FLAWLESS to mapOf("morale" to 5f),
                MiniGameOutcomeTier.DISASTER to mapOf("morale" to -15f)
            ),
            blurbOnTier = mapOf(
                MiniGameOutcomeTier.FLAWLESS to "Laser-guided perfection!",
                MiniGameOutcomeTier.DISASTER to "Scanner jam disaster!"
            )
        )

        val flawless = MiniGameSimulator.simulate(config, skillModifier = 1.5f, fatigueModifier = 1.0f, random = Random(1))
        assertEquals(MiniGameOutcomeTier.FLAWLESS, flawless.tier)
        assertEquals(5f, flawless.metricDeltas["morale"] ?: 0f, 0.01f)

        val disaster = MiniGameSimulator.simulate(config, skillModifier = 0.4f, fatigueModifier = 2.0f, random = Random(2))
        assertEquals(MiniGameOutcomeTier.DISASTER, disaster.tier)
        assertEquals(-15f, disaster.metricDeltas["morale"] ?: 0f, 0.01f)
    }
}
