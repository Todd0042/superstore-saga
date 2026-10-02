package com.voicerpg.engine

import androidx.compose.ui.graphics.Color
import com.voicerpg.engine.audio.CombatNarrator
import com.voicerpg.engine.audio.SpeechManager
import com.voicerpg.engine.content.GameContent
import com.voicerpg.engine.engine.IntentParser
import com.voicerpg.engine.engine.SaveManager
import com.voicerpg.engine.model.DialogueSpeaker
import com.voicerpg.engine.model.MetaCommand
import com.voicerpg.engine.model.PlayerCustomization
import com.voicerpg.engine.viewmodel.StoryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Lean narration, TTS voice resolution, and options test suite.
 */
class NarrationAndOptionsTest {

    private lateinit var dummySpeech: SpeechManager
    private lateinit var narrator: CombatNarrator

    @Before
    fun setUp() {
        dummySpeech = SpeechManager()
        narrator = CombatNarrator()
    }

    @Test
    fun testCombatNarratorPreferredVoiceIdsConfiguredForAllSpeakers() {
        val preferred = CombatNarrator.PREFERRED_VOICE_IDS
        assertEquals(GameContent.preferredVoiceIds, preferred)
        assertEquals("en-gb-x-rjs-local", preferred["narrator"])
        for (speaker in GameContent.allSpeakers) {
            assertEquals(
                speaker.id,
                GameContent.characterById(speaker.id)?.preferredVoiceId,
                preferred[speaker.id]
            )
        }
    }

    @Test
    fun testResolvePreferredVoiceNameRespectsHierarchy() {
        val speakerId = "fixture_speaker"
        val preferred = mapOf(speakerId to "voice-preferred-local")
        val installed = setOf("en-us-x-iol-local", "voice-preferred-local", "en-us-x-sfg-local")

        // 1. Persisted override takes priority
        val persisted = mapOf(speakerId to "en-us-x-iol-local")
        val resolvedWithPersisted = CombatNarrator.resolvePreferredVoiceName(
            speakerId = speakerId,
            heuristicName = "en-us-x-sfg-local",
            installedNames = installed,
            persistedAssignments = persisted,
            preferredByDefault = preferred
        )
        assertEquals("en-us-x-iol-local", resolvedWithPersisted)

        // 2. Fallback to declared preferredVoiceId
        val resolvedWithoutPersisted = CombatNarrator.resolvePreferredVoiceName(
            speakerId = speakerId,
            heuristicName = "en-us-x-sfg-local",
            installedNames = installed,
            persistedAssignments = emptyMap(),
            preferredByDefault = preferred
        )
        assertEquals("voice-preferred-local", resolvedWithoutPersisted)
    }

    @Test
    fun testNarrationTogglesAndPersistenceRoundTrip() {
        narrator.setNarrationEnabled(true)
        assertTrue(narrator.isNarrationEnabled.value)
        narrator.setNarrationEnabled(false)
        assertFalse(narrator.isNarrationEnabled.value)

        val saveManager = SaveManager()
        val custom = PlayerCustomization(name = "TestHero")
        val initialSave = saveManager.createInitialSave(custom)

        val updated = initialSave.copy(
            isEyesFreeMode = true,
            isNarrationEnabled = false,
            speechRate = 1.25f
        )
        saveManager.save(updated, 1)

        val reloaded = saveManager.load(1)
        assertTrue(reloaded?.isEyesFreeMode == true)
        assertFalse(reloaded?.isNarrationEnabled == true)
        assertEquals(1.25f, reloaded?.speechRate ?: 0f, 0.01f)
    }

    @Test
    fun testIntentParserRecognizesNarrationMetaCommands() {
        assertEquals(MetaCommand.TOGGLE_NARRATION, IntentParser.parse("toggle narration").metaCommand)
        assertEquals(MetaCommand.TOGGLE_EYES_FREE, IntentParser.parse("pocket mode").metaCommand)
        assertEquals(MetaCommand.TOGGLE_AUTO_LISTEN, IntentParser.parse("auto listen").metaCommand)
        assertEquals(MetaCommand.OPEN_OPTIONS, IntentParser.parse("options").metaCommand)
        assertEquals(MetaCommand.CLOSE_OPTIONS, IntentParser.parse("close options").metaCommand)
    }
}
