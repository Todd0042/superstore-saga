# Superstore Saga: Agent Guidelines & Architecture Rules

This repository is **Superstore Saga: Escape From MegaMart** (`com.voicerpg.superstoresaga`), a satirical corporate survival voice JRPG built natively for Android using the **VoiceRPG Engine**.
Upstream engine repository: `/home/todd/Documents/GitHub/voice-rpg-engine`

---

## 1. Upstream Engine Separation & Sync Protocol
- **Shared Code Belongs Upstream**: All core engine packages under `app/src/main/java/com/voicerpg/engine/` and shared assets (`assets/game/commands/`, `assets/game/localization/`) originate in `voice-rpg-engine`.
- **Never Maintain Local Divergent Forks**: If an engine bug is fixed or an engine improvement is made while developing Superstore Saga:
  1. The change must be ported back to `voice-rpg-engine`.
  2. You can pull the changes upstream using:
     ```bash
     cd ../voice-rpg-engine && ./scripts/sync_engine_to_games.sh --pull-from superstore-saga --verify
     ```
  3. This ensures all companion games (`echoes-of-logos`, `wallys-world`, `commander-america-rpg`, `superstore-saga`) remain synchronized.

---

## 2. Receiving Engine Updates
- When `voice-rpg-engine` is updated, pull updates down via:
  ```bash
  cd ../voice-rpg-engine && ./scripts/sync_engine_to_games.sh superstore-saga
  ```
- Game-specific story narrative, dialogue, characters, encounters, and art assets (in `app/src/main/assets/game/`) are protected and will never be overwritten.

---

## 3. Opt-In Feature Adoption
- Superstore Saga is **not required to utilize every new feature** added to the engine (e.g. selfie generation, parallel story tracks, mini-games).
- The game maintains seamless compatibility with the engine. When the engine updates, this game pulls and compiles cleanly without forced adoption of irrelevant features.

---

## 4. Game Content & Data Architecture
- All retail story chapters, employee classes, store departments, customer encounters, and abilities live strictly in `app/src/main/assets/game/**` as JSON:
  - `manifest.json` (Title, MegaMart Store #1042 world, initial scene, victory/defeat lines)
  - `characters/*.json`, `classes/*.json`, `spells/*.json` (Barcode Whip, Price Check Page, Coupon Rejection)
  - `enemies/enemies.json`, `encounters/encounters.json` (Entitled Karen, Coupon Hoarder, Loss Prevention Sentinel)
  - `story/scenes.json`, `story/nodes.json`
- Bundled art and audio live in `app/src/main/assets/{portraits,story,audio,sprites}`.

---

## 5. Automated Verification Before Any Commit
- Full unit test suite: `./gradlew testDebugUnitTest` (must be 100% green).
- Clean compilation: `./gradlew compileDebugKotlin` (must be clean).