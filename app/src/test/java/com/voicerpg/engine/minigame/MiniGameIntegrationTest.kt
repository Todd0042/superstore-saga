package com.voicerpg.engine.minigame

import com.voicerpg.engine.audio.CombatNarrator
import com.voicerpg.engine.audio.SpeechManager
import com.voicerpg.engine.content.GameContent
import com.voicerpg.engine.engine.MiniGameSimulator
import com.voicerpg.engine.engine.StoryScript
import com.voicerpg.engine.model.GameScreen
import com.voicerpg.engine.model.MiniGameOutcomeTier
import com.voicerpg.engine.viewmodel.StoryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.random.Random

class MiniGameIntegrationTest {

    @Before
    fun setUp() {
        val assetsDir = listOf(
            File("app/src/main/assets/game"),
            File("src/main/assets/game"),
            File("assets/game")
        ).firstOrNull { it.isDirectory }
        assertNotNull("Assets directory should be found", assetsDir)
        GameContent.reset()
        GameContent.initializeFromDirectory(assetsDir!!)
    }

    private fun createViewModel(): StoryViewModel {
        val narrator = CombatNarrator()
        val speechManager = SpeechManager()
        val testScope = CoroutineScope(Dispatchers.Default)
        return StoryViewModel(
            speechManager = speechManager,
            combatNarrator = narrator,
            scopeOverride = testScope
        )
    }

    @Test
    fun testMiniGameConfigsLoadFromAssets() {
        val pack = GameContent.pack
        val minigames = pack.minigames
        assertTrue("Minigames must be loaded from minigames.json", minigames.isNotEmpty())

        val expectedIds = listOf("barcode_blaster", "pallet_haul", "baler_compactor", "counter_commotion", "cart_corral")
        for (id in expectedIds) {
            val config = minigames[id]
            assertNotNull("MiniGame config '$id' must exist", config)
            assertEquals(id, config!!.id)
            assertTrue("Title must not be blank for '$id'", config.title.isNotBlank())
            assertTrue("Description must not be blank for '$id'", config.description.isNotBlank())
            assertTrue("Duration must be > 0 for '$id'", config.durationSeconds > 0)
            assertTrue("Quota must be > 0 for '$id'", config.quota > 0)

            // Verify tier metric deltas and blurbs
            for (tier in MiniGameOutcomeTier.values()) {
                val deltas = config.metricDeltasOnTier[tier]
                assertNotNull("Metric deltas must be defined for tier $tier in '$id'", deltas)
                assertTrue("Deltas must have entries for tier $tier in '$id'", deltas!!.isNotEmpty())

                val blurb = config.blurbOnTier[tier]
                assertNotNull("Blurb must be defined for tier $tier in '$id'", blurb)
                assertTrue("Blurb must not be blank for tier $tier in '$id'", blurb!!.isNotBlank())
            }
        }
    }

    @Test
    fun testMiniGameSimulatorOutcomeFromScore() {
        val config = GameContent.minigameById("barcode_blaster")!!

        val flawless = MiniGameSimulator.outcomeFromScore(config, 95f)
        assertEquals(MiniGameOutcomeTier.FLAWLESS, flawless.tier)
        assertEquals(95f, flawless.score, 0.001f)
        assertTrue(flawless.narrativeBlurb.isNotBlank())
        assertEquals(20.0f, flawless.metricDeltas["productivity"])

        val solid = MiniGameSimulator.outcomeFromScore(config, 75f)
        assertEquals(MiniGameOutcomeTier.SOLID, solid.tier)
        assertEquals(10.0f, solid.metricDeltas["productivity"])

        val clumsy = MiniGameSimulator.outcomeFromScore(config, 50f)
        assertEquals(MiniGameOutcomeTier.CLUMSY, clumsy.tier)
        assertEquals(5.0f, clumsy.metricDeltas["productivity"])

        val disaster = MiniGameSimulator.outcomeFromScore(config, 20f)
        assertEquals(MiniGameOutcomeTier.DISASTER, disaster.tier)
        assertEquals(0.0f, disaster.metricDeltas["productivity"])
    }

    @Test
    fun testMiniGameSimulatorSimulateDistribution() {
        val config = GameContent.minigameById("baler_compactor")!!

        repeat(50) {
            val outcome = MiniGameSimulator.simulate(
                config = config,
                skillModifier = 1.0f,
                fatigueModifier = 1.0f,
                random = Random(it)
            )
            assertTrue("Score should be between 0 and 100", outcome.score in 0f..100f)
            assertNotNull("Tier must not be null", outcome.tier)
            assertTrue("Blurb must not be blank", outcome.narrativeBlurb.isNotBlank())
        }
    }

    @Test
    fun testStoryViewModelTriggerMiniGameInPureStoryMode() {
        val viewModel = createViewModel()

        viewModel.switchToStory()
        viewModel.toggleAutoPlay()
        assertTrue(viewModel.state.value.isPureStoryMode)

        val initialProductivity = viewModel.state.value.dynamicMetrics["productivity"] ?: 50f

        // Trigger minigame in pure story mode: simulates instantly
        viewModel.triggerMiniGame("barcode_blaster")

        // Should return to STORY_EXPLORATION
        assertEquals(GameScreen.STORY_EXPLORATION, viewModel.state.value.gameScreen)
        assertNull(viewModel.state.value.activeMiniGameConfig)

        // Productivity metric should have adjusted
        val newProductivity = viewModel.state.value.dynamicMetrics["productivity"] ?: 50f
        assertTrue("Productivity should update after minigame outcome", newProductivity >= initialProductivity)
    }

