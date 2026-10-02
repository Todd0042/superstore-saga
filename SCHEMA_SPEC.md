# Retail Inferno — JSON Data Schema & Manifest Specification

> **Topic:** Drop-in JSON Architecture, Data Contracts, and Content-Neutral Engine Integration  
> **Target Directory:** `app/src/main/assets/game/`  
> **Governing Rule:** Rule 1 (Content Neutrality) — No retail-specific hardcoding in Kotlin code; all characters, dialogue, abilities, encounters, and incidents live strictly in JSON.

---

## 1. Directory Structure Overview

The entire retail simulation is powered by drop-in data assets organized within `app/src/main/assets/game/`:

```
app/src/main/assets/game/
├── manifest.json              # Boot metadata, title, starting scene, hero defaults, and TTS prompts
├── incidents/
│   └── incidents.json         # Dynamic Conflict Event Database (weights, cooldowns, prerequisites)
├── story/
│   ├── scenes.json            # 10 core retail store environments (music, backgrounds, entry nodes)
│   └── nodes.json             # Dialogue trees, choices, auto-play archetype tags, and battle triggers
├── characters/
│   ├── player_hero.json       # Player associate baseline identity
│   ├── narrator.json          # System narrator & PA announcer voice
│   ├── henderson.json         # Store Manager Henderson
│   ├── cheryl.json            # HR Manager Cheryl
│   ├── clint.json             # Security Manager Clint
│   ├── donna.json             # 15-Year Veteran Donna
│   ├── brenda.json            # Senior Cashier Brenda
│   ├── kyle.json              # Stocker Kyle
│   ├── derrick.json           # Forklift Operator Derrick
│   ├── chad.json              # Specialty Sales Associate Chad
│   ├── marsha.json            # Front End Manager Marsha
│   ├── todd.json              # Sales Floor Manager Todd
│   ├── mack.json              # Backroom Manager Mack
│   └── julian.json            # Specialty Manager Julian
├── classes/
│   ├── class_cashier.json     # Front End starting role & stats
│   ├── class_stocker.json     # Sales Floor starting role & stats
│   ├── class_unloader.json    # Backroom starting role & stats
│   └── class_specialist.json  # Specialty starting role & stats
├── spells/
│   ├── spells.json            # POS Register terminal abilities & action skills
│   └── thesaurus.json         # Thematic root lexicon for chant/timing calibration resonance
├── enemies/
│   ├── enemies.json           # Boss and hazard combat stats (Glitchers, Toxic Peers, Managers)
│   ├── enemy_spells.json      # Boss special attacks (Coupon Filibuster, Lane Closed, Vape Cloud)
│   └── enemy_sprites.json     # Visual sprite definitions and animations
├── encounters/
│   └── encounters.json        # End-of-Act boss encounters with multi-phase triggers
└── combat/
    ├── affinity_matrix.json   # Workstyle element interactions and damage multipliers
    └── status_catalog.json    # Status effects (Overworked, Paralyzed, Coffee High, Demoralized)
```

---

## 2. Game Manifest (`manifest.json`)

The manifest provides top-level configuration loaded at application launch:

```json
{
  "_doc": "Retail Inferno Game Manifest. Configures boot parameters, initial scene, and system strings.",
  "gameTitle": "Retail Inferno",
  "gameSubtitle": "A Satirical Workplace Survival RPG",
  "worldName": "MegaMart Store #1042",
  "initialSceneId": "scene_hiring_kiosk",
  "initialNodeId": "node_app_001",
  "defaultEncounterId": "encounter_coupon_glitcher",
  "startingAchievementId": "ONBOARDING_SURVIVOR",
  "victoryLine": "Punch confirmed: 07:00 AM. Status: Free at last.",
  "defeatLine": "Escorted off premises: Your badge has been deactivated by Loss Prevention.",
  "titleBackgroundAsset": "story/bg_title_storefront.jpg",
  "defaultHeroName": "Associate",
  "defaultHeroTitle": "Probationary Hire #1042-882",
  "quickNames": ["Taylor", "Jordan", "Morgan", "Alex", "Casey", "Riley"],
  "characterTitles": [
    "Probationary Hire",
    "Lane 4 Defender",
    "Pallet Jack Wrangler",
    "Baler Master",
    "The Quiet Quitter"
  ],
  "creationEmbarkPrompt": "SWIPE BADGE TO BEGIN SHIFT ➔",
  "creationVoiceKeywords": [
    "swipe", "clock in", "begin", "start", "punch in", "work",
    "start shift", "ready", "confirm", "enter"
  ],
  "defaultHeroPortrait": "portraits/associate_neutral.png",
  "tutorialCompletionText": "Application approved. Uniform issued. Locker assigned. Report to Action Alley for the morning cheer.",
  "voicePermissionTitle": "HANDS-FREE INVENTORY ASSISTANT",
  "voicePermissionSubtitle": "Optional Voice Commands & Narration",
  "voicePermissionPrompt": "Operate the POS register, respond to managers, and listen to store announcements hands-free."
}
```

