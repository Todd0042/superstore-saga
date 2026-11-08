<p align="center">
  <a href="https://ko-fi.com/todd0042">
    <img src="https://raw.githubusercontent.com/Todd0042/creator-assets/main/banners/todd_creator_banner_v1.0.png" alt="Support Todd on Ko-fi" width="100%" />
  </a>
</p>

---

# 🛒 Superstore Saga: Escape From MegaMart

[![Version](https://img.shields.io/badge/Version-0.1.0--alpha-ff2a85.svg)](https://github.com/Todd0042/superstore-saga/releases)
[![License: PolyForm Noncommercial](https://img.shields.io/badge/License-PolyForm%20Noncommercial%201.0.0-00f0ff.svg)](LICENSE)
[![Platform: Android 8.0+](https://img.shields.io/badge/Platform-Android%208.0%2B%20(API%2026%2B)-3DDC84.svg?logo=android&logoColor=white)](https://developer.android.com)
[![16 KB Page Size](https://img.shields.io/badge/16%20KB-Compatible-success.svg)](https://developer.android.com/16kb-page-size)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF.svg?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![UI: Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%202024.10.01-4285F4.svg?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Android CI](https://github.com/Todd0042/superstore-saga/actions/workflows/android_ci.yml/badge.svg)](https://github.com/Todd0042/superstore-saga/actions)
[![Unit Tests](https://img.shields.io/badge/Unit%20Tests-108%20Passing-39ff14.svg)](app/src/test/java/com/voicerpg/engine/)
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
* **Part 2 — Technical Architecture & Engine Integration**
  * [1. Powered by VoiceRPG Engine](#1-powered-by-voicerpg-engine)
  * [2. Retail Content Decoupling & JSON Schema (`assets/game/`)](#2-retail-content-decoupling--json-schema)
  * [3. Speech Recognition & Hands-Free Store Commands (`SpeechManager.kt`)](#3-speech-recognition--hands-free-store-commands)
  * [4. Department Ability Resonance & POS Timing (`SpellChantPresets.kt`)](#4-department-ability-resonance--pos-timing)
  * [5. Dynamic Retail Conflict Incidents (`IncidentManager.kt`)](#5-dynamic-retail-conflict-incidents)
  * [6. Multi-Repo Synchronization Workflow (`sync_engine_to_games.sh`)](#6-multi-repo-synchronization-workflow)
  * [7. Automated Unit Verification Suite (`tests/`)](#7-automated-unit-verification-suite)
  * [8. Clean-Room Provenance & Non-Commercial Licensing](#8-clean-room-provenance--non-commercial-licensing)

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

1. **Clock In & Select Your Department**: Begin your probationary shift as an applicant at Store #1042. Choose your role—Cashier, Stocker, Customer Service, or Unloader.
2. **Handle Customer Confrontations**: When chaotic customers or corporate auditors attack, issue voice commands on your Point-of-Sale (POS) terminal to de-escalate, parry expired coupons, or summon managerial backup.
3. **Survive the Closing Shift**: Manage your Stamina (HP), Focus (MP), and Corporate Metrics (Patience, Morale) to make it to the time clock and punch out.

---

## 🏢 The 4 Department Career Tracks

| Department Track | Archetype & Combat Role | Signature Abilities | Corporate Weapon |
| :--- | :--- | :--- | :--- |
| **Front End (Cashier)** | High-Velocity Striker & Crowd Control | *Barcode Whip, Price Check Page, Coupon Rejection, Drawer Slam* | Handheld Laser Scanner |
| **Sales Floor (Stocker)** | Heavy Tank & Environmental Shaper | *Box Cutter Flurry, Pallet Jack Ram, Z-Zone Facing, Overstock Crush* | Steel Safety Box Cutter |
| **Backroom (Unloader)** | Heavy Physical DPS & Impact Brute | *Bale Wire Snare, Dock Plate Slam, Hydraulic Lift Drop, Forklift Horn* | Hydraulic Pallet Jack |
| **Customer Service (Tamer)** | Social Debuffer & Karen Neutralizer | *Kill With Kindness, Policy Recitation, Gift Card Pacify, Corporate Escalation* | Return Slip Clipboard |

---

## ⚔️ The POS Register Terminal Battle System

Combat in *Superstore Saga* represents high-stakes retail showdowns. The combat interface is styled as a retro Point-of-Sale terminal:
- **Stamina (HP)**: Your physical energy (0–1,000). Dropping to 0 triggers an unpaid disciplinary review in HR.
- **Focus / Patience (MP)**: Your mental bandwidth (0–100) needed to perform complex procedural overrides and customer appeasements.
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

# Part 2 — Technical Architecture & Engine Integration

## 1. Powered by VoiceRPG Engine

*Superstore Saga* is built on top of **[VoiceRPG Engine](https://github.com/Todd0042/voice-rpg-engine)**, inheriting:
- **Clean Architecture**: Complete separation of generic engine logic (`com.voicerpg.engine.*`) from satirical retail story content.
- **Continuous Speech Arbitration**: Zero-overlap audio arbitration between Android `TextToSpeech` and `SpeechRecognizer`.
- **100% On-Device & Offline Privacy**: Employs `EXTRA_PREFER_OFFLINE` with hardware-accelerated on-device recognition (`SpeechRecognizer.createOnDeviceSpeechRecognizer`) on Android 12+ (API 31+).

---

## 2. Retail Content Decoupling & JSON Schema

All game data is 100% decoupled from Kotlin source code and defined in JSON under `app/src/main/assets/game/`:
- [`manifest.json`](app/src/main/assets/game/manifest.json): World title, starting scene (`scene_online_app`), and default encounter (`encounter_register_rush`).
- `characters/*.json`: Store managers, shift leads, and companion associates.
- `enemies/enemies.json`: Enemy templates (*Entitled Karen, Coupon Hoarder, Loss Prevention Sentinel*).
- `spells/spells.json`: Department abilities mapped to the 5 tactical categories (`[ATTACK]`, `[AOE]`, `[HEAL]`, `[DEFENSE]`, `[MANA]`).
- `story/scenes.json` & `story/nodes.json`: Dialogue trees across MegaMart departments.

---

## 3. Speech Recognition & Hands-Free Store Commands

The conversational loop is managed by [`SpeechManager.kt`](app/src/main/java/com/voicerpg/engine/audio/SpeechManager.kt) and [`IntentParser.kt`](app/src/main/java/com/voicerpg/engine/engine/IntentParser.kt):
- **Store-Themed Dynamic Entity Matching**: Translates voice utterances directly to active sales floor targets (*"Scan the coupon hoarder"*, *"Page manager"*).
- **Audio Chime Suppression**: Temporarily mutes notification audio streams during mic opening to eliminate repetitive OS beeps.

---

## 4. Department Ability Resonance & POS Timing

Abilities are evaluated via the **Resonance Engine** using acoustic pitch detection, cadence analysis, and lexical keyword matching:
- **Basic (1.0x)**: Simple keyword match (*"Scan item"*).
- **Adept (1.35x)**: Polite procedural phrasing (*"Please scan item for customer"*).
- **Master (1.85x)**: Department authority tone (*"Authorized associate scanning item immediately"*).
- **Transcendental (3.0x)**: Full corporate announcement (*"Attention all MegaMart shoppers, clean up on Aisle 4, execute full register override now!"*).

---

## 5. Dynamic Retail Conflict Incidents

Integrated with [`IncidentManager.kt`](app/src/main/java/com/voicerpg/engine/engine/IncidentManager.kt), the game dynamically injects emergency store events:
- **Aisle Spills & Code Sparks**: Sudden environmental hazards requiring quick verbal resolution.
- **Unannounced Corporate Audits**: High-pressure pop-up encounters adjusting Corporate Morale and Store Metrics in real time.

---

## 6. Multi-Repo Synchronization Workflow

*Superstore Saga* stays up to date with the core framework via the upstream distribution pipeline:
```bash
# Push engine updates from voice-rpg-engine to Superstore Saga:
cd ../voice-rpg-engine && ./scripts/sync_engine_to_games.sh superstore-saga

# Audit synchronization status:
cd ../voice-rpg-engine && ./scripts/sync_engine_to_games.sh --check
```

---

## 7. Automated Unit Verification Suite

The repository contains **108 automated unit tests** verifying dialogue graph traversal, retail ability damage resolution, and the headless **Balance Harness** ([`BalanceHarnessTest.kt`](app/src/test/java/com/voicerpg/engine/combat/BalanceHarnessTest.kt)):
```bash
# Run full unit test suite:
./gradlew testDebugUnitTest

# Build debug APK:
./gradlew assembleDebug
```

---

## 8. Clean-Room Provenance & Non-Commercial Licensing

- **Clean-Room Retail Satire**: All character names, department lore, dialogue scripts, and audio assets are 100% original satirical creations.
- **PolyForm Noncommercial 1.0.0**: Free for personal, recreational, educational, and hobbyist gaming. For commercial inquiries, contact **[todd.0042.git@gmail.com](mailto:todd.0042.git@gmail.com)**.

---

<p align="center">
  If you enjoy surviving shifts at MegaMart, consider supporting open-source development:<br/><br/>
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