    @Test
    fun testStoryViewModelInteractiveMiniGameAndSkip() {
        val viewModel = createViewModel()

        viewModel.switchToStory()
        assertEquals(GameScreen.STORY_EXPLORATION, viewModel.state.value.gameScreen)

        // Trigger interactive minigame
        viewModel.triggerMiniGame("pallet_haul")
        assertEquals(GameScreen.MINI_GAME, viewModel.state.value.gameScreen)
        assertNotNull(viewModel.state.value.activeMiniGameConfig)
        assertEquals("pallet_haul", viewModel.state.value.activeMiniGameConfig?.id)

        // Skip/Auto minigame
        viewModel.onSkipMiniGame()

        // Back to story exploration with results applied
        assertEquals(GameScreen.STORY_EXPLORATION, viewModel.state.value.gameScreen)
        assertNull(viewModel.state.value.activeMiniGameConfig)
    }

    @Test
    fun testStoryScriptNodesMiniGameReferencesResolve() {
        val allNodes = StoryScript.ALL_NODES
        val minigameNodes = allNodes.values.filter { it.triggerMiniGameId != null }
        assertTrue("Story graph should have nodes with triggerMiniGameId", minigameNodes.isNotEmpty())

        for (node in minigameNodes) {
            val gameId = node.triggerMiniGameId!!
            val config = GameContent.minigameById(gameId)
            assertNotNull("Node '${node.id}' triggers unknown mini-game '$gameId'", config)
        }
    }

    @Test
    fun testTitleScreenMiniGameTestingFlow() {
        val viewModel = createViewModel()
        assertEquals(GameScreen.TITLE, viewModel.state.value.gameScreen)
        assertFalse(viewModel.state.value.isMiniGameTestMenuOpen)

        // Open QA test menu
        viewModel.openMiniGameTestMenu()
        assertTrue(viewModel.state.value.isMiniGameTestMenuOpen)

        // Close QA test menu
        viewModel.closeMiniGameTestMenu()
        assertFalse(viewModel.state.value.isMiniGameTestMenuOpen)

        // Launch mini-game from title test menu
        viewModel.triggerMiniGame("baler_compactor")
        assertEquals(GameScreen.MINI_GAME, viewModel.state.value.gameScreen)
        assertEquals(GameScreen.TITLE, viewModel.state.value.previousScreen)
        assertEquals("baler_compactor", viewModel.state.value.activeMiniGameConfig?.id)
        assertFalse(viewModel.state.value.isMiniGameTestMenuOpen)

        // Skip / finish mini-game
        viewModel.onSkipMiniGame()

        // Clean return to TITLE screen without corrupting story state
        assertEquals(GameScreen.TITLE, viewModel.state.value.gameScreen)
        assertNull(viewModel.state.value.previousScreen)
        assertNull(viewModel.state.value.activeMiniGameConfig)
        assertFalse(viewModel.state.value.isMiniGameTestMenuOpen)
    }

    @Test
    fun testTitleScreenVoiceMiniGameTriggers() {
        val viewModel = createViewModel()
        assertEquals(GameScreen.TITLE, viewModel.state.value.gameScreen)

        // Voice "test minigames" opens dialog
        viewModel.handleStoryVoiceInput("test minigames")
        assertTrue(viewModel.state.value.isMiniGameTestMenuOpen)

        // Voice "close" closes dialog
        viewModel.handleStoryVoiceInput("close")
        assertFalse(viewModel.state.value.isMiniGameTestMenuOpen)

        // Voice "arcade" opens dialog
        viewModel.handleStoryVoiceInput("arcade")
        assertTrue(viewModel.state.value.isMiniGameTestMenuOpen)

        // Voice direct triggers launch games
        val gameVoiceMap = mapOf(
            "barcode blaster" to "barcode_blaster",
            "pallet haul" to "pallet_haul",
            "baler" to "baler_compactor",
            "counter commotion" to "counter_commotion",
            "cart corral" to "cart_corral"
        )

        for ((voiceCmd, expectedId) in gameVoiceMap) {
            viewModel.handleStoryVoiceInput(voiceCmd)
            assertEquals(GameScreen.MINI_GAME, viewModel.state.value.gameScreen)
            assertEquals(expectedId, viewModel.state.value.activeMiniGameConfig?.id)
            assertEquals(GameScreen.TITLE, viewModel.state.value.previousScreen)
            assertFalse(viewModel.state.value.isMiniGameTestMenuOpen)

            // Return to title
            viewModel.onSkipMiniGame()
            assertEquals(GameScreen.TITLE, viewModel.state.value.gameScreen)
            assertNull(viewModel.state.value.activeMiniGameConfig)
        }
    }
}