---

## 3. Dynamic Incident Database (`incidents/incidents.json`)

The incident database drives Phase 4 of the daily shift loop via the **Dynamic Conflict Event Engine**:

```json
{
  "_doc": "Retail Inferno Dynamic Incident Catalog. Loaded by ConflictEventManager at runtime.",
  "incidents": [
    {
      "id": "event_connex_tryst",
      "title": "The Lawn & Garden Connex Trailer Tryst",
      "applicableTracks": ["all"],
      "eligibleActs": [2, 3],
      "maxOccurrences": 1,
      "cooldownShifts": 0,
      "baseWeight": 85,
      "prerequisites": {
        "minMorale": 0,
        "maxMorale": 100,
        "minEngagement": 0,
        "maxEngagement": 100,
        "requiredFlags": [],
        "forbiddenFlags": ["todd_fired", "taylor_transferred"]
      },
      "weightModifiers": [
        { "stat": "store_morale", "operator": "LESS_THAN", "threshold": 45, "multiplier": 1.8 },
        { "stat": "associate_morale", "operator": "LESS_THAN", "threshold": 35, "multiplier": 1.4 },
        { "stat": "labor_squeeze", "operator": "GREATER_THAN", "threshold": 60, "multiplier": 1.3 }
      ],
      "startNodeId": "node_incident_connex_001"
    },
    {
      "id": "event_microwave_fish",
      "title": "The Charred Fish Breakroom Biohazard",
      "applicableTracks": ["all"],
      "eligibleActs": [1, 2, 3],
      "maxOccurrences": 3,
      "cooldownShifts": 5,
      "baseWeight": 120,
      "prerequisites": {
        "minMorale": 10,
        "maxMorale": 80,
        "minEngagement": 0,
        "maxEngagement": 100,
        "requiredFlags": [],
        "forbiddenFlags": []
      },
      "weightModifiers": [
        { "stat": "store_morale", "operator": "LESS_THAN", "threshold": 50, "multiplier": 1.6 },
        { "stat": "customer_chaos", "operator": "GREATER_THAN", "threshold": 60, "multiplier": 1.4 }
      ],
      "startNodeId": "node_incident_fish_001"
    },
    {
      "id": "event_code_spark_mutiny",
      "title": "The Code Spark Register Mutiny",
      "applicableTracks": ["all"],
      "eligibleActs": [2, 3],
      "maxOccurrences": 2,
      "cooldownShifts": 6,
      "baseWeight": 95,
      "prerequisites": {
        "minMorale": 0,
        "maxMorale": 100,
        "minEngagement": 0,
        "maxEngagement": 100,
        "requiredFlags": [],
        "forbiddenFlags": []
      },
      "weightModifiers": [
        { "stat": "customer_chaos", "operator": "GREATER_THAN", "threshold": 65, "multiplier": 2.2 },
        { "stat": "labor_squeeze", "operator": "GREATER_THAN", "threshold": 70, "multiplier": 1.5 }
      ],
      "startNodeId": "node_incident_code_spark_entry"
    },
    {
      "id": "event_shelf_plugging",
      "title": "The Shelf Plugging Cat Food Discovery",
      "applicableTracks": ["sales_floor"],
      "eligibleActs": [2, 3],
      "maxOccurrences": 2,
      "cooldownShifts": 4,
      "baseWeight": 110,
      "prerequisites": {
        "minMorale": 0,
        "maxMorale": 90,
        "minEngagement": 0,
        "maxEngagement": 100,
        "requiredFlags": [],
        "forbiddenFlags": []
      },
      "weightModifiers": [
        { "stat": "labor_squeeze", "operator": "GREATER_THAN", "threshold": 60, "multiplier": 1.5 }
      ],
      "startNodeId": "node_incident_plugging_001"
    },
    {
      "id": "event_baler_wire_snap",
      "title": "The Hydraulic Baler Wire Snap Jam",
      "applicableTracks": ["backroom"],
      "eligibleActs": [1, 2, 3],
      "maxOccurrences": 2,
      "cooldownShifts": 5,
      "baseWeight": 105,
      "prerequisites": {
        "minMorale": 0,
        "maxMorale": 100,
        "minEngagement": 0,
        "maxEngagement": 100,
        "requiredFlags": [],
        "forbiddenFlags": []
      },
      "weightModifiers": [
        { "stat": "labor_squeeze", "operator": "GREATER_THAN", "threshold": 50, "multiplier": 1.4 }
      ],
      "startNodeId": "node_incident_baler_001"
    }
  ]
}
```

