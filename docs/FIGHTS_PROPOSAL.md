# Superstore Saga: More Fights (proposal)

Status: proposal for Todd's review, 2026-11-27. Nothing here is built. Every story line below is a **DRAFT**.
Source: `voice-rpg-engine/docs/COMBAT_PROGRESSION_PLAN.md` (decision 13, section 4 "superstore-saga"): about
12-14 fights per department across its ~56 shifts, reusing the 8 enemies and recolours where they fit, new enemies
briefed for sprites. Longer stories come later; this pass only adds fights.

Result: **14 fights per department** (7 existing + 7 new), **13 new encounters**, **4 new enemies that need art**,
5 new enemy templates that reuse staff sheets already delivered, 0 required recolours (1 optional).

---

## 1. How the story runs today

### 1.1 Shape (same for all four departments)

| Act | Shifts | Theme | Scenes used |
|---|---|---|---|
| Pre-hire | - | Online assessment (`node_app_*`), interview with Cheryl (`node_interview_positions` picks the class) | online app, personnel office |
| Shared intro | - | `node_locker_<dept>_enter` sets `role_<dept>`; locker hub; Henderson's huddle; `node_huddle_breakout` -> `node_station_<dept>` -> `node_hub_<dept>` -> `node_shift1_start` | locker room, action alley |
| I: Fresh Blood | 1-10 | Honeymoon: training, cheers, festival rush, Rookie of the Month | department scene |
| II: Breakroom Politics | 11-22 | The department slacker dumps work, hoards gear, vanishes | department scene, breakroom |
| III: Corporate Gaze | 23-34 | Algorithmic quotas, audits, timecard shaving | department scene, personnel office |
| IV: Frame Job | 35-46 | Shrink scapegoat hunt, planted evidence, Clint | department scene, security office |
| V: Crucible | 47-56 | Lockdown, bribe, solidarity, Henderson, endings | department scene, manager office, breakroom |

Routing: `node_clockout_fingerprint` (end of shift 1) and `node_shift11/23/35/47_branch` use `flagRoutes` on
`role_*`. Each act opens with a shared node (`node_shift11/23/35/47_start`), a department opener
(`node_shiftN_<dept>`) and a department hub (`node_hub_<dept>_actN`, one advice option with a `completionFlag`).
Shift 55 (`node_shift55_start`) and shift 56 (`node_shift56_reckoning`, six endings) are shared again.

Department nodes in Acts II-V are a straight chain, one node per shift, each with one choice (some with three
choices that all lead to the same next shift: 13, 24, 37, 41, 47, 49, 50, 52, 54 depending on department).

### 1.2 Path quirks found

- **Shift 1 is shared:** every department's hub leads to `node_shift1_start` (Marsha, checkout concourse,
  `encounter_register_rush`), so unloaders and stockers fight their first fight at the registers.
- **Stocker Act I has no stocker nodes:** `node_shift2_stocker` leads into the generic chain
  `node_shift2_sync/kyle -> node_shift2_end -> node_shift3_start ... node_shift10_start` (mixed-department content:
  PLU codes with Marsha, baler with Mack). The other three have `node_shiftN_<dept>` chains.
- **Each department's antagonists differ:** Act II slacker = Derrick (unloader), Kyle (stocker), Brenda (cashier),
  Chad (specialist); Act IV/V culprit = Henderson and Clint (unloader), Todd (stocker), Marsha (cashier), Chad
  (specialist). `encounter_slacker_showdown` calls all four "Slacker Veteran" on the generic slacker sheet.
- `scenes.json` `actNumber` is 1 for every department scene (4 for security office, 5 for manager office), so
  the act is only knowable from the shift number (relevant to the warp work, plan E8).

### 1.3 The 7 existing fights (identical in all four departments)

