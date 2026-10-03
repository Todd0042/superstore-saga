# Superstore Saga: Master Database & Documentation Index

> **Purpose:** Quick-lookup routing table mapping game systems, features, and content edits directly to their **Design Specifications**, **Runtime JSON Data Assets**, and **Engine Code**.

---

## 1. Quick Feature-to-File Routing Map

When you want to edit or add a feature, consult this table to open the exact file needed without searching through the full documentation:

| If you want to edit... | Primary Live Runtime File (`assets/game/`) | Governing Spec Document (Root `.md`) | Engine Code (`app/src/main/java/`) |
| :--- | :--- | :--- | :--- |
| **Story, Dialogue & Choice Trees** | `story/nodes.json`<br>`story/scenes.json` | [`STORY_SCRIPT_ACT1.md`](file:///home/todd/Documents/GitHub/superstore-saga/STORY_SCRIPT_ACT1.md)<br>[`NARRATIVE_DESIGN.md`](file:///home/todd/Documents/GitHub/superstore-saga/NARRATIVE_DESIGN.md) | `StoryViewModel.kt`<br>`StoryScreen.kt` |
| **Store Environments & Music** | `story/scenes.json`<br>`audio/` | [`ASSET_MANIFEST.md`](file:///home/todd/Documents/GitHub/superstore-saga/ASSET_MANIFEST.md) | `MusicManager.kt`<br>`StoryAssetLoader.kt` |
| **Characters, Names & TTS Voices** | `characters/*.json`<br>`portraits/` | [`ASSET_MANIFEST.md`](file:///home/todd/Documents/GitHub/superstore-saga/ASSET_MANIFEST.md) | `CombatNarrator.kt`<br>`AudioSetupScreen.kt` |
| **Pre-Employment Screening Questions** | `story/nodes.json` (`node_app_*`) | [`STORY_SCRIPT_ACT1.md`](file:///home/todd/Documents/GitHub/superstore-saga/STORY_SCRIPT_ACT1.md) | `StoryViewModel.kt` (`computeAssessmentScore`) |
| **Department Roles (Cashier, Stocker, etc.)** | `classes/*.json` | [`OVERALL_DESIGN.md`](file:///home/todd/Documents/GitHub/superstore-saga/OVERALL_DESIGN.md) | `CharacterCreationScreen.kt` |
| **Abilities, POS Spells & Chants** | `spells/spells.json`<br>`spells/thesaurus.json` | [`COMBAT_DESIGN.md`](file:///home/todd/Documents/GitHub/superstore-saga/COMBAT_DESIGN.md) | `CombatViewModel.kt`<br>`ResonanceEngine.kt` |
| **Bosses, Enemies & Battle Encounters** | `enemies/enemies.json`<br>`encounters/encounters.json` | [`COMBAT_DESIGN.md`](file:///home/todd/Documents/GitHub/superstore-saga/COMBAT_DESIGN.md) | `CombatViewModel.kt`<br>`RetroBattleScreen.kt` |
| **Store Metrics (Morale, Karen Index, Squeeze)** | `metrics/metrics.json` | [`SCHEMA_SPEC.md`](file:///home/todd/Documents/GitHub/superstore-saga/SCHEMA_SPEC.md) | `StoryScreen.kt` (HUD)<br>`StoryViewModel.kt` |
| **Workplace Drama & Incidents** | `incidents/incidents.json` | [`CONFLICT_EVENTS.md`](file:///home/todd/Documents/GitHub/superstore-saga/CONFLICT_EVENTS.md) | `StoryViewModel.kt` |
| **Hourly Wages, Vending & Inventory** | *(Pending economy JSON)* | [`ECONOMY_SYSTEM.md`](file:///home/todd/Documents/GitHub/superstore-saga/ECONOMY_SYSTEM.md) | *(Economy ViewModel)* |
| **Department Mini-Games** | *(Mini-game JSON)* | [`MINIGAMES_SPEC.md`](file:///home/todd/Documents/GitHub/superstore-saga/MINIGAMES_SPEC.md) | *(Mini-game Composables)* |
| **Screen Layouts, Badges & UI Theme** | `ui/theme/Theme.kt` | [`UI_UX_DESIGN.md`](file:///home/todd/Documents/GitHub/superstore-saga/UI_UX_DESIGN.md) | `StoryScreen.kt`<br>`RetroBattleScreen.kt` |
| **Image Generation Prompts** | `portraits/`, `story/` | [`IMAGE_GENERATION_PROMPTS.md`](file:///home/todd/Documents/GitHub/superstore-saga/IMAGE_GENERATION_PROMPTS.md) | N/A |
| **JSON Data Contracts & Schemas** | `manifest.json` | [`SCHEMA_SPEC.md`](file:///home/todd/Documents/GitHub/superstore-saga/SCHEMA_SPEC.md) | `GameContent.kt` |

---

## 2. Directory Hierarchy Guide

### `app/src/main/assets/game/` (The Live Runtime Database)
Every piece of data that the engine loads during gameplay lives here:
* `manifest.json` — Root game settings, title, start node, defeat/victory lines, initial store number.
* `story/`
  * `nodes.json` — Every dialogue node, speaker ID, text, choices, voice keywords, and flag mutations.
  * `scenes.json` — Store zones/rooms, background art links, and background music links.
* `characters/` — JSON files for each character (`donna.json`, `cheryl.json`, `henderson.json`, etc.) with TTS voice IDs and colors.
* `classes/` — Department starting archetypes (`class_cashier.json`, `class_stocker.json`, etc.).
* `metrics/` — Live game gauges (`metrics.json` tracking Store Morale, Karen Index, Labor Squeeze, Engagement).
* `spells/` — Combat abilities (`spells.json`) and chant dictionary (`thesaurus.json`).
* `enemies/` & `encounters/` — Battle adversaries, boss stats, and combat triggers.
* `incidents/` — Shift conflict pool (`incidents.json`).

### Root `.md` Files (Design Blueprints & Reference Bibles)
These are planning documents created to design the game before compiling into JSON:
* [`SCHEMA_SPEC.md`](file:///home/todd/Documents/GitHub/superstore-saga/SCHEMA_SPEC.md) — Exact schema definitions and JSON sample structures.
* [`STORY_SCRIPT_ACT1.md`](file:///home/todd/Documents/GitHub/superstore-saga/STORY_SCRIPT_ACT1.md) — Raw text and dialogue script for Act I.
* [`NARRATIVE_DESIGN.md`](file:///home/todd/Documents/GitHub/superstore-saga/NARRATIVE_DESIGN.md) — High-level 5-Act storyline, department tracks, and character motivations.
* [`CONFLICT_EVENTS.md`](file:///home/todd/Documents/GitHub/superstore-saga/CONFLICT_EVENTS.md) — Massive catalog of customer drama, peer disputes, and corporate inspections.
* [`COMBAT_DESIGN.md`](file:///home/todd/Documents/GitHub/superstore-saga/COMBAT_DESIGN.md) — POS Terminal combat mechanics, stats, and boss strategies.
* [`ECONOMY_SYSTEM.md`](file:///home/todd/Documents/GitHub/superstore-saga/ECONOMY_SYSTEM.md) — Satirical payroll, paystubs, breakroom vending, and store credit.
* [`MINIGAMES_SPEC.md`](file:///home/todd/Documents/GitHub/superstore-saga/MINIGAMES_SPEC.md) — Mini-game mechanics (Scanner, Cart Wrangler, Truck Unload, Zone Recovery).
* [`UI_UX_DESIGN.md`](file:///home/todd/Documents/GitHub/superstore-saga/UI_UX_DESIGN.md) — Screen hierarchy, badge layouts, receipt styling, and wireframes.
* [`ASSET_MANIFEST.md`](file:///home/todd/Documents/GitHub/superstore-saga/ASSET_MANIFEST.md) — Character visual dossiers, TTS voice mappings, and audio requirements.
* [`IMAGE_GENERATION_PROMPTS.md`](file:///home/todd/Documents/GitHub/superstore-saga/IMAGE_GENERATION_PROMPTS.md) — Complete ready-to-copy image generation prompts.
* [`OVERALL_DESIGN.md`](file:///home/todd/Documents/GitHub/superstore-saga/OVERALL_DESIGN.md) — High-level game design document (GDD).
* [`AGENTS.md`](file:///home/todd/Documents/GitHub/superstore-saga/AGENTS.md) — Engineering rules (content neutrality, settle pattern in tests, Android invariants).