---

## 4. Scenes Specification (`story/scenes.json`)

Scenes declare the visual and acoustic background for each department environment:

```json
{
  "_doc": "Retail Inferno Store Scenes. Binds environment art, background audio, and entry nodes.",
  "scenes": [
    {
      "id": "scene_hiring_kiosk",
      "name": "The Hiring Kiosk",
      "chapterTitle": "Orientation: Day 0",
      "backgroundAsset": "story/bg_hiring_kiosk.jpg",
      "initialNodeId": "node_app_001",
      "ambientDescription": "A flickering corporate terminal with peeling motivational stickers.",
      "musicAsset": "audio/music/bgm_sterile_hum.ogg",
      "chapterIndex": 0,
      "actNumber": 1,
      "actTitle": "Act I: Fresh Blood",
      "milestoneTitle": "Orientation: The Application",
      "milestoneLocation": "Store Vestibule Kiosk",
      "milestoneSummary": "You completed the pre-employment assessment and received your badge assignment.",
      "milestoneIcon": "📋",
      "defaultObjective": "Complete the 10-question assessment honestly."
    },
    {
      "id": "scene_locker_room",
      "name": "The Associate Locker Room",
      "chapterTitle": "Day 1: Fitting the Vest",
      "backgroundAsset": "story/bg_locker_room.jpg",
      "initialNodeId": "node_locker_001",
      "ambientDescription": "Battleship-gray metal lockers, ozone smell, and fluorescent hum.",
      "musicAsset": "audio/music/bgm_breakroom_dull.ogg",
      "chapterIndex": 1,
      "actNumber": 1,
      "actTitle": "Act I: Fresh Blood",
      "milestoneTitle": "Day 1: Locker Room Initiation",
      "milestoneLocation": "Associate Locker Room",
      "milestoneSummary": "Donna assigned you Locker 42 and taught you the three golden rules of survival.",
      "milestoneIcon": "🗝️",
      "defaultObjective": "Speak with Donna and memorize your locker code."
    },
    {
      "id": "scene_action_alley",
      "name": "Center Action Alley",
      "chapterTitle": "Shift 3: The Milk Crate Pulpit",
      "backgroundAsset": "story/bg_action_alley.jpg",
      "initialNodeId": "node_huddle_001",
      "ambientDescription": "Wide center aisle between lawn furniture and towers of paper towels.",
      "musicAsset": "audio/music/bgm_corporate_cheer.ogg",
      "chapterIndex": 2,
      "actNumber": 1,
      "actTitle": "Act I: Fresh Blood",
      "milestoneTitle": "Shift 3: The Morning Huddle",
      "milestoneLocation": "Action Alley Pallet Stack",
      "milestoneSummary": "Store Manager Henderson led the mandatory storewide morning cheer.",
      "milestoneIcon": "📣",
      "defaultObjective": "Participate in the morning cheer without losing your composure."
    },
    {
      "id": "scene_breakroom",
      "name": "The Employee Breakroom",
      "chapterTitle": "Act II: The Whisper Mill",
      "backgroundAsset": "story/bg_breakroom.jpg",
      "initialNodeId": "node_breakroom_entry",
      "ambientDescription": "Wobbly folding chairs, humming vending machines, and dirty microwave odors.",
      "musicAsset": "audio/music/bgm_elevator_jazz.ogg",
      "chapterIndex": 3,
      "actNumber": 2,
      "actTitle": "Act II: Breakroom Politics",
      "milestoneTitle": "Act II: The Whisper Mill",
      "milestoneLocation": "Employee Breakroom",
      "milestoneSummary": "You navigated dirty fridge purges and peer gossip circles.",
      "milestoneIcon": "☕",
      "defaultObjective": "Survive lunch without getting drawn into coworker feuds."
    },
    {
      "id": "scene_security_office",
      "name": "The Loss Prevention Fishbowl",
      "chapterTitle": "Act IV: The Closed-Door Audit",
      "backgroundAsset": "story/bg_security_office.jpg",
      "initialNodeId": "node_interrogation_entry",
      "ambientDescription": "Windowless cinderblock interrogation room under harsh halogen spotlight.",
      "musicAsset": "audio/music/bgm_security_tension.ogg",
      "chapterIndex": 4,
      "actNumber": 4,
      "actTitle": "Act IV: The Frame Job",
      "milestoneTitle": "Act IV: The Interrogation",
      "milestoneLocation": "Loss Prevention Security Suite",
      "milestoneSummary": "Clint and Cheryl cross-examined you over missing high-ticket merchandise.",
      "milestoneIcon": "📹",
      "defaultObjective": "Defend your record against manufactured shrinkage evidence."
    },
    {
      "id": "scene_executive_mezzanine",
      "name": "The Store Executive Penthouse",
      "chapterTitle": "Act V: The Final Clock-Out",
      "backgroundAsset": "story/bg_executive_suite.jpg",
      "initialNodeId": "node_mezzanine_entry",
      "ambientDescription": "Glass-walled office overlooking the burning sales floor, carpeted in deep burgundy.",
      "musicAsset": "audio/music/bgm_final_boss_inferno.ogg",
      "chapterIndex": 5,
      "actNumber": 5,
      "actTitle": "Act V: The Inferno",
      "milestoneTitle": "Act V: The Executive Reckoning",
      "milestoneLocation": "Executive Mezzanine",
      "milestoneSummary": "You confronted Store Manager Henderson and transmitted the fraud logs to the Department of Labor.",
      "milestoneIcon": "🔥",
      "defaultObjective": "Defeat Store Manager Henderson and swipe your badge to clock out forever."
    }
  ]
}
```