| # | Shift | Act | Node (per department) | Encounter | Kind |
|---|---|---|---|---|---|
| 1 | 1 | I | `node_shift1_start` (shared) | `encounter_register_rush` (karen_customer, extreme_couponer) | regular |
| 2 | 7 | I | `node_shift7_start` (stocker) / `node_shift7_<dept>` | `encounter_saturday_rush_wave` (overtime_screamer, karen_customer) | regular |
| 3 | 10 | I | `node_shift10_start` (stocker) / `node_shift10_<dept>` | `encounter_regional_showcase` (regional_auditor "VP Vance") | boss |
| 4 | 22 | II | `node_shift22_<dept>` | `encounter_slacker_showdown` (slacker_veteran) | boss (Act II climax) |
| 5 | 34 | III | `node_shift34_<dept>` | `encounter_auditor_standoff` (regional_auditor "Auditor Vance") | boss (Act III climax) |
| 6 | 46 | IV | `node_shift46_<dept>` | `encounter_loss_prevention_standoff` (security_clint) | boss (Act IV climax) |
| 7 | 55 | V | `node_shift55_start` (shared) | `encounter_henderson_showdown` (manager_henderson) | finale |

Acts II-IV each have one fight in 12 shifts, at the very end. That is where the new fights go.

---

## 2. How a new fight is added (engine constraint)

`StoryViewModel` starts the battle as soon as a node with `triggerBattleEncounterId` is reached; the node's
choices are never shown, and victory continues to the node's `nextNodeId`. So a fight cannot sit on an existing
node that has a choice without losing the choice (and its `metricDeltas`). Every new fight is therefore:

