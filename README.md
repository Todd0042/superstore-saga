<p align="center">
  <a href="https://ko-fi.com/todd0042">
    <img src="https://raw.githubusercontent.com/Todd0042/creator-assets/main/banners/todd_creator_banner_v1.0.png" alt="Support Todd on Ko-fi" width="100%" />
  </a>
</p>

---

# 🛒 Superstore Saga: Escape From MegaMart

[![Version](https://img.shields.io/badge/Version-0.1.0--alpha-ff2a85.svg)](https://github.com/Todd0042/superstore-saga/releases)
[![License: PolyForm Noncommercial 1.0.0](https://img.shields.io/badge/License-PolyForm%20Noncommercial%201.0.0-00f0ff.svg)](LICENSE)
[![Platform: Android 8.0+](https://img.shields.io/badge/Platform-Android%208.0%2B%20(API%2026%2B)-3DDC84.svg?logo=android&logoColor=white)](https://developer.android.com)
[![16 KB Page Size](https://img.shields.io/badge/16%20KB-Compatible-success.svg)](https://developer.android.com/16kb-page-size)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF.svg?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![UI: Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%202024.10.01-4285F4.svg?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Voice Control: Hands-Free](https://img.shields.io/badge/Controls-Hands--Free%20Voice-orange.svg)](app/src/main/java/com/voicerpg/engine/audio/SpeechManager.kt)
[![Speech: Android Offline STT](https://img.shields.io/badge/Speech-Android%20Offline%20STT-3DDC84.svg)](https://developer.android.com/reference/android/speech/SpeechRecognizer)
[![Combat: Cadence & Timing](https://img.shields.io/badge/Combat-Cadence%20%26%20Timing-yellow.svg)](app/src/main/java/com/voicerpg/engine/engine/ResonanceEngine.kt)
[![Audio: Dynamic Ducking](https://img.shields.io/badge/Audio-Dynamic%20Ducking-green.svg)](app/src/main/java/com/voicerpg/engine/audio/)
[![Unit Tests](https://img.shields.io/badge/Unit%20Tests-368%20Passing-39ff14.svg)](app/src/test/java/com/voicerpg/engine/)
[![Engine: VoiceRPG](https://img.shields.io/badge/Engine-VoiceRPG%20v2.0.0-teal.svg)](https://github.com/Todd0042/voice-rpg-engine)
[![Support on Ko-fi](https://img.shields.io/badge/Ko--fi-Support%20Creator-F16061?style=flat&logo=ko-fi&logoColor=white)](https://ko-fi.com/todd0042)

> **A satirical, hands-free corporate survival voice JRPG for Android. Step onto the sales floor of MegaMart Store #1042, battle through entitled Karens, coupon hoarders, and rogue district managers, execute POS register combos with vocal cadence and barcode-whip timing, and survive the closing shift to clock out.**

```
       _________________________________________________________________________
      |  ENTITLED KAREN (MANAGER DEMANDER)         PATIENCE [██████░░░░░░░░]  40%   |
      |  "I want to speak to your corporate VP!"   STAMINA  [███████████████] 100%   |
      |_________________________________________________________________________|
      |  ASSOCIATE: "Clean up on Aisle 4! Price check on expired yogurt!"       |
      |  RESONANCE: MYTHIC (142%) >>> OVERRIDE CODE 99 DEALS 2.25x FOCUS CRUSH!  |
      |  POS KEYPAD: [ITEM SCAN] [SUBTOTAL] [VOID / PARRY] [MANAGER OVERRIDE]   |
      |_________________________________________________________________________|
```

---

## 📖 Table of Contents

* **Part 1 — Consumer Overview & Quick Start**
  * [🛒 What Is Superstore Saga?](#-what-is-superstore-saga)
  * [⚡ How a Shift Works in 3 Steps](#-how-a-shift-works-in-3-steps)
  * [🏢 The 4 Department Career Tracks](#-the-4-department-career-tracks)
  * [⚔️ The POS Register Terminal Battle System](#-the-pos-register-terminal-battle-system)
  * [🚶 Screenless Pocket Mode & Floor Pagers](#-screenless-pocket-mode--floor-pagers)
  * [🚀 Quick Download & Installation (Android APK)](#-quick-download--installation-android-apk)
* **Part 2 — Consolidated Technical Documentation & Architecture Reference**
  * [1. Clean Architecture & System Framework (`com.voicerpg.engine.*`)](#1-clean-architecture--system-framework)
  * [2. Retail Content Decoupling & JSON Schema (`assets/game/`)](#2-retail-content-decoupling--json-schema)
  * [3. Real-Time Speech Recognition & Continuous Audio Arbitration Loop (`SpeechManager.kt`)](#3-real-time-speech-recognition--continuous-audio-arbitration-loop)
  * [4. Resonance Engine & Acoustic-Poetic Scoring Math (`ResonanceEngine.kt`, `SpellChantPresets.kt`)](#4-resonance-engine--acoustic-poetic-scoring-math)
  * [5. Real-Time Audio DSP & Dynamic Music Ducking Pipeline (`SoundbitePlayer.kt`, `MusicManager.kt`)](#5-real-time-audio-dsp--dynamic-music-ducking-pipeline)
  * [6. Dynamic Retail Conflict Incidents & Conveyor ATB Battle System (`IncidentManager.kt`, `CombatModels.kt`)](#6-dynamic-retail-conflict-incidents--conveyor-atb-battle-system)
  * [7. Multi-Repo Synchronization Workflow (`sync_engine_to_games.sh`)](#7-multi-repo-synchronization-workflow)
  * [8. Automated Unit Verification Suite (`tests/`)](#8-automated-unit-verification-suite)
  * [9. Clean-Room Provenance & Non-Commercial Licensing](#9-clean-room-provenance--non-commercial-licensing)

---

# Part 1 — Consumer Overview & Quick Start

## 🛒 What Is Superstore Saga?

**Superstore Saga: Escape From MegaMart** is a satirical corporate dungeon-crawler and turn-based voice JRPG for Android. 

Instead of ancient crypts, dragons, and mystical castles, you are a newly hired retail associate assigned to **MegaMart Store #1042**—a sprawling mega-retail monolith with infinite aisles, impossible corporate metrics, and chaotic customer confrontations. 

Navigate department politics, resolve emergency store incidents, and survive high-stress retail battles using your voice:
- **🗣️ Speak Corporate Directives & Intercom Pages**: Trigger department abilities by speaking their keywords (*"Price check!"*, *"Clean up on Aisle 3!"*, *"Manager override!"*).
- **⚡ Voice Resonance & Cadence Scoring**: Dramatic customer service inflection and assertive vocal cadence trigger the **Resonance Engine**, multiplying ability damage up to **3.0x (Transcendental)**.
- **🎧 100% Screenless Pocket Mode**: Slip your phone into your pocket, plug in headphones, and navigate entire shifts eyes-free with full audio narration and hands-free register directives.

---

## ⚡ How a Shift Works in 3 Steps

1. **Clock In & Select Your Department**: Begin your probationary shift as an applicant at Store #1042. Choose your role—Cashier, Stocker, Unloader, or Specialty Specialist. Each department plays its own **56-shift story with 14 fights**.
2. **Handle Customer Confrontations**: When chaotic customers or corporate auditors attack, issue voice commands on your Point-of-Sale (POS) terminal to de-escalate, parry expired coupons, or summon managerial backup.
3. **Survive the Closing Shift**: Manage your Stamina (HP) and Corporate Metrics (Patience, Morale) to make it to the time clock and punch out. Your department kit grows as the story goes on: each act's boss you beat teaches you a new move.

---

## 🏢 The 4 Department Career Tracks

You fight **alone**, with your department's kit. Every kit starts with one attack, **Union Break** (a coffee with the crew that heals you for a fifth of your max Stamina, scaled by how well you say it) and one more move, and learns four more as the story goes on (see [Part 2, section 2](#2-retail-content-decoupling--json-schema)).

| Department Track | Starting Kit | Learned Along the Story | Strong Against |
| :--- | :--- | :--- | :--- |
| **Front End (Cashier)** | *Barcode Scan* (Equipment), *Union Break*, *Coupon Shield* (guard) | *Drawer Smash*, *Express Lane* (hits everyone), *Escalate*, *Strength in Numbers* | Customers, Corporate, the Floor Rush Crowd |
| **Sales Floor (Stocker)** | *Box Cutter Slash* (Elbow Grease), *Union Break*, *Cart Push* (Hustle) | *Flatbed Charge*, *Escalate*, *By the Book*, *Strength in Numbers* | Crowds, Lifers, Fixtures, then Customers and Loss Prevention |
| **Backroom (Unloader)** | *Pallet Crush* (Elbow Grease), *Union Break*, *Shrink Wrap Hold* (guard) | *Baler Grind*, *Escalate*, *By the Book*, *Strength in Numbers* | Fixtures, Loss Prevention, then Customers |
| **Specialty (Specialist)** | *Warranty Pitch* (Upsell), *Union Break*, *Price Check* (guard) | *Barcode Scan*, *Flash Sale* (hits everyone), *By the Book*, *Strength in Numbers* | Customers, Management, Fixtures |

---

## ⚔️ The POS Register Terminal Battle System

Combat in *Superstore Saga* represents high-stakes retail showdowns. The combat interface is styled as a retro Point-of-Sale terminal:
- **Stamina (HP)**: Your physical energy (0–1,000). Dropping to 0 triggers an unpaid disciplinary review in HR.
- **No mana, no cooldowns**: every move is always available; the right choice comes from what the enemy is weak to (Hustle, Store Policy, Equipment, Solidarity, Chain of Command, Upsell or Elbow Grease) and what it shrugs off. A hit reveals a weakness, and the game remembers it.
- **Conveyor Belt Turn Meter (ATB)**: Determines who acts next based on Employee Agility and customer fury.
- **Active Register Reactions**: Speak *"Void!"* or tap `[VOID / PARRY]` to parry expired coupon attacks with 100% damage deflection!

---

## 🚶 Screenless Pocket Mode & Floor Pagers

Designed for real-world commutes, walks, or listening on the go, **Pocket Mode** turns the screen into an OLED touch-guarded black display while routing all customer dialogue, department pages, and combat audio through your earbuds:
- Voice meta-commands (*"Status report"*, *"Check customers"*, *"Floor report"*, *"Options"*) allow you to play complete shifts hands-free.
- Zero touch interaction required—the engine auto-arms the microphone the moment retail narration finishes.

---

## 🚀 Quick Download & Installation (Android APK)

1. Download **`superstore-saga-v0.1.0-alpha.apk`** from [Releases](https://github.com/Todd0042/superstore-saga/releases).
2. Install the APK on any Android device running **Android 8.0 or higher (API 26+)**.
3. Grant **Microphone Permission** on first boot to enable hands-free voice commands.

---

# Part 2 — Consolidated Technical Documentation & Architecture Reference

## 1. Clean Architecture & System Framework

*Superstore Saga* is architected upon the **[VoiceRPG Engine](https://github.com/Todd0042/voice-rpg-engine)** framework, strictly separating reusable game engine infrastructure from retail narrative and combat data:

```
+-----------------------------------------------------------------------+
|                       SUPERSTORE SAGA CLIENT                          |
|         (Jetpack Compose UI, Material 3, Soundbite Intercoms)         |
+-----------------------------------------------------------------------+
                                   |
                                   v
+-----------------------------------------------------------------------+
|                         VOICERPG CORE ENGINE                          |
|  +--------------------+  +--------------------+  +-----------------+  |
|  |   SpeechManager    |  |  ResonanceEngine   |  |   CombatEngine  |  |
|  | (Arbitration Loop) |  | (Acoustic Scoring) |  |   (ATB Matrix)  |  |
|  +--------------------+  +--------------------+  +-----------------+  |
+-----------------------------------------------------------------------+
                                   |
                                   v
+-----------------------------------------------------------------------+
|                    RETAIL CONTENT LAYER (JSON)                        |
|  manifest.json | characters/*.json | enemies/*.json | story/*.json    |
+-----------------------------------------------------------------------+
```

Key framework guarantees:
- **Zero Cloud Latency & Total Privacy**: 100% on-device speech processing with `EXTRA_PREFER_OFFLINE` using `SpeechRecognizer.createOnDeviceSpeechRecognizer` on Android 12+ (API 31+).
- **16 KB Page Alignment**: Fully compatible with Android 15's 16 KB page-size architecture requirements.

---

## 2. Retail Content Decoupling & JSON Schema

All gameplay data, dialogue trees, encounters, and register abilities are decoupled from compiled bytecode and located under `app/src/main/assets/game/`:

- [`manifest.json`](app/src/main/assets/game/manifest.json): World title, starting scene (`scene_online_app`), and default encounter (`encounter_register_rush`).
- `characters/*.json`: Store managers, shift leads, and companion associates.
- [`enemies/enemies.json`](app/src/main/assets/game/enemies/enemies.json): 17 enemy templates in 7 families (see *Enemies* below).
- [`encounters/encounters.json`](app/src/main/assets/game/encounters/encounters.json): 20 encounters (7 shared by every department, 13 added by the fights pass in [`docs/FIGHTS_PROPOSAL.md`](docs/FIGHTS_PROPOSAL.md)).
- [`spells/spells.json`](app/src/main/assets/game/spells/spells.json) and [`classes/*.json`](app/src/main/assets/game/classes): department moves, each class's starting kit and its `spellGrants` (moves learned at story flags).
- `story/scenes.json` & `story/nodes.json`: Dialogue trees across MegaMart departments.
- `sprites/sheets/`: 22 animated 64 px side-view battle sprite sheets (Aseprite PNG + JSON): the Associate, 7 enemies, Store Manager Henderson as a 128 px boss, and 13 store staff. The roster that maps every enemy to its sprite, with the artist's brief, is [`art/sprite_roster.json`](art/sprite_roster.json) / [`art/SPRITE_BRIEFS.md`](art/SPRITE_BRIEFS.md) (applied with the engine's `scripts/sprite_roster.py superstore-saga --apply`); `../voice-rpg-engine/scripts/sprite_coverage.sh superstore-saga` lists what the sprite contract still wants before a release build is allowed.
- `environments/environments.json`: the five battle backdrops (`assets/environments/*.png`, MegaMart-themed, 1344x768), each with its painted floor line (`artHorizon`) so combatants stand on the floor.

### Fights per department (14 each)

A new fight sits on its own lead-in node (`node_shiftN_<dept>_fight`) between a story beat's choice and the next shift, because a node with `triggerBattleEncounterId` goes straight to battle and skips its choices.

| Shift | Unloader | Stocker | Cashier | Specialist |
| :--- | :--- | :--- | :--- | :--- |
| 1 | Register Rush | Register Rush | Register Rush | Register Rush |
| 7 | Saturday Rush | Saturday Rush | Saturday Rush | Saturday Rush |
| 10 | VP Vance (boss) | VP Vance (boss) | VP Vance (boss) | VP Vance (boss) |
| 11-12 | The Screamer (12) | | Clopening Coupons (11) | Locked Case Riot (12) |
| 15 | Go-Back Golem | Go-Back Golem | Go-Back Golem | Go-Back Golem |
| 20 | | Forklift Cowboy Derrick | | |
| 22 | Slacker showdown (boss) | Slacker showdown (boss) | Slacker showdown (boss) | Slacker showdown (boss) |
| 26 | Code Spark | Code Spark | Code Spark | Code Spark |
| 29 | Sensor Meltdown | Sensor Meltdown | Sensor Meltdown | Sensor Meltdown |
| 34 | Auditor standoff (boss) | Auditor standoff (boss) | Auditor standoff (boss) | Auditor standoff (boss) |
| 36 | AP Shakedown | AP Shakedown | AP Shakedown | AP Shakedown |
| 46 | Clint (boss) | Clint (boss) | Clint (boss) | Clint (boss) |
| 48 | Lockdown: Derrick | Lockdown: Todd | Lockdown: Marsha | Lockdown: Chad |
| 53 | Henderson's Last Line (Cheryl) | same | same | same |
| 55 | Henderson (finale) | Henderson (finale) | Henderson (finale) | Henderson (finale) |

Each column has 14 fights: the stocker has no shift 11-12 fight (its Act II fights are 15 and 20), the other three have no shift 20 fight.

### Enemies

| Family (display name) | Weak to (matrix 1.5+) | Enemies |
| :--- | :--- | :--- |
| Customers (`FLESH`) | Chain of Command, Store Policy, Upsell | Entitled Customer, Extreme Couponer |
| Crowds (`VERDANT`) | Hustle | Floor Rush Crowd |
| Lifers (`UNDEAD`) | Solidarity, Hustle, Chain of Command | Slacker Veteran (boss), `rival_derrick`, `rival_chad` |
| Fixtures (`CONSTRUCT`) | Elbow Grease, Equipment | Practice Dummy, **The Screamer** (`runaway_pallet_jack`), **Go-Back Golem** |
| Loss Prevention (`VOID`) | Equipment, Solidarity | Loss Prevention Clint (boss), **Shrink Sentinel**, **Asset Protection Officer** |
| Management (`RADIANT`) | Upsell, Solidarity | Store Manager Henderson (boss), `rival_todd`, `rival_marsha` |
| Corporate (`SILENCE`) | Store Policy | Regional Auditor (boss), `rival_cheryl` |

Each enemy can add its own `weak` (x2.0) and `resist` (x0.8) on top of its family. The four **bold** enemies still need their own sprite sheets (briefed in [`art/SPRITE_BRIEFS.md`](art/SPRITE_BRIEFS.md); a placeholder sheet draws them until then); the five `rival_*` enemies use the store staff's delivered sheets.

### Kits learned from the story

Every class learns at the same four milestones, set on its own department's act-boss victory nodes:

| Flag (set on) | Cashier | Stocker | Unloader | Specialist |
| :--- | :--- | :--- | :--- | :--- |
| `flag_act1_showcase_won` (shift 10 victory) | Drawer Smash | Flatbed Charge | Baler Grind | Barcode Scan |
| `flag_act2_slacker_beaten` (shift 22 victory) | Express Lane | Escalate | Escalate | Flash Sale |
| `flag_act3_audit_survived` (shift 34 victory) | Escalate | By the Book | By the Book | By the Book |
| `flag_act4_interrogation_broken` (shift 46 victory) | Strength in Numbers | Strength in Numbers | Strength in Numbers | Strength in Numbers |

---

## 3. Real-Time Speech Recognition & Continuous Audio Arbitration Loop

The core hands-free conversational loop is coordinated by [`SpeechManager.kt`](app/src/main/java/com/voicerpg/engine/audio/SpeechManager.kt) and [`IntentParser.kt`](app/src/main/java/com/voicerpg/engine/engine/IntentParser.kt).

### Audio Arbitration State Machine

To prevent the Android `SpeechRecognizer` from picking up synthesized Text-To-Speech playback or retail intercom chimes, the engine executes a strict arbitration state machine:

```
  +--------------+   Narration Begins    +-------------------+
  |     IDLE     | --------------------> |   TTS_NARRATING   |
  +--------------+                       +-------------------+
         ^                                         |
         | Utterance Handled                       | Utterance Completed
         |                                         v
  +------------------+   Utterance Ready +-------------------+
  |  INTENT_DISPATCH | <---------------- |     MIC_ARMED     |
  +------------------+                   +-------------------+
         |                                         |
         v                                         | Voice Energy Detected
  +------------------+                             v
  |  RESONANCE_EVAL  | <----------------- +-------------------+
  +------------------+    RMS Threshold   |    RECOGNIZING    |
                                          +-------------------+
```

- **Audio Chime Suppression**: Notification audio streams are temporarily muted during microphone listening windows to eliminate distracting OS audio pings.
- **Dynamic Intent Parsing**: Utterances are mapped to live combat targets, customer personas, or register keypad functions with zero latency.

---

## 4. Resonance Engine & Acoustic-Poetic Scoring Math

Department abilities and customer appeasements are evaluated by [`ResonanceEngine.kt`](app/src/main/java/com/voicerpg/engine/engine/ResonanceEngine.kt). The compound resonance rating $R$ is formulated as:

$$R = S_{\text{acoustic}} + S_{\text{cadence}} + S_{\text{poetic}} + S_{\text{novelty}}$$

Where:

1. **Acoustic Energy ($S_{\text{acoustic}}$)**:
   Evaluated from peak decibel volume and fundamental pitch contour variation $\Delta f_0$:
   $$S_{\text{acoustic}} = \min\left(30, \max\left(0, \frac{\text{dB}_{\text{peak}} - 45}{40} \times 30\right)\right) + S_{\text{pitch}}$$

2. **Vocal Cadence ($S_{\text{cadence}}$)**:
   Measures pacing and rhythmic meter across syllables:
   $$S_{\text{cadence}} = 25 \times \exp\left(-\frac{(T_{\text{utterance}} - T_{\text{optimal}})^2}{2\sigma^2}\right)$$

3. **Phonetic Thesaurus Matching ($S_{\text{poetic}}$)**:
   Matches spoken phrases against department keyword thesauruses (`SpellThesaurus.kt`) using normalized Levenshtein distance:
   $$\text{Sim}(u, t) = 1 - \frac{\text{lev}(u, t)}{\max(|u|, |t|)}$$
   Words with $\text{Sim}(u, t) \ge 0.75$ contribute up to 35 points to $S_{\text{poetic}}$.

4. **Novelty Bonus ($S_{\text{novelty}}$)**:
   Rewards variations in phrasing across consecutive turns (up to 10 points), penalizing repetitive macro shouting.

### Resonance Multiplier Tiers

| Tier Name | Score Range | Damage / Recovery Multiplier | Visual & Audio Flair |
| :--- | :--- | :--- | :--- |
| **Novice** | $0 \le R < 40$ | **1.00x** (Standard effect) | Standard register beep |
| **Apprentice** | $40 \le R < 70$ | **1.15x** Base Boost | Green phosphor blink |
| **Adept** | $70 \le R < 100$ | **1.35x** High Efficiency | Blue scanner flash |
| **Master** | $100 \le R < 130$ | **1.85x** Critical Override | Dual amber warning light |
| **Mythic** | $130 \le R < 155$ | **2.25x** Manager Authorization | Red strobe & siren tone |
| **Transcendental** | $R \ge 155$ | **3.00x** (Absolute Cap) | Golden corporate broadcast flash |

---

## 5. Real-Time Audio DSP & Dynamic Music Ducking Pipeline

Ambient store music tracks and intercom announcements are modulated in real-time by [`SoundbitePlayer.kt`](app/src/main/java/com/voicerpg/engine/audio/SoundbitePlayer.kt) and `MusicManager`:

$$V_{\text{bgm}}(t) = V_{\text{base}} \cdot \Big(1 - \alpha \cdot \mathbb{I}_{\text{speaking}}(t)\Big)$$

Where:
- $V_{\text{base}}$ is the configured master background volume.
- $\alpha \approx 0.70$ is the speech ducking attenuation coefficient (reducing ambient music to $-10.5\text{ dB}$).
- $\mathbb{I}_{\text{speaking}}(t) \in \{0, 1\}$ is the boolean speech indicator provided by the arbitration engine.

Audio tracks are normalized to **$-16\text{ LUFS}$** (44.1 kHz Vorbis `.ogg`) to ensure consistent loudness across speakers and mobile headsets.

---

## 6. Dynamic Retail Conflict Incidents & Conveyor ATB Battle System

Integrated with [`IncidentManager.kt`](app/src/main/java/com/voicerpg/engine/engine/IncidentManager.kt), the game dynamically injects emergency store events into the Active Time Battle (ATB) conveyor:

- **Aisle Spills & Code Sparks**: Sudden environmental hazards requiring prompt vocal resolution.
- **Unannounced Corporate Audits**: High-pressure pop-up encounters adjusting Corporate Morale and Store Metrics in real time.
- **Conveyor ATB Initiative**: Action gauges fill proportionally to Employee Agility $\text{Spd}_e$ versus Customer Fury $\text{Spd}_c$:
  $$\Delta \text{Gauge}_i = \frac{\text{Spd}_i}{\text{TickRate}} \times \Delta t$$

---

## 7. Multi-Repo Synchronization Workflow

*Superstore Saga* stays synchronized with upstream framework improvements via the automated multi-repo pipeline:

```bash
# Push engine updates from voice-rpg-engine to Superstore Saga:
cd ../voice-rpg-engine && ./scripts/sync_engine_to_games.sh superstore-saga

# Audit synchronization status across all downstream games:
cd ../voice-rpg-engine && ./scripts/sync_engine_to_games.sh --check
```

---

## 8. Automated Unit Verification Suite

The repository contains **368 automated unit tests** verifying dialogue graph traversal, retail ability damage resolution, and the headless **Balance Harness** ([`BalanceHarnessTest.kt`](app/src/test/java/com/voicerpg/engine/combat/BalanceHarnessTest.kt)):

```bash
# Run full unit test suite:
./gradlew testDebugUnitTest

# Build debug APK:
./gradlew assembleDebug
```

**Combat balance.** Every story fight is played by the engine's battle simulator (`BalanceContractTest`: the real combat on virtual time, with the party and spells the story has given them at that point): 56 fights (14 for each of the 4 departments). A sensible player at the balance point (**x1.5** resonance, [`combat/balance.json`](app/src/main/assets/game/combat/balance.json)) should win in a target number of rounds with a safety margin that narrows toward the finale, a lazy x1.1 player should be able to lose late bosses, healing should never take over a fight, and Pure Story Mode must win every fight. The enemy HP and attack in [`encounters.json`](app/src/main/assets/game/encounters/encounters.json) were tuned to those targets with the engine's `BalanceTunerTest`; the report of every fight is written to `app/build/reports/balance.txt` (it reports rather than fails the build: a handful of fights still sit just outside a target).

Test coverage includes:
- Dialogue graph cyclic and dead-end detection.
- Department spell balance simulations over 1,000 automated battle rounds.
- Resonance acoustic scoring threshold boundaries.

---

## 9. Clean-Room Provenance & Non-Commercial Licensing

- **Clean-Room Retail Satire**: All character names, department lore, dialogue scripts, and audio assets are 100% original satirical creations.
- **AI-Assisted Battle Art**: the battle sprite sheets and the battle backdrops are AI-assisted (made with Antigravity from the game's own portraits and briefs).
- **PolyForm Noncommercial 1.0.0**: Free for personal, recreational, educational, and hobbyist gaming. For commercial inquiries, contact **[Todd.0042.git@gmail.com](mailto:Todd.0042.git@gmail.com)**.
- **Media License**: the images, artwork and audio made for this project (paths listed in [LICENSE-MEDIA](LICENSE-MEDIA)) are © Todd0042 under **[CC BY-NC 4.0](LICENSE-MEDIA)**: free to share and adapt with credit for non-commercial purposes; commercial use requires permission.

---

<p align="center">
  If you enjoy surviving shifts at MegaMart, consider supporting its development:<br/><br/>
  <a href="https://ko-fi.com/todd0042" target="_blank">
    <img src="https://img.shields.io/badge/☕_Support_Superstore_Saga_on_Ko--fi-F16061?style=for-the-badge&logo=ko-fi&logoColor=white" alt="Support on Ko-fi">
  </a>
</p>

---

<p align="center">
  <a href="https://ko-fi.com/todd0042">
    <img src="https://raw.githubusercontent.com/Todd0042/creator-assets/main/banners/todd_creator_banner_v2.0.png" alt="Support Todd on Ko-fi" width="100%" />
  </a>
</p>
