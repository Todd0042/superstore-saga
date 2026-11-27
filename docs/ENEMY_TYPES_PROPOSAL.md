# Enemy Types Proposal: Superstore Saga

Status: **proposal for Todd's approval** (2026-11-27). Nothing in the game's JSON has changed. It covers
`docs/COMBAT_PROGRESSION_PLAN.md` (engine repo) decision 18 (type names, proposed by Claude and approved by Todd) and the
Superstore item in section 4: "widen enemy types beyond 2 families; type names proposed for approval".

**Today:** 8 enemies, two families (FLESH 4, CONSTRUCT 4). Three of the four department kits are all PHYSICAL, so
weaknesses can't matter, and Karen's and the Floor Rush Crowd's PHYSICAL `selfElement` makes every Cashier, Stocker
and Unloader attack impaired (0.8) against them: **3 encounter slots fail for each of those departments today** (details below).
Henderson's `selfElement` is `"FIRE"`, which isn't an engine id and is ignored.

The plan adds 12-14 fights per department (new fights reuse these 8 enemies plus recoloured variants), so the families
below are sized for that: each new enemy should drop into one of the seven without new names.

**Assumed rules** (E6 isn't built yet; confirm when it is):
- Damage multiplier = family value from the matrix; a school in `weak` becomes **2.0**, a school in `resist` becomes
  **0.8** (override; never immune).
- `element` keeps today's meaning (same-school hits capped at 0.8). This proposal drops all three PHYSICAL elements and Henderson's invalid one.
- `armor`: none / light / heavy = flat reduction per hit; piercing spells ignore it.
- Engine ids stay; only display and spoken names change (`combat/affinity_matrix.json`).

---

## 1. Display names

The hero's "spells" are retail moves; the schools are **how you get through a shift** and the families are **who's in
your way**.

### Schools

| Engine id | Display name | Why |
|---|---|---|
| PHYSICAL | **Elbow Grease** | Box Cutter Slash, Pallet Crush: plain physical work. |
| ELECTROMANCY | **Equipment** | Barcode Scan, the 20-ton baler, the intercom: anything plugged in or hydraulic. |
| CRYOMANCY | **Store Policy** | Coupon Shield ("expired, see fine print"), the drawer slammed shut: the cold, by-the-book no. |
| PYROMANCY | **Hustle** | Cart Push, Flatbed Charge: speed and urgency on the floor. |
| HOLY | **Chain of Command** | Price Check ("paging a manager"): calling in authority. No attack uses it yet; it's ready for new grants. |
| SHADOW | **Upsell** | Warranty Pitch: fine print, protection plans and the smooth pitch. |
| NATURE | **Solidarity** | Union Break (breakroom coffee with coworkers): workers backing each other up. No attack yet. |

### Families

| Engine id | Display name | Who |
|---|---|---|
| FLESH | **Customers** | The Entitled Customer, the Extreme Couponer. Weak to "let me get my manager" (Chain of Command), policy and a smooth pitch; worker solidarity means nothing to them. |
| VERDANT | **Crowds** | The Floor Rush Crowd (Bargain Swarm), future Black Friday waves: they grow and spread. Hustle clears them; nobody hears policy over a crowd. |
| UNDEAD | **Lifers** | The Slacker Veteran: twenty years in, unkillable, an excuse for everything. Solidarity and hustle shame him; he knows every policy loophole. |
| CONSTRUCT | **Fixtures** | The Practice Dummy, and future jammed balers, collapsed pallets, broken registers. Elbow Grease and Equipment; paging a manager or upselling a pallet does nothing. |
| VOID | **Loss Prevention** | Loss Prevention Clint: cameras and interrogations. The register log (Equipment) and a coworker in the room (Solidarity) beat him; rushing looks guilty and a pitch sounds like a lie. |
| RADIANT | **Management** | Store Manager Henderson and future shift leads. Sell them on it (Upsell) or stand together (Solidarity); you can't go over their heads to themselves. |
| SILENCE | **Corporate** | The Regional Auditor (VP Vance), compliance visits. Quote their own handbook back (Store Policy); they *are* the chain of command. |

All seven families are used. The `narrations` templates should be reworded ("devastates it" reads as magic), for example
`"{enemyName} cracks - {school} gets through!"`.

---

## 2. Proposed affinity matrix

Rows are schools, columns families. **Bold** = 1.5 or more, _italic_ = 0.8. Elbow Grease is never resisted, because it's
the base of every physical department.

| School \ Family | Customers (FLESH) | Lifers (UNDEAD) | Fixtures (CONSTRUCT) | Crowds (VERDANT) | Loss Prevention (VOID) | Management (RADIANT) | Corporate (SILENCE) |
|---|---|---|---|---|---|---|---|
| **Hustle** (PYROMANCY) | 1.0 | **1.5** | 1.0 | **2.0** | _0.8_ | 1.2 | 1.0 |
| **Store Policy** (CRYOMANCY) | **1.5** | _0.8_ | 1.0 | _0.8_ | 1.2 | 1.2 | **1.5** |
| **Equipment** (ELECTROMANCY) | 1.0 | 1.0 | **1.5** | 1.0 | **1.5** | 1.0 | 1.2 |
| **Solidarity** (NATURE) | _0.8_ | **2.0** | 1.0 | 1.0 | **1.5** | **1.5** | 1.2 |
| **Chain of Command** (HOLY) | **2.0** | **1.5** | _0.8_ | 1.0 | 1.0 | _0.8_ | _0.8_ |
| **Upsell** (SHADOW) | **1.5** | 1.0 | _0.8_ | 1.2 | _0.8_ | **1.5** | 1.2 |
| **Elbow Grease** (PHYSICAL) | 1.0 | 1.2 | **1.5** | 1.2 | 1.0 | 1.0 | 1.0 |

---

## 3. Every enemy

The four bosses are shared by all four departments today, so each gets **two** weaknesses chosen so that every
department carries at least one where possible.

| id | name | boss | family | element | weak | resist | armor | rationale |
|---|---|---|---|---|---|---|---|---|
| `practice_dummy` | Practice Dummy |  | Fixtures | - | - | - | none | Training target; PHYSICAL element removed so the drill doesn't teach that Elbow Grease is resisted. |
| `karen_customer` | Entitled Customer |  | Customers | - | - | - | none | Wants a manager (Chain of Command 2.0) and folds to store policy or a smooth pitch; PHYSICAL element removed (it impaired every Stocker/Unloader attack). |
| `extreme_couponer` | Extreme Couponer |  | Customers | - | Store Policy | Upsell | none | "One coupon per transaction" (Store Policy) ends her; nobody upsells a binder tactician. |
| `regional_auditor` | Regional Auditor | yes | Corporate | - | Equipment, Elbow Grease | Upsell | light | Spreadsheet Inquisitor: the scanner logs (Equipment) and shelves actually done (Elbow Grease) pass the audit; you can't upsell an auditor. Weak to two so every department has one. |
| `overtime_screamer` | Floor Rush Crowd |  | Crowds | - | - | - | none | Bargain swarm: moving fast (Hustle) clears it; PHYSICAL element removed. |
| `slacker_veteran` | Slacker Veteran | yes | Lifers | - | Hustle, Equipment | Store Policy | light | Excuse Specialist: showing him up (Hustle) and the idle-time log (Equipment) leave no excuse; he has a policy excuse for everything. |
| `security_clint` | Loss Prevention Clint | yes | Loss Prevention | - | Equipment, Store Policy | Upsell | light | Reid-technique interrogator: the register log (Equipment) and citing your rights by policy beat him; he reads every pitch as a lie. |
| `manager_henderson` | Store Manager Henderson | yes | Management | - | Upsell, Equipment | Store Policy | heavy | The Desperate Executive needs numbers: sell him (Upsell) or show him the data (Equipment); he rewrites policy on the spot. Current element "FIRE" is not an engine id and is ignored today; drop it. |

---

## 4. Coverage check

The hero fights every encounter alone with the chosen department's kit (`classes/*.json`), so each department is checked
on its own. Kits use the proposed school changes in section 5. Guards and Union Break don't count.

### What each department is good against

| Fighter | Attack schools (proposed) | Strong (1.5+) against | Resisted (0.8) by |
|---|---|---|---|
| Cashier | Store Policy, Equipment | Store Policy: Customers, Corporate; Equipment: Fixtures, Loss Prevention | Store Policy: Lifers, Crowds; Equipment: - |
| Specialist | Equipment, Upsell | Equipment: Fixtures, Loss Prevention; Upsell: Customers, Management | Equipment: -; Upsell: Fixtures, Loss Prevention |
| Stocker | Elbow Grease, Hustle | Elbow Grease: Fixtures; Hustle: Lifers, Crowds | Elbow Grease: -; Hustle: Loss Prevention |
| Unloader | Equipment, Elbow Grease | Equipment: Fixtures, Loss Prevention; Elbow Grease: Fixtures | Equipment: -; Elbow Grease: - |

### Results

### Today: cashier solo
Fails (best party school < 1.0): 3
- `encounter_register_rush` vs `karen_customer` (party cashier): Elbow Grease 0.8
- `encounter_saturday_rush_wave` vs `overtime_screamer` (party cashier): Elbow Grease 0.8
- `encounter_saturday_rush_wave` vs `karen_customer` (party cashier): Elbow Grease 0.8
Members with no neutral-or-better school vs an enemy (party still covered): 3
- `encounter_register_rush`: cashier vs `karen_customer`
- `encounter_saturday_rush_wave`: cashier vs `overtime_screamer`
- `encounter_saturday_rush_wave`: cashier vs `karen_customer`
No reachable weakness: `karen_customer` (encounter_register_rush), `extreme_couponer` (encounter_register_rush), `regional_auditor` (encounter_auditor_standoff), `overtime_screamer` (encounter_saturday_rush_wave), `karen_customer` (encounter_saturday_rush_wave), `regional_auditor` (encounter_regional_showcase), `slacker_veteran` (encounter_slacker_showdown), `security_clint` (encounter_loss_prevention_standoff), `manager_henderson` (encounter_henderson_showdown)
Enemy slots with a reachable weakness (>=1.5): 0 of 9
Member schools never strictly best in any fight they are in: none

### Today: specialist solo
Fails (best party school < 1.0): 0
Members with no neutral-or-better school vs an enemy (party still covered): 0
No reachable weakness: `karen_customer` (encounter_register_rush), `extreme_couponer` (encounter_register_rush), `regional_auditor` (encounter_auditor_standoff), `overtime_screamer` (encounter_saturday_rush_wave), `karen_customer` (encounter_saturday_rush_wave), `regional_auditor` (encounter_regional_showcase), `slacker_veteran` (encounter_slacker_showdown), `security_clint` (encounter_loss_prevention_standoff), `manager_henderson` (encounter_henderson_showdown)
Enemy slots with a reachable weakness (>=1.5): 0 of 9
Member schools never strictly best in any fight they are in: none

### Today: stocker solo
Fails (best party school < 1.0): 3
- `encounter_register_rush` vs `karen_customer` (party stocker): Elbow Grease 0.8
- `encounter_saturday_rush_wave` vs `overtime_screamer` (party stocker): Elbow Grease 0.8
- `encounter_saturday_rush_wave` vs `karen_customer` (party stocker): Elbow Grease 0.8
Members with no neutral-or-better school vs an enemy (party still covered): 3
- `encounter_register_rush`: stocker vs `karen_customer`
- `encounter_saturday_rush_wave`: stocker vs `overtime_screamer`
- `encounter_saturday_rush_wave`: stocker vs `karen_customer`
No reachable weakness: `karen_customer` (encounter_register_rush), `extreme_couponer` (encounter_register_rush), `regional_auditor` (encounter_auditor_standoff), `overtime_screamer` (encounter_saturday_rush_wave), `karen_customer` (encounter_saturday_rush_wave), `regional_auditor` (encounter_regional_showcase), `slacker_veteran` (encounter_slacker_showdown), `security_clint` (encounter_loss_prevention_standoff), `manager_henderson` (encounter_henderson_showdown)
Enemy slots with a reachable weakness (>=1.5): 0 of 9
Member schools never strictly best in any fight they are in: none

### Today: unloader solo
Fails (best party school < 1.0): 3
- `encounter_register_rush` vs `karen_customer` (party unloader): Elbow Grease 0.8
- `encounter_saturday_rush_wave` vs `overtime_screamer` (party unloader): Elbow Grease 0.8
- `encounter_saturday_rush_wave` vs `karen_customer` (party unloader): Elbow Grease 0.8
Members with no neutral-or-better school vs an enemy (party still covered): 3
- `encounter_register_rush`: unloader vs `karen_customer`
- `encounter_saturday_rush_wave`: unloader vs `overtime_screamer`
- `encounter_saturday_rush_wave`: unloader vs `karen_customer`
No reachable weakness: `karen_customer` (encounter_register_rush), `extreme_couponer` (encounter_register_rush), `regional_auditor` (encounter_auditor_standoff), `overtime_screamer` (encounter_saturday_rush_wave), `karen_customer` (encounter_saturday_rush_wave), `regional_auditor` (encounter_regional_showcase), `slacker_veteran` (encounter_slacker_showdown), `security_clint` (encounter_loss_prevention_standoff), `manager_henderson` (encounter_henderson_showdown)
Enemy slots with a reachable weakness (>=1.5): 0 of 9
Member schools never strictly best in any fight they are in: none

### Proposed: cashier solo
Fails (best party school < 1.0): 0
Members with no neutral-or-better school vs an enemy (party still covered): 0
No reachable weakness: `overtime_screamer` (encounter_saturday_rush_wave)
Enemy slots with a reachable weakness (>=1.5): 8 of 9
Member schools never strictly best in any fight they are in: none

### Proposed: specialist solo
Fails (best party school < 1.0): 0
Members with no neutral-or-better school vs an enemy (party still covered): 0
No reachable weakness: `extreme_couponer` (encounter_register_rush), `overtime_screamer` (encounter_saturday_rush_wave)
Enemy slots with a reachable weakness (>=1.5): 7 of 9
Member schools never strictly best in any fight they are in: none

### Proposed: stocker solo
Fails (best party school < 1.0): 0
Members with no neutral-or-better school vs an enemy (party still covered): 0
No reachable weakness: `karen_customer` (encounter_register_rush), `extreme_couponer` (encounter_register_rush), `karen_customer` (encounter_saturday_rush_wave), `security_clint` (encounter_loss_prevention_standoff), `manager_henderson` (encounter_henderson_showdown)
Enemy slots with a reachable weakness (>=1.5): 4 of 9
Member schools never strictly best in any fight they are in: none

### Proposed: unloader solo
Fails (best party school < 1.0): 0
Members with no neutral-or-better school vs an enemy (party still covered): 0
No reachable weakness: `karen_customer` (encounter_register_rush), `extreme_couponer` (encounter_register_rush), `overtime_screamer` (encounter_saturday_rush_wave), `karen_customer` (encounter_saturday_rush_wave)
Enemy slots with a reachable weakness (>=1.5): 5 of 9
Member schools never strictly best in any fight they are in: none

**Findings**
- **Today: 9 failures** (3 each for Cashier, Stocker and Unloader): Karen (twice) and the Floor Rush Crowd impair
  their all-PHYSICAL kits through their PHYSICAL element. Only the Specialist (Warranty Pitch is SHADOW) is covered.
- **Proposed: no failures** for any department.
- **No reachable weakness**, proposed: the **Stocker** against Karen, the Couponer, Clint and Henderson (neutral
  1.0-1.2 only); the **Unloader** against Karen, the Couponer and the Crowd; the **Specialist** against the Couponer and the
  Crowd; the **Cashier** against the Crowd. Customers are mostly the Cashier's and Specialist's problem, which fits, but
  each department's growing kit (plan section 4) should add one school that answers Customers or Loss Prevention: Store
  Policy or Chain of Command for the Stocker and Unloader.
- **Schools never the best choice:** none once the school moves in section 5 are made. (Without them, three kits are
  one school and the question doesn't arise.)
- **Chain of Command and Solidarity have no attacks** in any kit. They're kept in the matrix for the new fights and
  grants; until then they only label the guard and Union Break.

---

## 5. Party spells whose school looks wrong

These are needed: without them three of the four departments have one school and weaknesses do nothing.

| Spell | Who | Today | Proposed | Why |
|---|---|---|---|---|
| Barcode Scan (`spell_barcode_scan`) | Specialist, Cashier | Elbow Grease (PHYSICAL) | **Equipment** (ELECTROMANCY) | A scanner swipe is equipment, not a punch; gives Cashier and Specialist a second school. |
| Drawer Smash (`spell_drawer_smash`) | Cashier | Elbow Grease (PHYSICAL) | **Store Policy** (CRYOMANCY) | Slamming the drawer shut is "this lane is closed": policy in action; gives the Cashier their own school. |
| Cart Push (`spell_cart_push`) | Stocker | Elbow Grease (PHYSICAL) | **Hustle** (PYROMANCY) | Driving carts through congested aisles is speed, not strength: Hustle. |
| Flatbed Charge (`spell_flatbed_charge`) | Stocker | Elbow Grease (PHYSICAL) | **Hustle** (PYROMANCY) | Barrelling a flatbed forward: Hustle. |
| Baler Grind (`spell_baler_grind`) | Unloader | Elbow Grease (PHYSICAL) | **Equipment** (ELECTROMANCY) | The 20-ton hydraulic baler is equipment. |
| Coupon Shield (`spell_coupon_shield`) | Cashier | Elbow Grease (PHYSICAL) | **Store Policy** (CRYOMANCY) | Citing the expiry date in fine print is Store Policy (guard; cosmetic). |
| Union Break (`spell_union_break`) | Specialist, Unloader, Cashier, Stocker | Chain of Command (HOLY) | **Solidarity** (NATURE) | Breakroom coffee with coworkers is Solidarity (mana restore today; becomes a heal when mana goes). |

Resulting attack schools: **Cashier** Equipment + Store Policy, **Specialist** Upsell + Equipment, **Stocker** Elbow
Grease + Hustle, **Unloader** Elbow Grease + Equipment.

Also: the template spells (`power_strike`, `flame_spark`, `fireball`, `chain_lightning` and the rest) belong to the
engine's template `hero` character, which no department uses; they can be ignored or removed in the pass.

**Piercing candidates** (Henderson is heavy armor): Box Cutter Slash, Barcode Scan.

---

## 6. For Todd to decide

1. The names, especially **Store Policy** (CRYOMANCY), **Upsell** (SHADOW) and **Management** vs **Corporate** for
   Henderson and the Auditor.
2. The seven school moves in section 5 (Drawer Smash as Store Policy is the least obvious).
3. Two weaknesses per shared boss now, or one each once the departments have their own fights.
4. The weak = 2.0 / resist = 0.8 override rule.
5. Note for whoever applies this: the repo has two `llm-sync` WIP branches (`wip/desktop/main`, `origin/wip/laptop/main`,
   2026-11-26) with commits not on `main`; they hold sprite art only and don't touch enemies, spells or classes.