1. one **new node** `node_shiftN_<dept>_fight` (speaker, one lead-in line, `triggerBattleEncounterId`,
   `nextNodeId` = the beat's current next node);
2. the existing beat's single choice repointed to it (its text and `metricDeltas` unchanged).

The flow becomes: beat text -> the player's choice -> lead-in line -> battle -> the next shift's opener (which is the
story between fights). All 28 beats chosen below are single-choice nodes (checked); none is a hub, so the hub
"completed option" logic is untouched. Three-choice beats (e.g. shift 49's bribes, where one choice is "accept the
bribe") were deliberately avoided. Random incidents (`incidents.json`) are not used: they are weighted and
one-time, so a fight there would not be on every playthrough.

Minimum new text: **28 lead-in lines** (7 per department), plus translations in
`game/translations/strings_<lang>.json` if the game ships them for story text. An optional one-line victory node is
suggested only for the two Act V fights.

---

## 3. Difficulty curve (same slots in every department)

| Rank | Shift | Act | Slot | Tier |
|---|---|---|---|---|
| 1 | 1 | I | Register Rush (existing) | early |
| 2 | 7 | I | Saturday Rush (existing) | early |
| 3 | 10 | I | VP Vance showcase (existing boss) | early (tune below the Act II boss: it is a celebration, and it reuses the Act III boss template) |
| 4 | 11-12 | II | New: department trouble | early-mid |
| 5 | 15 / 20 | II | New: the night dump (Go-Back Golem) / stocker's Derrick | early-mid |
| 6 | 22 | II | Slacker showdown (existing boss) | mid |
| 7 | 26 | III | New: Code Spark | mid |
| 8 | 29 | III | New: sensor meltdown | mid |
| 9 | 34 | III | Auditor standoff (existing boss) | mid-late |
| 10 | 36 | IV | New: Asset Protection shakedown | late |
| 11 | 46 | IV | Clint (existing boss) | late |
| 12 | 48 | V | New: the department culprit catches you (elite) | late |
| 13 | 53 | V | New: Henderson's last line | late (hardest regular fight) |
| 14 | 55 | V | Henderson (existing finale) | finale |

No two fights are adjacent: the closest pairs (10/11-12, 20/22, 34/36, 46/48, 53/55) always have at least one shift
node (and usually an act-complete card and hub) between them. Bosses stay the act climaxes; the new fights are all
regular, with the shift-48 culprit as an elite regular (not a boss, so it doesn't need a boss sheet).

---

## 4. Per-department fights

Legend: **E** = existing encounter, **N** = new encounter. Enemies: existing template, *recolour*, `rival_*`
(new template on a staff sheet already delivered, no art), or **NEW** (needs a sprite sheet, section 6).

### 4.1 Unloader (Mack, Derrick; Receiving Bay 2)

| # | Shift / Act | Triggers after | Story reason | Enemies | Kind | Tier | Encounter |
|---|---|---|---|---|---|---|---|
| 1 | 1 / I | `node_shift1_start` | Morning rush (shared) | karen_customer, extreme_couponer | regular | early | E `encounter_register_rush` |
| 2 | 7 / I | `node_shift7_unloader` | Festival supply line | overtime_screamer, karen_customer | regular | early | E `encounter_saturday_rush_wave` |
| 3 | 10 / I | `node_shift10_unloader` | VP Vance walks the dock | regional_auditor | boss | early | E `encounter_regional_showcase` |
| 4 | 12 / II | `node_shift12_unloader` | Derrick padlocked Old Reliable; you're stuck with "The Screamer" | **NEW** runaway_pallet_jack | regular | early-mid | N `encounter_screamer_jack` |
| 5 | 15 / II | `node_shift15_unloader` | Floor dumps broken pallets and cardboard in Bay 2 at 10:50 PM | **NEW** go_back_golem | regular | early-mid | N `encounter_go_back_golem` |
| 6 | 22 / II | `node_shift22_unloader` | Derrick blames you | slacker_veteran | boss | mid | E `encounter_slacker_showdown` |
| 7 | 26 / III | `node_shift26_unloader` | Code Spark drags the dock to the registers | overtime_screamer x2, karen_customer | regular | mid | N `encounter_code_spark` |
| 8 | 29 / III | `node_shift29_unloader` | The conveyor's optical sensor goes haywire | **NEW** shrink_sentinel x2 | regular | mid | N `encounter_sensor_meltdown` |
| 9 | 34 / III | `node_shift34_unloader` | Auditor Vance, demurrage blame | regional_auditor | boss | mid-late | E `encounter_auditor_standoff` |
| 10 | 36 / IV | `node_shift36_unloader` | Clint's exit-bag shakedown (the text already has two guards) | **NEW** asset_protection_officer x2 | regular | late | N `encounter_ap_shakedown` |
| 11 | 46 / IV | `node_shift46_unloader` | Clint's interrogation | security_clint | boss | late | E `encounter_loss_prevention_standoff` |
| 12 | 48 / V | `node_shift48_unloader` | You find Henderson's signed bills of lading; Derrick comes for the file | `rival_derrick` (elite), **NEW** asset_protection_officer | regular (elite) | late | N `encounter_lockdown_dock` |
| 13 | 53 / V | `node_shift53_unloader` | The backroom march meets Henderson's last line | `rival_cheryl`, **NEW** asset_protection_officer x2 | regular | late | N `encounter_last_line` |
| 14 | 55 / V | `node_shift55_start` | Henderson | manager_henderson | finale | finale | E `encounter_henderson_showdown` |

Draft lead-ins (DRAFT, for review):
- 12 (Mack): "THE SCREAMER BUCKS! The jack shrieks like a banshee, slews under 2,400 pounds of water and swings its handle at your shins. Tame it before it takes out the dock plate!"
- 15 (narrator): "10:51 PM. The pile of broken pallets and ripped cardboard groans, shifts... and stands up. Every go-back the floor dumped on the dock tonight has become one lumbering heap!"
- 26 (Marsha): "CODE SPARK! Forty carts, four open lanes and a crowd that smells weakness. Dock crew, you're bagging. Hold the line!"
- 29 (Derrick): "Derrick's boot sends the sensor box swinging. The dome above the conveyor drops on its cable, strobe screaming 'IDLE INFRACTION', and its twin wakes up beside it!"
- 36 (Clint): "Clint nods once. The two guards reach for your toolbag before the bay camera can see what's in it. Don't let go."
- 48 (Derrick, optional victory line by Mack): "A flashlight clicks on in the dark office. Derrick, keys revoked since Shift 22, steps out with a lockdown guard: 'Henderson says I get my forklift card back if that file walks out with me.'"
- 53 (Mack): "At the end of Action Alley, Cheryl steps out with two lockdown guards and a stack of termination forms: 'Anyone past this line is resigning without notice.' Mack doesn't slow down."

### 4.2 Stocker (Todd, Kyle, Donna; Action Alley)

| # | Shift / Act | Triggers after | Story reason | Enemies | Kind | Tier | Encounter |
|---|---|---|---|---|---|---|---|
| 1 | 1 / I | `node_shift1_start` | Morning rush (shared) | karen_customer, extreme_couponer | regular | early | E `encounter_register_rush` |
| 2 | 7 / I | `node_shift7_start` | Saturday festival rush | overtime_screamer, karen_customer | regular | early | E `encounter_saturday_rush_wave` |
| 3 | 10 / I | `node_shift10_start` | VP Vance's showcase | regional_auditor | boss | early | E `encounter_regional_showcase` |
| 4 | 15 / II | `node_shift15_stocker` | Cashiers dump melting ice cream and return junk in your aisle | **NEW** go_back_golem | regular | early-mid | N `encounter_go_back_golem` |
| 5 | 20 / II | `node_shift20_stocker` | You warn unlicensed forklift-cowboy Derrick; he squares up | `rival_derrick` | regular | early-mid | N `encounter_forklift_cowboy` |
| 6 | 22 / II | `node_shift22_stocker` | Kyle blames you | slacker_veteran | boss | mid | E `encounter_slacker_showdown` |
| 7 | 26 / III | `node_shift26_stocker` | Code Spark: stockers to the registers | overtime_screamer x2, karen_customer | regular | mid | N `encounter_code_spark` |
| 8 | 29 / III | `node_shift29_stocker` | Smart-shelf brackets shriek in three aisles | **NEW** shrink_sentinel x2 | regular | mid | N `encounter_sensor_meltdown` |
| 9 | 34 / III | `node_shift34_stocker` | Auditor Vance, scan-score blame | regional_auditor | boss | mid-late | E `encounter_auditor_standoff` |
| 10 | 36 / IV | `node_shift36_stocker` | Locker room raid with bolt cutters | **NEW** asset_protection_officer x2 | regular | late | N `encounter_ap_shakedown` |
| 11 | 46 / IV | `node_shift46_stocker` | Clint's interrogation | security_clint | boss | late | E `encounter_loss_prevention_standoff` |
| 12 | 48 / V | `node_shift48_stocker` | Todd catches you piecing his shredded claims slips (his bribe at 49 follows the defeat) | `rival_todd` (elite), **NEW** asset_protection_officer | regular (elite) | late | N `encounter_lockdown_aisle4` |
| 13 | 53 / V | `node_shift53_stocker` | Sirens outside; Henderson's last line blocks the aisle | `rival_cheryl`, **NEW** asset_protection_officer x2 | regular | late | N `encounter_last_line` |
| 14 | 55 / V | `node_shift55_start` | Henderson | manager_henderson | finale | finale | E `encounter_henderson_showdown` |

Draft lead-ins (DRAFT):
- 15 (narrator): "10:50 PM. The return carts lurch. Melting ice cream, crushed cereal and a dozen go-backs slump together into one dripping heap, and it lurches toward your clean aisle!"
- 20 (Derrick): "Derrick kills the forklift and hops down, jabbing a finger: 'Nobody rats on me, rookie. Not you, not Donna.' Make him hear it."
- 26 (Marsha): "CODE SPARK! Lane 6 is yours, and the line is already past the gumball machines. Hold the line!"
- 29 (narrator): "Every bracket in baby formula strobes at once. Two ceiling domes drop on their cables, lenses locked on you: 'SHRINK EVENT DETECTED.'"
- 36 (Clint): "Clint snaps his fingers. Two Asset Protection officers step between you and your locker, bolt cutters out. Hold your ground."
- 48 (Todd, optional victory line by Donna): "A flashlight beam hits the torn slips in your hands. Todd, with a lockdown guard at his shoulder: 'Those are corporate property, kid. Hand them over.'"
- 53 (Donna): "Between you and the manager's suite: Cheryl, two lockdown guards, and a clipboard of termination forms. Donna grips her flashlight: 'Together, rookie.'"

### 4.3 Cashier (Marsha, Brenda; Checkout Concourse, Lane 4)

| # | Shift / Act | Triggers after | Story reason | Enemies | Kind | Tier | Encounter |
|---|---|---|---|---|---|---|---|
| 1 | 1 / I | `node_shift1_start` | Morning rush (home turf) | karen_customer, extreme_couponer | regular | early | E `encounter_register_rush` |
| 2 | 7 / I | `node_shift7_cashier` | Festival rush, Lane 4 anchors | overtime_screamer, karen_customer | regular | early | E `encounter_saturday_rush_wave` |
| 3 | 10 / I | `node_shift10_cashier` | VP Vance times the front end | regional_auditor | boss | early | E `encounter_regional_showcase` |
| 4 | 11 / II | `node_shift11_cashier` (before the Act II hub) | "The coupon crowd is already at the glass doors" on a clopening | extreme_couponer x2, karen_customer | regular | early-mid | N `encounter_clopening_coupons` |
| 5 | 15 / II | `node_shift15_cashier` | Floor dumps three carts of return junk in your lane | **NEW** go_back_golem | regular | early-mid | N `encounter_go_back_golem` |
| 6 | 22 / II | `node_shift22_cashier` | Brenda blames you | slacker_veteran | boss | mid | E `encounter_slacker_showdown` |
| 7 | 26 / III | `node_shift26_cashier` | Code Spark, 100 holiday shoppers, Brenda's register jams | overtime_screamer x2, karen_customer | regular | mid | N `encounter_code_spark` |
| 8 | 29 / III | `node_shift29_cashier` | False sensor alerts blare every 90 seconds | **NEW** shrink_sentinel x2 | regular | mid | N `encounter_sensor_meltdown` |
| 9 | 34 / III | `node_shift34_cashier` | Auditor Vance, queue blame | regional_auditor | boss | mid-late | E `encounter_auditor_standoff` |
| 10 | 36 / IV | `node_shift36_cashier` | Clint's surprise till audit, "you're short $120" | **NEW** asset_protection_officer x2 | regular | late | N `encounter_ap_shakedown` |
| 11 | 46 / IV | `node_shift46_cashier` | Clint's interrogation | security_clint | boss | late | E `encounter_loss_prevention_standoff` |
| 12 | 48 / V | `node_shift48_cashier` | Marsha catches you with her altered drop slips (her promotion pledge at 49 follows) | `rival_marsha` (elite), **NEW** asset_protection_officer | regular (elite) | late | N `encounter_lockdown_cash_office` |
| 13 | 53 / V | `node_shift53_cashier` | Blue lights outside; Henderson's last line | `rival_cheryl`, **NEW** asset_protection_officer x2 | regular | late | N `encounter_last_line` |
| 14 | 55 / V | `node_shift55_start` | Henderson | manager_henderson | finale | finale | E `encounter_henderson_showdown` |

Draft lead-ins (DRAFT):
- 11 (Brenda): "6:00 AM. The doors slide open and the coupon crowd charges Lane 4, binders first. Four hours of sleep, honey. Scan fast."
- 15 (narrator): "10:49 PM. The reshop carts tip, and the junk keeps coming: bags, boxes, a melting tub of rocky road, all rising into one shambling heap in your lane!"
- 26 (Brenda): "CODE SPARK! My register's jammed, the alarms are screaming and the line is to the doors. Rookie, take them!"
- 29 (Marsha): "'SENSOR ALERT, AISLE 9.' Again. Then the dome over Lane 4 drops on its cable and starts flashing at YOU."
- 36 (Clint): "Clint locks the audit box. Two Asset Protection officers close in on either side of Lane 4: 'Step away from the till.'"
- 48 (Marsha, optional victory line by Brenda): "The cash office door clicks. Marsha, a lockdown guard behind her, sees the drop slips in your hand: 'Put those back, {hero}. Now.'"
- 53 (Brenda): "Cheryl and two lockdown guards block the concourse with a cart of termination forms. Brenda cracks her knuckles: 'Twelve cashiers behind you, kid.'"

### 4.4 Specialist (Julian, Chad; Specialty Electronics, Action Alley)

| # | Shift / Act | Triggers after | Story reason | Enemies | Kind | Tier | Encounter |
|---|---|---|---|---|---|---|---|
| 1 | 1 / I | `node_shift1_start` | Morning rush (shared) | karen_customer, extreme_couponer | regular | early | E `encounter_register_rush` |
| 2 | 7 / I | `node_shift7_specialist` | Bluetooth speaker frenzy | overtime_screamer, karen_customer | regular | early | E `encounter_saturday_rush_wave` |
| 3 | 10 / I | `node_shift10_specialist` | VP Vance audits Specialty | regional_auditor | boss | early | E `encounter_regional_showcase` |
| 4 | 12 / II | `node_shift12_specialist` | Chad took the key ring home; customers bang on the locked cases | karen_customer x2, overtime_screamer | regular | early-mid | N `encounter_showcase_bangers` |
| 5 | 15 / II | `node_shift15_specialist` | Front end dumps opened games and tangled cables behind your counter | **NEW** go_back_golem | regular | early-mid | N `encounter_go_back_golem` |
| 6 | 22 / II | `node_shift22_specialist` | Chad blames you | slacker_veteran | boss | mid | E `encounter_slacker_showdown` |
| 7 | 26 / III | `node_shift26_specialist` | Code Spark: you ring frozen pizzas | overtime_screamer x2, karen_customer | regular | mid | N `encounter_code_spark` |
| 8 | 29 / III | `node_shift29_specialist` | Peg-hook alarms shriek on every headphone box | **NEW** shrink_sentinel x2 | regular | mid | N `encounter_sensor_meltdown` |
| 9 | 34 / III | `node_shift34_specialist` | Auditor Vance, cycle-count blame | regional_auditor | boss | mid-late | E `encounter_auditor_standoff` |
| 10 | 36 / IV | `node_shift36_specialist` | Clint's emergency key count | **NEW** asset_protection_officer x2 | regular | late | N `encounter_ap_shakedown` |
| 11 | 46 / IV | `node_shift46_specialist` | Clint's interrogation | security_clint | boss | late | E `encounter_loss_prevention_standoff` |
| 12 | 48 / V | `node_shift48_specialist` | Chad catches you mirroring his cloud deletions (his bribe at 49 follows) | `rival_chad` (elite), **NEW** asset_protection_officer | regular (elite) | late | N `encounter_lockdown_server` |
| 13 | 53 / V | `node_shift53_specialist` | The advance on the suite meets Henderson's last line | `rival_cheryl`, **NEW** asset_protection_officer x2 | regular | late | N `encounter_last_line` |
| 14 | 55 / V | `node_shift55_start` | Henderson | manager_henderson | finale | finale | E `encounter_henderson_showdown` |

Draft lead-ins (DRAFT):
- 12 (Julian): "The glass rattles under a dozen fists. 'OPEN THE CASE!' Nobody wants to hear that Chad has the keys. Hold the counter, {hero}!"
- 15 (narrator): "10:49 PM. Behind the counter, the dumped heap of opened games and tangled cables twitches, knots itself together and rises, HDMI cords whipping like tentacles!"
- 26 (Marsha): "CODE SPARK! All certified staff to the front! Your OLED shoppers can wait; these forty carts can't."
- 29 (Chad): "'Not my fault!' Chad yelps as the peg hooks scream again. Two ceiling domes drop on their cables, strobes sweeping the headphone wall."
- 36 (Clint): "Clint holds out his palm for your key ring. Two Asset Protection officers move to block the counter gate. Keep the keys."
- 48 (Chad, optional victory line by Julian): "The server room door bangs open. Chad, a lockdown guard at his back, sees the progress bar on your screen: 'Pull the plug. NOW.'"
- 53 (Julian): "Cheryl and two lockdown guards stand between the specialty crew and the suite, termination forms fanned like a hand of cards. Julian pockets the brass keys: 'After you.'"

---

## 5. Encounters: reused vs new

Each department: **7 existing reused + 7 new = 14**. Across the game: the 7 existing (all reused, unchanged) plus
**13 new encounters**:

| New encounter | Used by | Shift | Enemies |
|---|---|---|---|
| `encounter_screamer_jack` | unloader | 12 | runaway_pallet_jack |
| `encounter_clopening_coupons` | cashier | 11 | extreme_couponer x2, karen_customer |
| `encounter_showcase_bangers` | specialist | 12 | karen_customer x2, overtime_screamer |
| `encounter_go_back_golem` | all four | 15 | go_back_golem |
| `encounter_forklift_cowboy` | stocker | 20 | rival_derrick |
| `encounter_code_spark` | all four | 26 | overtime_screamer x2, karen_customer |
| `encounter_sensor_meltdown` | all four | 29 | shrink_sentinel x2 |
| `encounter_ap_shakedown` | all four | 36 | asset_protection_officer x2 |
| `encounter_lockdown_dock` | unloader | 48 | rival_derrick, asset_protection_officer |
| `encounter_lockdown_aisle4` | stocker | 48 | rival_todd, asset_protection_officer |
| `encounter_lockdown_cash_office` | cashier | 48 | rival_marsha, asset_protection_officer |
| `encounter_lockdown_server` | specialist | 48 | rival_chad, asset_protection_officer |
| `encounter_last_line` | all four | 53 | rival_cheryl, asset_protection_officer x2 |

Sharing an encounter across departments is safe: a save follows one department, and `defeatedEncounters` is per
save. Shared encounters keep a department-neutral `name`/`description`; the department flavour is in the lead-in.

Optional 14th-slot swaps if a department wants a different mix: shift 32 "Mystery Shopper" in every department
(a *recolour* of karen_customer: plain beige cardigan and a hidden clipboard; for the cashier, a recolour of
extreme_couponer with the expired $50 coupon fits better). To get down to 12, drop 29 and 53 first.

Side note (existing, not changed here): `encounter_slacker_showdown` names the boss "Slacker Veteran" in every
department, though the story names Derrick / Kyle / Brenda / Chad. Per-department names for that encounter would
match the story better; it would still use the generic slacker boss sheet.

---

## 6. New enemies

### 6.1 New templates on sheets already delivered (no art needed)

`sprites/sheets/` already holds 64 px side-view sheets for the staff (`derrick`, `todd`, `marsha`, `chad`,
`cheryl`, plus kyle, brenda, donna, mack, julian, clint, henderson). They carry idle, attack (impact=4), hurt,
cast (release=4) and a `feet` slice, which meets the regular-enemy contract. New templates, `spriteId` = the sheet:

| Template | Sheet | Used | Role |
|---|---|---|---|
| `rival_derrick` | derrick | stocker 20, unloader 48 | regular / elite |
| `rival_todd` | todd | stocker 48 | elite |
| `rival_marsha` | marsha | cashier 48 | elite |
| `rival_chad` | chad | specialist 48 | elite |
| `rival_cheryl` | cheryl | all four, 53 | regular |

If Todd wants any of the shift-48 culprits as true bosses (phased, ultimate), that needs a 96 px boss sheet with the
boss tags (windup, charge_ultimate, ultimate): new art, not counted here.

### 6.2 Genuinely new enemies (sprite sheet required before release)

Four, all 64 px regular enemies (tags: idle 4f loop, attack 5f impact=4, hurt 4f; recommended windup, cast
release=4, death). Families are suggestions to widen beyond FLESH/CONSTRUCT (plan section 4); the type names are still
to be proposed and approved.

#### `runaway_pallet_jack`: "The Screamer" (64 px, construct/physical) - unloader 12

> **Deliver:** `app/src/main/assets/game/sprites/sheets/runaway_pallet_jack.png` + `.json` · **enemy** · **64x64** cells · tags: idle, attack (impact=4), hurt (+ windup, cast (release=4), death)

The dock's worst manual pallet jack, drawn as a beast: chipped yellow body, scraped silver forks for jaws, a cracked
red handle reared up like a neck, one squealing wheel trailing a curl of smoke, a torn out-of-service tag fluttering
(no readable text). Low and wide; the forks sit on the sole line.

*Moves*: idle: shudders and jitters on the bad wheel; windup: the handle pumps, hydraulics hiss; attack: lunges forks
first; cast: throws back its handle and shrieks, a ring of squeal-waves (release on the peak); hurt: tips up on one
wheel; death: the hydraulic seal bursts and it sags flat to the floor.

#### `go_back_golem`: Go-Back Golem (64 px, verdant/physical: a cardboard joke) - all, shift 15

> **Deliver:** `app/src/main/assets/game/sprites/sheets/go_back_golem.png` + `.json` · **enemy** · **64x64** cells · tags: idle, attack (impact=4), hurt (+ windup, cast (release=4), death)

Every go-back dumped at closing, fused into a hulking heap: crushed cardboard torso, broken pallet slats for arms, a
melting ice-cream tub for a head with two red scanner-laser eyes, shopping bags, a tangled HDMI cable and dangling
price tags. Hunched, top-heavy, about 50 px tall, wider than the hero. Must read the same in every department (no
department-specific item dominates).

*Moves*: idle: slow sway, a can rolls off and pops back; windup: raises both slat-arms; attack: a two-handed slab
slam; cast: hurls a returned item (a can, a toy-dinosaur box) in an arc (release=4); hurt: boxes cave in and spring
back; death: collapses into a neat flattened stack of cardboard.

#### `shrink_sentinel`: Shrink Sentinel (64 px, construct/radiant) - all, shift 29

> **Deliver:** `app/src/main/assets/game/sprites/sheets/shrink_sentinel.png` + `.json` · **enemy** · **64x64** cells · tags: idle, attack (impact=4), hurt (+ windup, cast (release=4), death)

One of Act III's AI ceiling domes, come down to hunt: a black-glass hemisphere with a red recording LED, an amber
strobe ring and a tiny tachometer screen, hanging on a coiled cable from above the frame (or hovering; then its
shadow sits on the sole line). Cold, corporate, faintly comic.

*Moves*: idle: bobs on its cable and pans slowly; windup: the lens zooms and the LED blinks faster; attack: swoops
and rams; cast: an amber strobe pulse and alarm shriek (release=4); hurt: the glass cracks, the screen glitches;
death: the cable snaps, the dome drops and the LED fades out.

#### `asset_protection_officer`: Asset Protection Officer (64 px, flesh/shadow) - all, shifts 36, 48, 53

> **Deliver:** `app/src/main/assets/game/sprites/sheets/asset_protection_officer.png` + `.json` · **enemy** · **64x64** cells · tags: idle, attack (impact=4), hurt (+ windup, cast (release=4), death)

Clint's floor guards: black security polo with a shoulder patch (no readable text), black cargo pants, a duty belt
with a long flashlight, radio and key ring, an earpiece coil, buzz cut. Leaner and younger than Clint, no aviators,
so the two never read as the same character; a clipboard of bag-check logs under one arm. Used in pairs, so the
idle should look fine standing next to a copy of itself.

*Moves*: idle: thumbs in the duty belt, scanning; windup: keys the radio, mouth to the earpiece; attack: a
flashlight jab or a grab for the bag; cast: clicks the flashlight on, a hard white glare beam (release=4); hurt:
staggers, radio squawks; death: drops the clipboard, the logs scatter, slumps.

### 6.3 Summary

| Kind | Count | Ids |
|---|---|---|
| Existing templates reused in new fights | 3 | karen_customer, extreme_couponer, overtime_screamer |
| New templates on existing staff sheets | 5 | rival_derrick, rival_todd, rival_marsha, rival_chad, rival_cheryl |
| Recolours | 0 required (1 optional) | Mystery Shopper (karen_customer or extreme_couponer) |
| Genuinely new, need sheets | 4 | runaway_pallet_jack, go_back_golem, shrink_sentinel, asset_protection_officer |

---

## 7. Where placement was hard

- **Act I (all):** cheerful, no conflict beats; the three existing fights in 10 shifts are enough, so no new fight.
- **Act IV (all):** shifts 37-45 are investigation and paperwork beats; only shift 36 suits a fight, so Act IV has a
  10-shift gap (36 -> 46). Acceptable for the "mind games" act; Clint stays its only boss.
- **Act V (all):** the natural confrontations (shift 49 bribes, 50 sirens, 54 the door) are three-choice nodes, one
  choice of which is siding with management, so fights moved to the single-choice beats 48 and 53.
- **Stocker:** Act I runs on the generic node chain, and Act II's shift 12 (hunting a scanner) and 13 (three
  choices) gave no fight beat, so its Act II fights are 15 and 20 (Derrick, not Kyle, so the shift-22 showdown with
  Kyle stays fresh).
- **Unloader:** its Act IV/V culprits (Clint, Henderson) are already the shift-46 and shift-55 bosses, so its shift-48
  elite is Derrick returning as Henderson's errand boy (a new story idea, flagged for review).
- **Cashier:** Brenda is the Act II slacker boss but later your witness (shift 45); no new fight uses her. Its first
  new fight sits between `node_shift11_cashier` and the Act II hub.

## 8. Open questions for Todd

1. Derrick as Henderson's lackey at unloader 48, and Cheryl as Henderson's "last line" at 53 in every department:
   both are new story ideas.
2. Shift-48 culprits as elite regulars (no new art) or real bosses (96 px boss sheets)?
3. 14 per department, or drop to 12-13 (29 and 53 are the first to cut)?
4. The suggested families for the four new enemies (type names come with the per-game type pass).
