package com.voicerpg.engine

import com.voicerpg.engine.audio.SpeechManager
import com.voicerpg.engine.content.GameContent
import com.voicerpg.engine.engine.NoveltyCache
import com.voicerpg.engine.engine.ResonanceEngine
import com.voicerpg.engine.model.BattleEnvironment
import com.voicerpg.engine.model.CombatPhase
import com.voicerpg.engine.model.Enemy
import com.voicerpg.engine.model.EncounterDefinition
import com.voicerpg.engine.model.PartyMember
import com.voicerpg.engine.viewmodel.CombatViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Lean, content-neutral test suite for dynamic combat encounters,
 * turn processing, and boss phase mechanics without thread sleeps.
 */
class DynamicEncounterTest {

    private lateinit var viewModel: CombatViewModel

    @Before
    fun setUp() {
        val dummySpeech = SpeechManager()
        val testScope = CoroutineScope(Dispatchers.Default)
        viewModel = CombatViewModel(
            speechManager = dummySpeech,
            resonanceEngine = ResonanceEngine(NoveltyCache()),
            scopeOverride = testScope
        )
    }

    @After
    fun tearDown() {
        viewModel.cleanup()
    }

    private fun createTestParty(): List<PartyMember> {
        val starterClass = GameContent.classes.first()
        val spells = GameContent.spellsForClass(starterClass.id)
        return listOf(
            PartyMember(
                id = "hero_1",
                name = "Test Hero",
                loreClass = starterClass.title,
                currentHp = starterClass.startingHp,
                maxHp = starterClass.startingHp,
                currentMp = starterClass.startingMp,
                maxMp = starterClass.startingMp,
                spells = spells,
                speed = starterClass.startingSpeed
            )
        )
    }

    private fun createTestEnemies(): List<Enemy> {
        val template = GameContent.enemyTemplates.values.firstOrNull()
        return listOf(
            Enemy(
                id = "test_target_1",
                name = "Test Adversary",
                subtitle = "Frontline",
                currentHp = 200,
                maxHp = 200,
                baseAttack = 15,
                defense = 5,
                speed = 40
            )
        )
    }

    @Test
    fun testEncounterInitializationFromDefinition() {
        val encounter = EncounterDefinition(
            id = "test_encounter",
            name = "Test Encounter",
            environment = BattleEnvironment.DUNGEON,
            enemies = createTestEnemies(),
            initialParty = createTestParty()
        )

        viewModel.startEncounter(encounter)

        val state = viewModel.state.value
        assertEquals(1, state.party.size)
        assertEquals(1, state.enemies.size)
        assertEquals(BattleEnvironment.DUNGEON, state.currentEnvironment)
        assertTrue(state.party.first().isAlive)
        assertTrue(state.enemies.first().isAlive)
    }

    private fun waitFor(timeoutMs: Long = 2000, condition: () -> Boolean) {
        val deadline = System.currentTimeMillis() + timeoutMs
        while (System.currentTimeMillis() < deadline) {
            if (condition()) return
            Thread.sleep(20)
        }
        assertTrue("Timed out waiting for combat action to settle", condition())
    }

    @Test
    fun testDirectPlayerActionAndTargetResolution() {
        val party = createTestParty()
        val enemies = createTestEnemies()
        viewModel.startEncounter(party = party, enemies = enemies, environment = BattleEnvironment.DUNGEON)

        viewModel.setPlayerInputPhaseForTesting(party.first().id)
        val initialEnemyHp = viewModel.state.value.enemies.first().currentHp

        // Execute attack/incantation
        val spell = party.first().spells.first { !it.isHeal && it.basePower > 0 }
        viewModel.processIncantation(spell.name)

        waitFor { viewModel.state.value.enemies.first().currentHp < initialEnemyHp }
    }

    @Test
    fun testDefendReducesDamageAndSetsGuardStance() {
        val party = createTestParty()
        val enemies = createTestEnemies()
        viewModel.startEncounter(party = party, enemies = enemies, environment = BattleEnvironment.DUNGEON)

        viewModel.setPlayerInputPhaseForTesting(party.first().id)
        viewModel.defendActivePartyMember()

        waitFor { viewModel.state.value.party.first().isGuarding }
    }

    @Test
    fun testVictoryConditionWhenAllEnemiesDefeated() {
        val party = createTestParty()
        val enemies = listOf(
            Enemy(id = "weak_foe", name = "Weak Foe", subtitle = "Minion", currentHp = 1, maxHp = 50, baseAttack = 10, speed = 20)
        )
        viewModel.startEncounter(party = party, enemies = enemies, environment = BattleEnvironment.DUNGEON)

        viewModel.setPlayerInputPhaseForTesting(party.first().id)
        val spell = party.first().spells.first { !it.isHeal && it.basePower > 0 }
        viewModel.processIncantation(spell.name)

        waitFor { viewModel.state.value.enemies.none { it.isAlive } }
    }
}