# Superstore Saga: battle sprite brief

Generated from `art/sprite_roster.json` by `voice-rpg-engine/scripts/sprite_roster.py`; edit the roster, not this file.
Every fighter, enemy and boss is mapped to a sprite key below. A sheet named after its key, dropped into
`app/src/main/assets/game/sprites/sheets/`, appears in battle as soon as the game runs the sprite-ready engine.

A retail workplace comedy set in MegaMart Store #1042. The look is 16-bit JRPG with a wink: ordinary store staff and customers drawn as if they were knights, mages and monsters, their weapons the tools of the trade (price guns, box cutters, coupon binders, clipboards). Match the portraits in `app/src/main/assets/portraits/` for faces, hair and uniforms: MegaMart associates wear a royal-blue vest with a name badge; managers wear a royal-blue polo or business casual with a lanyard.

**To draw: 0 sheets** (0 party fighters, 0 enemy families, 0 bosses). Everything else is a recolour of one of those.

## Style (every sprite)

- 16-bit SNES-era JRPG battle sprite, side view, facing RIGHT (the engine mirrors enemies to face the party).
- Limited palette (4-8 colours per sprite), 1 px dark outline, no anti-aliasing, transparent background.
- Cell 64x64 for party fighters and regular enemies (the body about 40-56 px tall); bosses 96x96 or 128x128 as listed.
- Soles on the same row in every frame (row 58 of a 64 cell, 90 of 96, 122 of 128); floating things put their lowest point or hover shadow there. The feet slice's bottom edge is the sole line.
- Nothing but the character in a frame: no labels, captions, frame names or text.
- Frame duration 190 ms unless a move needs faster frames; events in the tag user data, counted from 1 within the tag.
- Store lighting: colours slightly warm and saturated, like the fluorescent sales floor; keep outfits readable at 64 px (vest colour, badge, one signature prop each).

## Animations (Aseprite tags)

- **Party fighters**: idle (4f loop), ready (4f loop: turn ready, weapon up), charge (4f loop: speaking a chant), cast (5f, release=4), attack (5f, impact=4), hurt (4f), ko (6f, ends lying down), victory (4f loop). A fighter who is also fought as a boss adds windup, charge_ultimate, ultimate (and death).
- **Regular enemies**: required idle (4f loop), attack (5f, impact=4), hurt (4f); recommended windup (4f loop), cast (5f, release=4), death (6f; else the engine shatters it).
- **Bosses**: required idle (4f loop), windup (4f loop), attack (5f, impact=4), cast (5f, release=4), hurt (4f), charge_ultimate (4f loop), ultimate (6-8f, release=5); recommended death (6-8f), taunt (4f), p2_idle, p2_attack... (second phase).

## Delivering a sprite

1. Save `<key>.png` and its Aseprite JSON export `<key>.json` in `app/src/main/assets/game/sprites/sheets/`, with the tags above and a `feet` slice.
   Previews go in `art/sprite-previews/`, never in the sheets folder.
2. Run `python3 ../voice-rpg-engine/scripts/sprite_lint.py . --fix`, then `../voice-rpg-engine/scripts/sprite_coverage.sh <game>`.
3. Compare it with everyone else in the game's gallery, **SPRITES** tab.
4. A recolour (listed under *Recolours*) needs no art; a sheet named after its own id replaces the recolour if you want it to differ.

## Party fighters (1, 64 px)

### `hero`: The Associate (player) ✅ delivered

> **Deliver:** `app/src/main/assets/game/sprites/sheets/hero.png` + `hero.json` · **party** · **64x64** cells · tags: idle, ready, charge, cast (release=4), attack (impact=4), hurt, ko, victory

Portrait: `portraits/player_applicant.jpg`.

The player's new-hire MegaMart associate: royal-blue vest over a grey shirt, name badge and lanyard, khakis, sturdy work shoes, short practical hair (keep the face simple and neutral: the player is anyone). Carries a price-scanner gun holstered at the hip and a box cutter. Stance of an eager rookie: shoulders square, ready.

*Moves*: charge: holds the scanner up, its red laser flickering as the chant builds; cast: fires the scanner like a wand, a beam of barcode light (release on the beep); attack: a quick box-cutter slash or a shoulder-check with a clipboard; ko: slumps against an imaginary shelf and slides down; victory: thumbs-up and a pumped fist.

## Enemy families (6, 64 px)

### `practice_dummy`: Shift Trainer Mannequin ✅ delivered

> **Deliver:** `app/src/main/assets/game/sprites/sheets/practice_dummy.png` + `practice_dummy.json` · **enemy** · **64x64** cells · tags: idle, attack (impact=4), hurt (+ windup, cast (release=4), death)

The voice tutorial's practice dummy wears the training-drone sheet generated for the starter kit (delivered).

*Moves*: attack: wobbles forward on its base; hurt: spins half around; death: topples over and the head pops off.

Draws: Practice Dummy (Training Target, construct/physical)

### `karen_customer`: Entitled Customer ✅ delivered

> **Deliver:** `app/src/main/assets/game/sprites/sheets/karen_customer.png` + `karen_customer.json` · **enemy** · **64x64** cells · tags: idle, attack (impact=4), hurt (+ windup, cast (release=4), death)

A furious shopper: asymmetrical blonde bob, oversized sunglasses pushed up on the head, a puffer vest, a giant designer handbag swung like a flail, a receipt clutched in the other fist. Leans forward, finger pointing.