---

## 5. Story Nodes Specification (`story/nodes.json`)

Nodes include choice archetype tags for Auto-Play heuristics and stat effect deltas:

```json
{
  "_doc": "Retail Inferno Story Graph Nodes. Contains narrative branching, choices, and stat deltas.",
  "nodes": [
    {
      "id": "node_app_001",
      "speakerId": "cheryl",
      "side": "RIGHT",
      "text": "Welcome to the MegaMart family portal, prospective associate! Before we process your badge for Complex #1042, our automated integrity algorithm needs to understand your workplace instincts. Ten quick questions. No wrong answers! Well... technically there are, but let's have fun with it!",
      "choices": [
        {
          "id": "choice_app_start",
          "text": "Begin the pre-employment assessment.",
          "voiceKeywords": ["begin", "start", "ready", "assessment", "let's go"],
          "autoPlayArchetype": "#ENTHUSIASTIC",
          "autoPlayWeight": 100,
          "autoPlayForbidden": false,
          "nextNodeId": "node_app_q1",
          "statEffects": {
            "engagementDelta": 5
          }
        }
      ]
    },
    {
      "id": "node_app_q1",
      "speakerId": "cheryl",
      "side": "RIGHT",
      "text": "Question 1: You observe a coworker pocketing a $1.29 pack of strawberry chewing gum from an open box on the loading dock. How do you respond?",
      "choices": [
        {
          "id": "choice_q1_snitch",
          "text": "Immediately notify Security Manager Clint and submit an anonymous loss prevention report.",
          "voiceKeywords": ["report", "security", "clint", "loss prevention", "snitch"],
          "autoPlayArchetype": "#SYCOPHANT",
          "autoPlayWeight": 90,
          "autoPlayForbidden": false,
          "nextNodeId": "node_app_q2",
          "setFlags": ["app_integrity_snitch"],
          "statEffects": {
            "assKissingDelta": 10,
            "moraleDelta": -5
          }
        },
        {
          "id": "choice_q1_ignore",
          "text": "Mind your own business. You didn't see anything; it's a dollar-thirty.",
          "voiceKeywords": ["ignore", "nothing", "business", "forget it"],
          "autoPlayArchetype": "#SLACKER",
          "autoPlayWeight": 85,
          "autoPlayForbidden": false,
          "nextNodeId": "node_app_q2",
          "setFlags": ["app_integrity_slacker"],
          "statEffects": {
            "moraleDelta": 5
          }
        },
        {
          "id": "choice_q1_share",
          "text": "Ask the coworker if you can have a piece too.",
          "voiceKeywords": ["share", "piece", "chew", "gum", "join"],
          "autoPlayArchetype": "#DEFIANT",
          "autoPlayWeight": 70,
          "autoPlayForbidden": false,
          "nextNodeId": "node_app_q2",
          "setFlags": ["app_integrity_rebel"],
          "statEffects": {
            "moraleDelta": 10,
            "assKissingDelta": -10
          }
        }
      ]
    }
  ]
}
```

