package com.voicerpg.engine

import com.voicerpg.engine.audio.CombatNarrator
import com.voicerpg.engine.audio.SpeechManager
import com.voicerpg.engine.engine.TutorialBattleContent
import com.voicerpg.engine.engine.TutorialBattleStep
import com.voicerpg.engine.viewmodel.ModalCardType
import com.voicerpg.engine.viewmodel.TutorialBattleViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Lean state machine tests for tutorial battle flow without animation sleep loops.
 */
class TutorialBattleTest {

    private lateinit var viewModel: TutorialBattleViewModel
    private lateinit var dummySpeech: SpeechManager
    private lateinit var dummyNarrator: CombatNarrator
    private val testScope = CoroutineScope(Dispatchers.Default)

    private val heroId: String
        get() = TutorialBattleContent.CHARACTERS.first().id

    @Before
    fun setUp() {
        dummySpeech = SpeechManager()
        dummyNarrator = CombatNarrator()
        viewModel = TutorialBattleViewModel(
            speechManager = dummySpeech,
            combatNarrator = dummyNarrator,
            scopeOverride = testScope
        )
    }

    @Test
    fun testInitialTutorialState() {
        val state = viewModel.state.value
        assertEquals(0, state.stepIndex)
        assertTrue(state.isModalVisible)
        assertEquals(ModalCardType.NARRATOR_INTRO, state.modalType)
        assertEquals(1, state.party.size)
        assertEquals(heroId, state.party[0].id)
        assertEquals(3, state.enemies.size)
        assertEquals("dummy_alpha", state.enemies[0].id)
        assertEquals("dummy_beta", state.enemies[1].id)
        assertEquals("dummy_gamma", state.enemies[2].id)
        assertEquals("dummy_alpha", state.targetedEnemyId)
        assertEquals(heroId, state.activePartyMemberId)
    }

    @Test
    fun testStepProgressionAndHeroJoin() {
        // Step 0: Intro 1
        assertEquals(0, viewModel.state.value.stepIndex)
        viewModel.advanceModal()

        // Step 1: Intro 2
        assertEquals(1, viewModel.state.value.stepIndex)
        viewModel.advanceModal()

        // Step 2: Hero Join
        assertEquals(2, viewModel.state.value.stepIndex)
        assertEquals(ModalCardType.CHARACTER_JOIN, viewModel.state.value.modalType)
        assertEquals(1, viewModel.state.value.party.size)
        viewModel.advanceModal()

        // Step 3: Hero Spell Overview
        assertEquals(3, viewModel.state.value.stepIndex)
        assertEquals(ModalCardType.SPELL_OVERVIEW, viewModel.state.value.modalType)
        viewModel.advanceModal()

        // Step 4: First Practice Cast
        assertEquals(4, viewModel.state.value.stepIndex)
        assertEquals(ModalCardType.PRACTICE_CAST_PROMPT, viewModel.state.value.modalType)
        assertTrue(viewModel.state.value.currentStep is TutorialBattleStep.PracticeCast)
    }

    @Test
    fun testFullResetTutorial() {
        for (i in 0 until 5) {
            viewModel.advanceModal()
        }
        assertTrue(viewModel.state.value.stepIndex > 0)

        viewModel.resetTutorial()

        val resetState = viewModel.state.value
        assertEquals(0, resetState.stepIndex)
        assertEquals(1, resetState.party.size)
        assertEquals(heroId, resetState.party[0].id)
        assertEquals(3, resetState.enemies.size)
        assertEquals(ModalCardType.NARRATOR_INTRO, resetState.modalType)
    }
}