*Moves*: attack: swings the handbag overhead; windup: jabs a pointing finger ('Manager!'); cast: slaps the receipt down, a shockwave of outrage.

Draws: Entitled Customer (Escalation Vanguard, flesh/physical)

### `overtime_screamer`: The Floor Rush Crowd ✅ delivered

> **Deliver:** `app/src/main/assets/game/sprites/sheets/overtime_screamer.png` + `overtime_screamer.json` · **enemy** · **64x64** cells · tags: idle, attack (impact=4), hurt (+ windup, cast (release=4), death)

The Floor Rush Crowd: a sale-day shopper in a red hoodie pushing a cart piled with discounted goods (recolour; a separate sheet would ideally show the cart).

*Moves*: attack: swings the handbag overhead; windup: jabs a pointing finger ('Manager!'); cast: slaps the receipt down, a shockwave of outrage.

Draws: Floor Rush Crowd (Bargain Swarm, flesh/physical)

### `extreme_couponer`: Extreme Couponer ✅ delivered

> **Deliver:** `app/src/main/assets/game/sprites/sheets/extreme_couponer.png` + `extreme_couponer.json` · **enemy** · **64x64** cells · tags: idle, attack (impact=4), hurt (+ windup, cast (release=4), death)

A wiry coupon strategist in a fishing vest stuffed with clipped coupons, reading glasses on a beaded chain, a three-ring binder of coupons hugged like a spellbook, scissors in the other hand.

*Moves*: cast: fans coupons from the binder and flicks them like throwing cards (release=4); attack: snips with the scissors.

Draws: Extreme Couponer (Binder Tactician, flesh/shadow)

### `regional_auditor`: Regional Auditor ✅ delivered

> **Deliver:** `app/src/main/assets/game/sprites/sheets/regional_auditor.png` + `regional_auditor.json` · **enemy** · **64x64** cells · tags: idle, attack (impact=4), hurt (+ windup, cast (release=4), death)

A cold corporate auditor: grey suit, rimless glasses, a slicked parting, a laptop open on one forearm like a shield and a clipboard in the other hand; an icy blue glow from the laptop screen (element: cryomancy).

*Moves*: attack: slams the clipboard down; cast: the laptop screen flares and spreadsheets swirl out as frosty shards.

Draws: Regional Auditor (Spreadsheet Inquisitor, construct/cryomancy)

### `slacker_veteran`: Slacker Veteran ✅ delivered

> **Deliver:** `app/src/main/assets/game/sprites/sheets/slacker_veteran.png` + `slacker_veteran.json` · **enemy** · **64x64** cells · tags: idle, attack (impact=4), hurt (+ windup, cast (release=4), death)

A long-tenured associate who perfected doing nothing: faded vest worn open, beanie, a vape pen, slouched posture with hands in pockets, an energy drink can (Kyle's type; see portraits/kyle.jpg for the vibe, but a generic face).

*Moves*: attack: flicks the energy can; cast: exhales a vape cloud of excuses (shadow); idle: checks a phone, yawns.

Draws: Slacker Veteran (Excuse Specialist, flesh/shadow)

## Bosses (2 to draw, each its own sprite)

### `security_clint`: Loss Prevention Clint (96 px) ✅ delivered

> **Deliver:** `app/src/main/assets/game/sprites/sheets/security_clint.png` + `security_clint.json` · **boss** · **96x96** cells · tags: idle, windup, attack (impact=4), cast (release=4), hurt, charge_ultimate, ultimate (release=5) (+ death)

Clint from portraits/clint.jpg, drawn larger than life: stocky, grey security polo, cargo pants, aviator sunglasses, a heavy ring of keys and a radio on the belt, arms folded. The 'Invisible Eye' of loss prevention.

*Moves*: windup: lowers the sunglasses and stares; attack: grabs and slams like an interrogation hold; cast: a flashlight that flashes like a camera (shadow); charge_ultimate: spreads falsified timeline logs on an invisible table, the radio crackling; ultimate: the Reid technique, a cage of spotlights and accusing camera flashes closing in.

Element: construct/shadow.

### `manager_henderson`: Store Manager Henderson (96 px) ✅ delivered

> **Deliver:** `app/src/main/assets/game/sprites/sheets/manager_henderson.png` + `manager_henderson.json` · **boss** · **96x96** cells · tags: idle, windup, attack (impact=4), cast (release=4), hurt, charge_ultimate, ultimate (release=5) (+ death)

Henderson from portraits/henderson.jpg, drawn larger than life: silver-haired, polished smile, royal-blue manager polo with the store logo, khakis, a clipboard and a coffee mug. Every move is corporate theatre: a politician's grin that cracks into rage.

*Moves*: attack: Iron Slam (clipboard smash); cast: Steam Vent (coffee boils over into a scalding geyser, fire); charge_ultimate: locks the office door, phone to ear, red alarm light flashing; ultimate: an avalanche of write-up forms and 'TERMINATED' stamps raining down (no readable text needed). p2_ (optional): tie loosened, sleeves rolled, the smile gone.

Element: construct/fire.

## Notes for the game

- Job sprites: the hero's portrait changes with the chosen job (cashier, stocker, unloader, specialist). One Associate sheet covers them all today; per-job sheets (hero_cashier...) would need a small engine feature (a sprite per class) later.
- The hero character file is still named "Template Hero"; rename it when the sprites land.
