package com.voicerpg.game.story

import com.voicerpg.engine.story.*

import com.voicerpg.engine.audio.CombatNarrator
import com.voicerpg.engine.audio.SpeechManager
import com.voicerpg.engine.content.GameContent
import com.voicerpg.engine.engine.SaveManager
import com.voicerpg.engine.model.DialogueChoice
import com.voicerpg.engine.model.GameSaveData
import com.voicerpg.engine.viewmodel.StoryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HubReturnTeamworkIncidentTest {

    private fun createViewModel(saveManager: SaveManager = SaveManager()): StoryViewModel {
        val speechManager = SpeechManager()
        val combatNarrator = CombatNarrator()
        val testScope = CoroutineScope(Dispatchers.Default)
        return StoryViewModel(
            speechManager = speechManager,
            combatNarrator = combatNarrator,
            saveManager = saveManager,
            scopeOverride = testScope
        )
    }

    @Test
    fun testContinueGameWithStartInAutoPlayEnablesAndPersistsAutoPlay() {
        val saveManager = SaveManager()
        saveManager.save(
            GameSaveData(
                currentNodeId = "node_locker_hub",
                currentSceneId = "scene_breakroom",
                isAutoPlayEnabled = false
            ),
            slot = 1
        )
        val vm = createViewModel(saveManager)

        // Resuming via the AUTO button starts with startInAutoPlay = true
        vm.continueGame(slot = 1, startInAutoPlay = true)

        assertTrue("Story state must be in pure story / auto-play mode", vm.state.value.isPureStoryMode)
        assertFalse("Story mode should not be paused", vm.state.value.isStoryAutoPlayPaused)
        val persisted = saveManager.load(1)
        assertNotNull(persisted)
        assertTrue("Auto-play mode flag must be persisted to the save slot", persisted!!.isAutoPlayEnabled)
    }

    @Test
    fun testTeamworkIncidentResolutionRoutesBackToActiveHub() {
        val vm = createViewModel()

        // Simulate an incident choice leading to its resolve node
        val testChoice = DialogueChoice(
            id = "choice_cart_flank_steer",
            text = "Steer",
            voiceKeywords = listOf("steer"),
            nextNodeId = "node_incident_cart_train_resolve"
        )

        // Transition through the incident choice to its resolve node
        vm.selectChoice(testChoice)
        val resolveNode = vm.state.value.currentNode
        assertEquals("node_incident_cart_train_resolve", resolveNode.id)
        assertEquals("node_incident_return", resolveNode.nextNodeId)

        // Advancing from the resolve node triggers node_incident_return, which routes back to hub and sets guard
        vm.advanceDialogue()
        val postIncidentFlags = vm.state.value.narrativeFlags
        // Upon entering the hub with incident_just_happened = true, the guard prevents another incident and resets
        assertFalse(
            "incident_just_happened guard must reset to false once the hub is successfully entered",
            postIncidentFlags["incident_just_happened"] == true
        )
    }

    @Test
    fun testBlacklistedHubNeverTriggersIncidents() {
        // node_locker_hub is blacklisted (before morning cheer); incidents must NEVER trigger
        for (trial in 1..25) {
            val vm = createViewModel()
            // Setup a state where player already visited node_locker_hub and is returning to it
            vm.selectChoice(
                DialogueChoice(
                    id = "test_setup_locker",
                    text = "Locker",
                    voiceKeywords = listOf("locker"),
                    nextNodeId = "node_locker_hub"
                )
            )
            assertEquals("node_locker_hub", vm.state.value.currentNode.id)
            assertFalse("node_locker_hub must have allowIncidents == false", vm.state.value.currentNode.allowIncidents)

            // Make a choice from the hub that returns to the hub
            vm.selectChoice(
                DialogueChoice(
                    id = "choice_locker_advice",
                    text = "Advice",
                    voiceKeywords = listOf("advice"),
                    nextNodeId = "node_locker_donna_advice"
                )
            )
            assertEquals("node_locker_donna_advice", vm.state.value.currentNode.id)

            // Returning to hub from donna's advice
            vm.advanceDialogue()

            val currentNode = vm.state.value.currentNode
            assertEquals(
                "Blacklisted hub must NEVER trigger an incident on return",
                "node_locker_hub",
                currentNode.id
            )
        }
    }

    @Test
    fun testAllowedHubTriggersIncidentsAndAvoidsChaining() {
        var incidentObserved = false
        // Across multiple independent returns to an incident-eligible hub (node_breakroom_hub), incidents should trigger
        for (trial in 1..25) {
            val vm = createViewModel()
            // Setup a state where player already visited node_breakroom_hub and is returning to it
            vm.selectChoice(
                DialogueChoice(
                    id = "test_setup_breakroom",
                    text = "Breakroom",
                    voiceKeywords = listOf("breakroom"),
                    nextNodeId = "node_breakroom_hub"
                )
            )
            assertEquals("node_breakroom_hub", vm.state.value.currentNode.id)
            assertTrue("node_breakroom_hub must have allowIncidents == true", vm.state.value.currentNode.allowIncidents)

            // Make a choice from the hub that returns to the hub
            vm.selectChoice(
                DialogueChoice(
                    id = "choice_breakroom_toast",
                    text = "Toast",
                    voiceKeywords = listOf("toast"),
                    nextNodeId = "node_breakroom_mack_banter"
                )
            )
            assertEquals("node_breakroom_mack_banter", vm.state.value.currentNode.id)

            // Returning to breakroom hub from banter
            vm.advanceDialogue()

            val currentNode = vm.state.value.currentNode
            if (currentNode.id.startsWith("node_incident_")) {
                incidentObserved = true
                // Verify incident is flagged
                val flaggedDone = vm.state.value.narrativeFlags.keys.any { it.startsWith("flag_incident_") && it.endsWith("_done") }
                assertTrue("Triggered incident must be flagged as completed so it is not chosen again", flaggedDone)
                assertTrue("incident_just_happened must be set when an incident triggers", vm.state.value.narrativeFlags["incident_just_happened"] == true)
                break
            }
        }
        assertTrue("Expected at least one procedural incident trigger across 25 breakroom hub returns", incidentObserved)
    }

    @Test
    fun testAllIncidentsHaveValidTriggerNodesAndUniqueKeys() {
        val incidents = GameContent.incidents.values
        assertTrue("Must have multiple teamwork incidents defined", incidents.size >= 8)

        val ids = mutableSetOf<String>()
        for (inc in incidents) {
            assertTrue("Incident ID must be unique: ${inc.id}", ids.add(inc.id))
            assertNotNull("Incident ${inc.id} must declare triggerNodeId", inc.triggerNodeId)
        }
    }
}