---

## 6. Associate Classes (`classes/*.json`)

Four distinct starting classes define starting composure (HP), focus (MP), velocity, and initial action kits:

### `classes/class_cashier.json` (Front End)
```json
{
  "id": "class_cashier",
  "name": "Register Cashier",
  "department": "Front End",
  "description": "High-velocity front-line scanner. Fast turn recovery, crowd de-escalation, and barcode burst strikes.",
  "baseStats": {
    "maxHp": 450,
    "maxMp": 120,
    "attack": 38,
    "defense": 22,
    "speed": 65
  },
  "startingSpellIds": [
    "spell_barcode_whip",
    "spell_price_check",
    "spell_coupon_reject",
    "spell_drawer_slam"
  ],
  "startingItems": ["roll_register_tape", "plastic_name_badge"]
}
```

### `classes/class_stocker.json` (Sales Floor)
```json
{
  "id": "class_stocker",
  "name": "Merchandise Stocker",
  "department": "Sales Floor",
  "description": "Vanguard zone protector. Excels at cardboard barricades, heavy cart charges, and shelf-facing endurance.",
  "baseStats": {
    "maxHp": 580,
    "maxMp": 90,
    "attack": 48,
    "defense": 32,
    "speed": 45
  },
  "startingSpellIds": [
    "spell_box_cutter_slash",
    "spell_cart_barricade",
    "spell_shelf_plug_shove",
    "spell_flatbed_ram"
  ],
  "startingItems": ["utility_blade_dull", "high_vis_vest"]
}
```

### `classes/class_unloader.json` (Backroom)
```json
{
  "id": "class_unloader",
  "name": "Freight Unloader",
  "department": "Backroom",
  "description": "Heavy industrial juggernaut. Masters hydraulic machinery, pallet compression, and physical labor endurance.",
  "baseStats": {
    "maxHp": 700,
    "maxMp": 75,
    "attack": 55,
    "defense": 40,
    "speed": 35
  },
  "startingSpellIds": [
    "spell_pallet_crush",
    "spell_shrink_wrap_cocoon",
    "spell_baler_wire_whip",
    "spell_compactor_press"
  ],
  "startingItems": ["steel_toe_boots", "back_brace"]
}
```

### `classes/class_specialist.json` (Specialty)
```json
{
  "id": "class_specialist",
  "name": "Counter Sales Specialist",
  "department": "Specialty (Tech / Deli)",
  "description": "High-margin tactical saboteur. Uses extended warranty jargon, showcase spotlights, and credit traps.",
  "baseStats": {
    "maxHp": 420,
    "maxMp": 140,
    "attack": 42,
    "defense": 24,
    "speed": 55
  },
  "startingSpellIds": [
    "spell_warranty_pitch",
    "spell_showcase_glare",
    "spell_meat_slicer_sweep",
    "spell_credit_trap"
  ],
  "startingItems": ["case_key_ring", "specialist_black_polo"]
}
```

---

## 7. POS Actions & Resonance Thesaurus (`spells/spells.json` & `thesaurus.json`)

To comply with **Engine Rule 4 (Resonance Calibration Invariant)**, every retail ability is mapped to an engine spell school and covered by **exactly one root word** in `thesaurus.json`:

