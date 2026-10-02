package com.voicerpg.engine

import com.voicerpg.engine.audio.CombatNarrator
import com.voicerpg.engine.audio.SpeechManager
import com.voicerpg.engine.engine.IntentParser
import com.voicerpg.engine.engine.NoveltyCache
import com.voicerpg.engine.engine.ResonanceEngine
import com.voicerpg.engine.model.CombatPhase
import com.voicerpg.engine.model.MetaCommand
import com.voicerpg.engine.viewmodel.CombatViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Lean accessibility and eyes-free mode verification suite without thread sleeps.
 */
class EyesFreeAccessibilityTest {

    private lateinit var viewModel: CombatViewModel
    private lateinit var dummySpeech: SpeechManager
    private lateinit var dummyNarrator: CombatNarrator

    @Before
    fun setUp() {
        dummySpeech = SpeechManager()
        dummyNarrator = CombatNarrator()
        val testScope = CoroutineScope(Dispatchers.Default)
        viewModel = CombatViewModel(
            speechManager = dummySpeech,
            resonanceEngine = ResonanceEngine(NoveltyCache()),
            combatNarrator = dummyNarrator,
            scopeOverride = testScope
        )
    }

    @Test
    fun testMetaCommandParsingAndNoFalsePositives() {
        // Status and info commands
        val queries = mapOf(
            "status" to MetaCommand.STATUS_REPORT,
            "enemies" to MetaCommand.CHECK_ENEMIES,
            "party" to MetaCommand.CHECK_PARTY,
            "pocket mode" to MetaCommand.TOGGLE_EYES_FREE,
            "auto listen" to MetaCommand.TOGGLE_AUTO_LISTEN,
            "options" to MetaCommand.OPEN_OPTIONS,
            "close options" to MetaCommand.CLOSE_OPTIONS,
            "help" to MetaCommand.HELP
        )
        for ((q, expected) in queries) {
            val intent = IntentParser.parse(q)
            assertEquals("Expected $expected for '$q'", expected, intent.metaCommand)
        }

        // Normal spell chants should not trigger meta commands
        val normalIncantations = listOf(
            "Fireball the enemy",
            "Barcode scan strike",
            "Coupon shield protect",
            "Price check page"
        )
        for (incantation in normalIncantations) {
            val intent = IntentParser.parse(incantation)
            assertEquals("Incantation '$incantation' must not be meta command", MetaCommand.NONE, intent.metaCommand)
        }
    }

    @Test
    fun testViewModelEyesFreeAndOptionsToggles() {
        assertFalse(viewModel.state.value.isEyesFreeMode)
        val enabled = viewModel.toggleEyesFreeMode()
        assertTrue(enabled)
        assertTrue(viewModel.state.value.isEyesFreeMode)
        assertTrue(dummyNarrator.isEyesFreeMode.value)

        val disabled = viewModel.toggleEyesFreeMode()
        assertFalse(disabled)
        assertFalse(viewModel.state.value.isEyesFreeMode)

        assertFalse(viewModel.state.value.isOptionsOpen)
        viewModel.openOptions()
        assertTrue(viewModel.state.value.isOptionsOpen)
        viewModel.closeOptions()
        assertFalse(viewModel.state.value.isOptionsOpen)
    }

    @Test
    fun testCombatNarratorSpeakingLifecycle() = runBlocking {
        assertFalse(dummyNarrator.isSpeaking.value)
        dummyNarrator.setSpeakingForTesting(true)
        assertTrue(dummyNarrator.isSpeaking.value)
        dummyNarrator.setSpeakingForTesting(false)
        assertFalse(dummyNarrator.isSpeaking.value)

        // Headless narration calls should return cleanly without hanging
        dummyNarrator.setEyesFreeMode(true)
        dummyNarrator.speakSuspend("Testing speech suspend")
        assertFalse(dummyNarrator.isSpeaking.value)
    }
}
