# Game Repository: Upstream VoiceRPG Engine Protocol

This repository is a game title built on top of the **VoiceRPG Engine**. 
Engine source code: `/home/todd/Documents/GitHub/voice-rpg-engine`

---

## 1. Upstream-First Rule
- All shared engine packages under `app/src/main/java/com/voicerpg/engine/` and shared assets (`assets/game/commands/`, `assets/game/localization/`) belong upstream in `voice-rpg-engine`.
- **Never keep local, forked engine logic**: If you fix an engine bug or add an engine feature while working here:
  1. The change must be ported back to `voice-rpg-engine`.
  2. You can pull the changes upstream from `voice-rpg-engine` using:
     ```bash
     cd ../voice-rpg-engine && ./scripts/sync_engine_to_games.sh --pull-from superstore-saga --verify
     ```
  3. This ensures all sister games (`echoes-of-logos`, `wallys-world`, `commander-america-rpg`, `superstore-saga`) benefit from the fix.

---

## 2. Receiving Engine Updates
- When `voice-rpg-engine` updates, this game receives updates via:
  ```bash
  cd ../voice-rpg-engine && ./scripts/sync_engine_to_games.sh superstore-saga
  ```
- Game-specific story narrative, dialogue, characters, encounters, and art assets (in `app/src/main/assets/game/`) are protected and will never be overwritten.

---

## 3. Opt-In Feature Adoption
- This game is **not required to utilize every new feature** added to the engine (e.g. selfie generation, parallel story tracks, mini-games).
- The game merely maintains seamless compatibility with the engine. When the engine updates, the game should pull and compile cleanly without forced adoption of irrelevant features.