### `spells/spells.json`
```json
{
  "_doc": "Retail Inferno POS Register Keypad Action Library.",
  "spells": [
    {
      "id": "spell_barcode_whip",
      "name": "Barcode Laser Whip",
      "school": "PHYSICAL",
      "basePower": 40,
      "mpCost": 0,
      "description": "Rapid-fire barcode scanner swipe across enemy composure.",
      "exampleChant": "Scan barcode strike!",
      "aliases": ["scan", "laser", "whip", "swipe", "item scan"]
    },
    {
      "id": "spell_price_check",
      "name": "Price Check Intercom",
      "school": "HOLY",
      "basePower": 0,
      "mpCost": 20,
      "isGuard": true,
      "description": "Pervasive storewide page that stuns opponent focus.",
      "exampleChant": "Page price check chime!",
      "aliases": ["page", "price check", "intercom", "chime", "override"]
    },
    {
      "id": "spell_coupon_reject",
      "name": "Expired Coupon Shield",
      "school": "PHYSICAL",
      "basePower": 0,
      "mpCost": 15,
      "isGuard": true,
      "description": "Parries incoming hostility by citing the expiration date in fine print.",
      "exampleChant": "Reject coupon shield!",
      "aliases": ["reject", "void", "coupon", "policy", "parry"]
    },
    {
      "id": "spell_drawer_slam",
      "name": "Cash Drawer Slam",
      "school": "PHYSICAL",
      "basePower": 95,
      "mpCost": 45,
      "description": "Violently shoves the spring-loaded steel cash drawer shut with a deafening clang.",
      "exampleChant": "Slam cash drawer smash!",
      "aliases": ["slam", "drawer", "cash", "crush", "total"]
    },
    {
      "id": "spell_warranty_pitch",
      "name": "Extended Warranty Pitch",
      "school": "SHADOW",
      "basePower": 35,
      "mpCost": 20,
      "description": "Bores the target with dense 3-year accidental damage clauses, draining focus.",
      "exampleChant": "Pitch warranty terms!",
      "aliases": ["warranty", "upsell", "pitch", "protection", "terms"]
    },
    {
      "id": "spell_union_break",
      "name": "Fifteen Minute Union Break",
      "school": "HOLY",
      "basePower": 60,
      "mpCost": 25,
      "isHeal": true,
      "description": "Sits on a folding chair with lukewarm coffee, restoring composure.",
      "exampleChant": "Take union break!",
      "aliases": ["break", "rest", "coffee", "breathe", "sit"]
    }
  ]
}
```

---

## 8. Boss Encounters (`encounters/encounters.json`)

Encounters specify boss phase transitions, enrage announcements, and retail hazard summons:

```json
{
  "_doc": "Retail Inferno Boss Encounters with Phased Thresholds and Environmental Retail Hazards.",
  "encounters": [
    {
      "id": "encounter_coupon_glitcher",
      "name": "Act I Boss: The Extreme Coupon Glitcher",
      "description": "A coupon extremist brandishing 74 expired printout coupons and screaming for corporate.",
      "environment": "SUPERMARKET_FRONT",
      "party": ["player_hero"],
      "enemies": [
        {
          "templateId": "enemy_coupon_glitcher",
          "id": "boss_glitcher",
          "name": "Coupon Glitcher",
          "subtitle": "Binder of Doom",
          "hp": 1800,
          "attack": 32,
          "defense": 20,
          "speed": 45,
          "isTargeted": true,
          "atbGauge": 0.3
        }
      ],
      "bossPhases": [
        {
          "enemyId": "boss_glitcher",
          "hpBelow": 0.60,
          "bannerText": "FINE PRINT FILIBUSTER ACTIVATED",
          "narrationText": "The Glitcher spreads dozens of blurry mobile barcodes across the conveyor belt!",
          "reducePartyToHpPct": 0.8
        },
        {
          "enemyId": "boss_glitcher",
          "hpBelow": 0.25,
          "bannerText": "CUSTOMER MELTDOWN: DEMANDING STORE MANAGER",
          "narrationText": "The Glitcher screams: 'I HAVE SHOPPED HERE FOR TWENTY YEARS!'",
          "reducePartyToHpPct": 0.4
        }
      ]
    },
    {
      "id": "encounter_boss_brenda",
      "name": "Act II Boss: Senior Cashier Brenda",
      "description": "A 20-year veteran cashier who has mastered the art of weaponized lane lights and shift-dumping.",
      "environment": "REGISTER_CONCOURSE",
      "party": ["player_hero"],
      "enemies": [
        {
          "templateId": "enemy_senior_cashier_brenda",
          "id": "boss_brenda",
          "name": "Senior Cashier Brenda",
          "subtitle": "The Lane Closer",
          "hp": 4200,
          "attack": 46,
          "defense": 38,
          "speed": 50,
          "isTargeted": true,
          "atbGauge": 0.4
        }
      ],
      "bossPhases": [
        {
          "enemyId": "boss_brenda",
          "hpBelow": 0.65,
          "bannerText": "BRENDA FLIPS LANE LIGHT TO CLOSED",
          "narrationText": "Brenda flips her lane light to Closed and dumps two return carts at your register!",
          "summonEnemyId": "enemy_go_back_cart",
          "summonCount": 1
        },
        {
          "enemyId": "boss_brenda",
          "hpBelow": 0.30,
          "bannerText": "THE COUGH DROP DISAPPEARANCE",
          "narrationText": "Brenda claims she needs a quick lozenge and abandons her register entirely!",
          "reducePartyToHpPct": 0.5
        }
      ]
    }
  ]
}
```

---

## 9. Character Profiles & Preferred TTS Voices (`characters/*.json`)

To satisfy **Engine Rule 6 (Distinct Physical Voices)**, every character declares their preferred Android TTS physical voice ID with pitch/rate fallbacks:

```json
{
  "id": "cheryl",
  "name": "Cheryl",
  "role": "Human Resources Manager",
  "title": "The Velvet Guillotine",
  "portraitAsset": "portraits/cheryl.png",
  "preferredVoiceId": "en-us-x-sfg#female_cheerful",
  "speechPitch": 1.25,
  "speechRate": 1.05,
  "description": "Pastel floral cardigans, perky smile, and a glass bowl of rock-hard peppermints. Executes terminations with maternal cheer."
}
```

```json
{
  "id": "henderson",
  "name": "Henderson",
  "role": "Store Manager",
  "title": "The Polished Politician",
  "portraitAsset": "portraits/henderson.png",
  "preferredVoiceId": "en-us-x-iol#male_booming",
  "speechPitch": 0.95,
  "speechRate": 1.15,
  "description": "Crisp corporate polo, razor-creased khakis, orthopedic sneakers, and a blinking Bluetooth headset. Walks at power-walking speed."
}
```

```json
{
  "id": "clint",
  "name": "Clint",
  "role": "Security Manager",
  "title": "The Invisible Eye",
  "portraitAsset": "portraits/clint.png",
  "preferredVoiceId": "en-us-x-tpd#male_gravel",
  "speechPitch": 0.80,
  "speechRate": 0.90,
  "description": "Mirrored sunglasses on collar, battered baseball cap, low gravelly monotone. Convinced every employee is an undercover thief."
}
```

```json
{
  "id": "donna",
  "name": "Donna",
  "role": "15-Year Associate",
  "title": "The Breakroom Anchor",
  "portraitAsset": "portraits/donna.png",
  "preferredVoiceId": "en-us-x-sfb#female_raspy",
  "speechPitch": 0.85,
  "speechRate": 0.95,
  "description": "Company vest weighed down with 30+ pins. Solves newspaper crosswords with a stubby pencil and speaks pure unfiltered retail truth."
}
```

---

## 10. Verification Contract (Engine Invariant Compatibility)

When this JSON suite replaces the starter template files under `app/src/main/assets/game/`, the unit test suite (`./gradlew testDebugUnitTest`) must pass cleanly:
1. **Schema & Link Verification:** Every `nextNodeId` resolves; every choice contains a `voiceKeyword`; every battle trigger resolves to an entry in `encounters.json`.
2. **Resonance Invariant:** Every ability name in `spells.json` maps to **exactly one root word** in `thesaurus.json` across all schools, ensuring `testAllSpellChantPresetsAcrossAllResonanceTiers` remains green.
3. **Voice Invariant:** Only characters with non-null `preferredVoiceId` are registered in `GameContent.preferredVoiceIds`.
4. **Terminal Node:** The only terminal node in the story graph is the Act V resolution node that sets `game_completed`.
