# Retail Inferno — Overall Game Design Document

> **Repository:** `retail-inferno`  
> **Package ID (`applicationId`):** `com.voicerpg.retailinferno`  
> **Engine Foundation:** `voice-rpg-engine` (Customized for touch-first & hybrid gameplay)  
> **Status:** Planning & Architectural Design Phase

---

## 1. Executive Summary & Core Pillars

*Retail Inferno* is a satirical, psychological survival JRPG set within the sprawling, windowless expanse of a mega-retail department store ("The Store"). Rather than classic high-fantasy tropes, the game translates workplace mechanics, retail culture, peer friction, and corporate dread into an RPG framework.

### Core Pillars:
1. **Touch / Button-First Control Scheme:**
   - Designed around fast, responsive on-screen virtual controls.
   - Battle UI uses tactical buttons with calibrated random tier outcomes (inherited from the `echoes-of-logos` button engine).
   - Voice controls are non-mandatory and reserved for optional flavor or accessibility.
2. **Shift Loop Architecture:**
   - Chapters represent work shifts and promotion tiers.
   - Core gameplay alternates between story dialogue, arcade-style high-production mini-games, and dramatic turn-based boss battles at the end of each act.
3. **Branching Department Origins:**
   - Character creation replaces conventional fantasy classes with **Assigned Store Departments** (Front End, Sales Floor, Backroom, Specialty), each featuring an independent narrative track.
4. **Gradual Psychological Descent:**
   - Starts cheerful, bright, and comedic in Act I.
   - Degenerates through ground-level associate toxicity in Act II.
   - Escalates into cold corporate metrics, espionage, and psychological thriller/dread in Acts III through V.

---

## 2. Game Flow: App Launch through Shift Start

```mermaid
flowchart TD
    Launch[App Launch] --> SplashScreen[Corporate Splash: 'MegaMart Global Logistics']
    SplashScreen --> TitleScreen[Main Menu / Hiring & Attendance Portal]
    
    TitleScreen --> Options[Options / Audio & Display Settings]
    TitleScreen --> Credits[Credits / Corporate Disclaimers]
    TitleScreen --> ContinueGame[Swipe Badge / Resume Existing Shift]
    TitleScreen --> Apply[Apply Now / New Applicant Kiosk]

    subgraph Phase1["PHASE 1: THE JOB APPLICATION (Pre-Hire)"]
        Apply --> Step1[Step 1: Applicant Profile & Details]
        Step1 --> Step2[Step 2: Position Desired / Department Selection]
        Step2 --> Step3[Step 3: 10-Question Pre-Employment Assessment]
        Step3 --> Step4[Step 4: Algorithmic Hiring Decision & Job Offer]
    end

    subgraph Phase2["PHASE 2: DAY 1 ORIENTATION & FIRST CLOCK-IN"]
        Step4 --> Step5[Step 5: Uniform Fitting, Nametag & Flair Pin Setup]
        Step5 --> Step6[Step 6: Digital Liability Waiver & First Badge Swipe]
    end

    Step6 --> Act1Scene1[Act I Scene 1: The Morning Huddle]
```

### A. Title Screen (The MegaMart Hiring & Attendance Portal)
Stripped of all fantasy artifacts (no tutorial battles, no microphone chant calibrations), the title screen presents an authentic in-universe hiring and time clock terminal:
* **Visual Aesthetic:** A 1990s CRT monitor embedded in breakroom faux-wood paneling, faint green phosphor text, and an electronic badge-swipe slot.
* **Ambient Audio:** 60Hz fluorescent hum, distant overhead chime, and tinny department-store elevator muzak.
* **Menu Actions:**
  1. **`[ 📝 SUBMIT APPLICATION / APPLY NOW ]`** — Launches the New Applicant Kiosk (starts a new game).
  2. **`[ 💳 SWIPE BADGE / RESUME SHIFT ]`** — Select an active Employee File (3 slots showing Name, Department, Act, Morale, Engagement, and Attendance Points).
  3. **`[ ⚙️ STORE POLICY / OPTIONS ]`** — Sound FX, BGM volume, TTS narrator speed, button touch sensitivity.
  4. **`[ 📋 EMPLOYEE HANDBOOK / CREDITS ]`** — Satirical corporate disclaimers and credits.

---

### B. Phase 1: The Job Application Kiosk (Pre-Employment Screening)

#### Step 1: Applicant Profile & Identification
* **Applicant Name:** Text input (default: *"Taylor"*).
* **Pronouns / Salutation:** Influences NPC dialogue and automated corporate paperwork.

#### Step 2: Position Desired (Department Track Selection)
The applicant chooses which department opening they are applying for:
* **Front End:** Cashier Associate
* **Sales Floor:** Merchandising & Zone Associate
* **Backroom:** Logistics & Freight Handler
* **Specialty:** High-Ticket Counter Sales (Tech / Deli / Jewelry)

#### Step 3: The 10-Question Pre-Employment Personality Profiling Assessment
Modeled directly after notorious retail pre-hire screening tests (e.g., Unicru / Kronos retail aptitude assessments). This assessment is taken **during the application process before being hired**. Each question features 4 forced-choice answers ranging from **Extreme Sycophant**, **Pragmatic Slacker**, **Honest Human**, to **Passive-Aggressive Rebel**. These answers screen the applicant and calculate their starting **Morale (70–100%)**, **Engagement (60–100%)**, and hidden **Ass-Kissing Score (0–100)**:

1. **"An associate takes an extra 6 minutes on their scheduled 15-minute break. What is the most appropriate action?"**
   - A) *"Report their badge number immediately to Asset Protection. Time theft is real theft."* `(+Ass-Kissing 15, -Morale 5)`
   - B) *"Quietly ask them if they're okay—everyone needs a breather."* `(+Morale 10, -Engagement 5)`
   - C) *"Ignore it. If management isn't watching, it didn't happen."* `(+Slacker Bias 10)`
   - D) *"Take an extra 6 minutes on my own break to maintain corporate equilibrium."* `(+Morale 15, -Ass-Kissing 15)`

2. **"You spot an abandoned half-eaten rotisserie chicken in the toy aisle. What is your response?"**
   - A) *"Carefully inspect the chicken, log it on the inventory shrink app, sanitize the shelf, and smile!"* `(+Engagement 10, +Ass-Kissing 10)`
   - B) *"Page the maintenance associate over the intercom and walk in the opposite direction."* `(+Morale 5)`
   - C) *"Pretend I didn't see it. I work in a different department."* `(-Engagement 10)`
   - D) *"Check if it's still warm, then throw it into an empty shopping cart."* `(+Morale 10, -Engagement 5)`

3. **"How do you feel about participating in the mandatory daily MegaMart morning cheer?"**
   - A) *"I practice the synchronized clapping and spelling in front of my mirror at home!"* `(+Ass-Kissing 20, -Morale 5)`
   - B) *"I mouth the words so management doesn't pull me into an office."* `(Neutral Baseline)`
   - C) *"I strategically time my morning bathroom break to coincide with the cheer."* `(+Morale 15, -Ass-Kissing 15)`
   - D) *"I clap with dead eyes while questioning my life choices."* `(-Engagement 15, +Morale 5)`

4. **"A customer insists that a brand-new \$800 OLED TV was marked on the shelf for \$8.00. How do you resolve this?"**
   - A) *"Apologize profusely and honor the price—the customer is always our guest!"* `(+Ass-Kissing 10, -Morale 15)`
   - B) *"Explain the policy calmly while silently screaming inside."* `(+Engagement 5, -Morale 5)`
   - C) *"Call the Assistant Manager over so they can get yelled at instead of me."* `(+Morale 10)`
   - D) *"Stare at them in dead silence until they become uncomfortable and leave."* `(+Morale 15, -Ass-Kissing 10)`

5. **"Management asks you to stay past your scheduled clock-out time to unload a surprise freight truck. How do you respond?"**
   - A) *"Cancel my personal evening plans! MegaMart comes first!"* `(+Ass-Kissing 25, +Engagement 10, -Morale 20)`
   - B) *"Agree to stay, but spend half the time checking my phone behind a cardboard stack."* `(+Slacker Bias 15)`
   - C) *"Politely decline citing prior family obligations."* `(+Morale 10, -Ass-Kissing 10)`
   - D) *"Pretend I didn't hear the page, clock out at 4:59 PM, and sprint to my car."* `(+Morale 20, -Ass-Kissing 20)`

6. **"Which of the following statements best describes your philosophy on workplace safety rules?"**
   - A) *"Safety policies were written by corporate angels and must never be questioned."* `(+Ass-Kissing 15)`
   - B) *"Follow the ones that prevent losing a limb; ignore the ones that slow down productivity."* `(+Productivity Bias 10)`
   - C) *"I wear my back brace because it looks cool, not because I lift with my legs."* `(+Morale 5)`
   - D) *"Safety meetings are just a paid opportunity to sit on folding chairs and nap."* `(+Morale 10, -Engagement 10)`

7. **"You find a working handheld scanner hidden on top of the seasonal paper towel steel racks. What do you do?"**
   - A) *"Turn it into the Asset Protection office with an incident report."* `(+Ass-Kissing 15)`
   - B) *"Stash it in my personal department locker so I always have equipment."* `(+Survival Instinct 15)`
   - C) *"Leave it where it is—someone's life depends on that hidden scanner."* `(+Peer Solidarity 10)`
   - D) *"Trade its location to another associate in exchange for a free energy drink."* `(+Street Smarts 15)`

8. **"A customer approaches you with 10 expired coupons printed on illegible desktop paper. How do you proceed?"**
   - A) *"Manually key in all 10 coupons to make the customer feel valued and heard."* `(-Morale 15, +Ass-Kissing 10)`
   - B) *"Examine each expiration date with a magnifying glass while savoring their disappointment."* `(+Morale 15)`
   - C) *"Hit 'Manager Override' and pray the register doesn't flag my operator number."* `(-Engagement 10)`
   - D) *"Suddenly develop severe technical difficulties and restart the register terminal."* `(+Morale 10)`

9. **"What is the primary reason you applied to join the MegaMart family?"**
   - A) *"To build a long-term corporate career and one day manage my own district!"* `(+Ass-Kissing 20, +Engagement 15)`
   - B) *"Because human survival requires currency for rent and instant noodles."* `(Realist: +Morale 10, -Engagement 10)`
   - C) *"I was told there would be free pizza in the breakroom on weekends."* `(+Foodie Bias 10)`
   - D) *"My parole officer said I needed a W-2 job by Thursday."* `(+Chaos Bias 15)`

10. **"An executive from the Home Office is touring your aisle. What do you say when they ask how things are going?"**
    - A) *"Everything is flawless, sir! MegaMart is the greatest workplace in America!"* `(+Ass-Kissing 25, -Morale 10)`
    - B) *"Things are great, but we could really use a working pallet jack."* `(Honest: +Engagement 5, Ass-Kissing -5)`
    - C) *"Smile, nod, say 'Never better!', and retreat behind the milk crates."* `(+Pragmatism 10)`
    - D) *"Tell them the raw truth about the air conditioning being broken since June."* `(+Morale 20, -Ass-Kissing 25)`

#### Step 4: Algorithmic Hiring Decision & Job Offer
Upon submitting question 10, the kiosk processes the application through the automated corporate hiring algorithm:
```
╔══════════════════════════════════════════════════════════╗
║              APPLICATION PROCESSING COMPLETE             ║
║                                                          ║
║  ALGORITHMIC FIT RATING: "POTENTIALLY COMPLIANT"         ║
║  CALIBRATED MORALE: 85%  |  ENGAGEMENT: 90%              ║
║                                                          ║
║  STATUS: CONDITIONAL OFFER OF EMPLOYMENT EXTENDED        ║
║  REPORT TO: STORE #1042 (ROOM B)                         ║
║  DATE: MONDAY 06:00 AM FOR NEW HIRE ORIENTATION          ║
╚══════════════════════════════════════════════════════════╝
```

---

### C. Phase 2: Day 1 Orientation & First Clock-In

#### Step 5: Uniform Fitting, Nametag & Flair Setup
Having officially arrived for Day 1 Orientation, the player prepares their physical work attire:
* **Nametag Customization:** Confirms first name display on their plastic badge.
* **Vest Accent & Starting Flair Pin:** Choose a vest accent trim and initial corporate flair pin (*"In Training"*, *"Smiley Face"*, *"Safety First"*, or *"Ask Me Anything"*).

#### Step 6: Digital Liability Waiver & First Official Clock-In
* **Liability & Non-Disparagement Waiver:** The player signs a satirical electronic terms-of-employment waiver.
* **The First Clock-In:** The player swipes their brand new badge `#1042-882` at the punch clock, accompanied by an authentic terminal beep.
* **Transition to the Floor:** The screen fades from the terminal display to the fluorescent glow of the Breakroom for **Act I: Scene 1 (The Morning Huddle)**.

---

## 3. Store Tracks & Player Roles

Instead of selecting Warrior, Mage, or Scout, the player selects their starting department at orientation:

| Department Track | Starting Role | In-Game Playstyle | Core Equipment & Aesthetic |
|---|---|---|---|
| **Front End** | Register Cashier | High speed, multi-target barcode blasts, customer de-escalation, drawer balancing. | Register scanner, blue smock, roll of register tape, nametag lanyard. |
| **Sales Floor** | Merchandiser & Zone Associate | Zone defense, item facing, crowd navigation, physical heavy cart management. | Box cutter, yellow safety vest, shelf label gun, flatbed cart. |
| **Backroom** | Freight Unloader & Bin Runner | High physical endurance, forklift maneuvering, industrial machinery, pallet control. | Steel-toe boots, back brace, work gloves, hydraulic pallet jack. |
| **Specialty** | Counter Sales Specialist (Electronics / Deli) | Precision time-attack, high-ticket upsells, technical jargon, locked display cases. | Master key ring, black specialist polo, two-way radio, clipboard. |

---

## 4. Gameplay Loop & Chapter Flow

Each Chapter represents a pivotal **Shift** or **Work Week** in the store:

```mermaid
flowchart TD
    Start[Shift Start: Morning Huddle & Story Dialog] --> MG1[Mini-Game Phase 1: Shift Routine]
    MG1 --> MidStory[Mid-Shift Drama: Breakroom / Aisle Encounter]
    MidStory --> MG2[Mini-Game Phase 2: Shift Escalation / Rush Hour]
    MG2 --> Climax[Closing Announcement & Incident Escalation]
    Climax --> Boss[Act Climax: Turn-Based Boss Battle]
    Boss --> Review[Clock-Out & Performance Evaluation]
```

### Shift Phases:
1. **Morning Huddle / Clock-In:** Narrative dialogue, player decisions, gossip updates, and shift objectives.
2. **Mini-Game Phase 1:** High-production 2D arcade sequence reflecting daily tasks (e.g., freight hauling, crowd dodging, or counter service).
3. **Mid-Shift Drama:** Narrative inflection point occurring in the breakroom, smoking area, or managerial back office.
4. **Mini-Game Phase 2:** Escalated arcade challenge with higher stakes (e.g., top-down twin-stick shooter defense during rush hour or Black Friday sales).
5. **Act Climax Boss Battle:** Turn-based tactical battle utilizing the engine's full status, affinity, and tier-multiplier combat mechanics.
6. **Clock-Out:** Performance scoring, XP gain, store vouchers, and story flag updates.

---

## 5. Combat Mechanics & On-Screen Resonance Tiers

Boss battles repurpose the underlying engine's resonance combat system using **tactical on-screen buttons** with randomized tier calibrations (matching the `echoes-of-logos` model):

```mermaid
flowchart TD
    Btn[Player Taps Action Button on POS Terminal HUD] --> RNG{Random Tier Calibration}
    RNG -->|Standard Roll (Common)| T1["Tier I: Standard Duty (1.0x)<br/>'Clean up on Aisle 3'"]
    RNG -->|Elevated Roll (Uncommon)| T2["Tier II: Escalated Protocol (1.5x - 2.0x)<br/>'Customer Service to Register 2'"]
    RNG -->|Jackpot Roll (Rare)| T3["Tier III: Code Red Override (2.5x - 3.0x)<br/>'MANAGEMENT OVERRIDE SANCTIONED'"]
```

### Action Categories & Visual Indicator Badges:
* `[⚔️ ATK]` **Offensive:** *Barcode Laser Flash*, *Cart Slam*, *Policy Citation*, *Heavy Lift*.
* `[💥 AOE]` **Area of Effect:** *Intercom Page All*, *Spill on Floor*, *Flash Clearance Sale*, *Fire Alarm Pull*.
* `[💚 HEAL]` **Recovery:** *Union Break*, *Smuggled Energy Drink*, *Lukewarm Breakroom Coffee*, *Deep Breath*.
* `[🛡️ DEF]` **Defensive:** *Clock Out Shield*, *Show Nametag*, *"I Just Work Here" Stance*, *Wet Floor Sign*.
* `[✨ MANA / SANITY]` **Morale Boost:** *Employee Discount*, *Spite*, *Overtime Paycheck*.

### Battle Stats & Status Mechanics:
* **Combat Health (HP):** Immediate combat patience during a boss encounter (reaching 0 causes the character to break down or walk away from the argument).
* **Combat Energy (MP):** Fuel for tactical responses, policy citations, and break actions.
* **Affinity / Element:** Workplace Persona (Customer-Facing, Policy-Stickler, Slacker-Immune, Industrial).

---

## 6. Persistent Meta-Meters (The Narrative HUD)

Spanning across **Story Dialog, Mini-Games, and Boss Battles**, three persistent status gauges sit at the top of the screen:

```
[ ❤️ MORALE: ▓▓▓▓▓▓░░░░ 60% ]   [ ⚡ ENGAGEMENT: ▓▓▓░░░░░░░ 30% ]   [ ⚠️ OCCURRENCES: 3.5 / 5.0 ]
```

### 1. Morale Meter (Emotional Battery: 0% - 100%)
- **What it represents:** The character's emotional resilience, mental health, and willingness to endure retail absurdity.
- **Narrative Trajectory:** Starts at 100% in Act I; steadily eroded by peer toxicity in Act II and corporate gaslighting in Acts III–V.
- **Gameplay Effect:** When Morale is high, the player has access to witty, patient, or de-escalating dialogue choices. When Morale is depleted (<20%), dialogue choices shift to nihilistic, cynical, or explosive outbursts.

### 2. Engagement Meter (Effort & Caring: 0% - 100%)
- **What it represents:** How much the character still buys into corporate propaganda, cares about doing a good job, and believes hard work will pay off.
- **Narrative Trajectory:** High in Act I ("Employee of the Month hopeful"); drops as the player realizes slackers get promoted and good work is rewarded with more work ("Quiet Quitting").
- **Gameplay Effect:** Gates high-effort actions. A player with 15% Engagement physically cannot choose *"Stay late to fix the seasonal aisle"* or *"Help the frantic customer find baking soda"*. Instead, low engagement unlocks "slacker shortcuts" (e.g., *"Hide in the fixture room"*, *"Tell customer it's out of stock without checking"*).

### 3. Attendance Occurrence Tracker (The 5-Point "Selective Enforcement" System)
- **The Retail Dread Mechanic:** Inspired by strict real-world retail attendance policies (e.g., Walmart's 5-point system).
  - Tardy (10 mins late): `+0.5 points`
  - Unexcused Absence / Call-In: `+1.0 point`
  - Leaving Early: `+0.5 points`
  - "No Call, No Show": `+2.0 points`
- **The 5.0+ Point Paradox (Hypocritical Enforcement):**
  - Reaching **5.0 points does NOT automatically trigger immediate termination**.
  - Instead, the corporation reviews the employee through a corrupt, two-factor evaluation:
    1. **Productivity Score (Output/Velocity):** How much freight do they stock? How fast is their scan IPM? Can the store survive losing them?
    2. **Ass-Kissing Score (Brown-Nosing / Compliance):** Do they attend the morning cheer? Do they laugh at the assistant manager's jokes? Do they report coworkers?
  - **The Evaluation Outcomes at 5.0+ Points:**
    - **High Productivity + High Ass-Kissing:** Points are quietly "cleared from the system" or perpetually ignored ("points can build to 8.5 or 12.0 without a word").
    - **High Productivity + Low Ass-Kissing:** Management holds the points over the player's head as a weapon ("You're at 6.0 points, so you *will* work this weekend or we process your paperwork today").
    - **Low Productivity + High Ass-Kissing:** Management protects their favorite lackey, making excuses to corporate HR.
    - **Low Productivity + Low Ass-Kissing:** Immediate ruthless termination / Game Over.
- **The Dilemma (Desire to Not Be There vs. Survival):**
  - Story prompts regularly test the player's boiling point: *"You wake up at 5:30 AM with a pounding migraine. You have 4.5 points. Do you drag yourself into the store, or burn another point to sleep in?"*
  - Calling out pushes the player into the dangerous 5.0+ danger zone, where survival hinges on whether their output or sycophancy is high enough to dodge the corporate chopping block.

---

## 7. Mini-Game Portfolio (High-Level Concepts)

1. **Top-Down Shooter (Dead Nation Style):**
   - Twin-stick controls. Wielding handheld barcode scanners or price guns to stun, tag, and clear swarming crowds during peak holiday madness.
2. **The Pallet Haul / Aisle Dodger (Frogger / Overcooked Style):**
   - Navigating overloaded pallet jacks and carts through packed aisles while dodging runaway toddlers, spills, and blocked carts without damaging freight.
3. **The Baler Compactor (Physics / Puzzle):**
   - High-speed cardboard crushing and wire-tying before the hydraulic compactor overflows.
4. **Counter Commotion (Precision Time-Attack):**
   - Slicing deli meats to exact fractions, keying paint formulas, or finding the right key from a 50-key ring under customer pressure.

---

## 8. 4-Slot Save System & Auto-Play / Pure Story Mode

To support all 4 distinct department storylines (Front End, Sales Floor, Backroom, Specialty), the engine supports 4 independent employee file slots alongside a seamless **Auto-Play / Pure Story Mode**.

```mermaid
flowchart TD
    SelectSlot[Select Employee Badge Slot 1-4] --> ChoiceMode{Select Gameplay Mode}
    ChoiceMode -->|Manual Shift| ManualPlay[Full Gameplay: Mini-Games, Manual Choices & Turn-Based Battles]
    ChoiceMode -->|Pure Story Shift| AutoPlay[Auto-Play Mode: Skipped Mini-Games, Paced TTS Narration & Heuristic Choices]
    
    subgraph AutoPlayLogic["Auto-Play Narrative Heuristics"]
        AutoPlay --> Filter[Rule 1: Filter Out Forbidden Choices<br/>#autoPlayForbidden: No Call-Outs / Taking Day Off]
        Filter --> StatCheck[Rule 2: Read Current Character State<br/>Morale, Engagement, Ass-Kissing Score]
        StatCheck --> Match[Rule 3: Score Choices via Archetype Tags<br/>#SYCOPHANT, #SLACKER, #ENTHUSIASTIC, #DEFIANT]
        Match --> Execute[Auto-Select Highest Weighted Option & Advance]
    end
```

### A. The 4-Slot Personnel Filing Terminal
* **In-Universe Representation:** A corporate personnel filing terminal showing active employee badges:
  ```
  ╔══════════════════════════════════════════════════════════════════════╗
  ║                    ASSOCIATE PERSONNEL TERMINAL                      ║
  ║                                                                      ║
  ║  [ SLOT 1 ] TAYLOR (Front End)    | Act II  | Morale: 62% | 1.5 Pts   ║
  ║  [ SLOT 2 ] JORDAN (Backroom)     | Act I   | Morale: 90% | 0.0 Pts   ║
  ║  [ SLOT 3 ] MORGAN (Sales Floor)  | Act IV  | Morale: 28% | 4.5 Pts   ║
  ║  [ SLOT 4 ] ALEX   (Specialty)    | [ EMPTY BADGE / NEW APPLICANT ]  ║
  ╚══════════════════════════════════════════════════════════════════════╝
  ```
* **Mode Selection on Load:**
  - Tapping a populated badge slot presents a prompt:
    - **`[ 🎮 MANUAL SHIFT (Interactive) ]`** — Complete interactive experience: hands-on dialogue decisions, arcade mini-games, and boss battles.
    - **`[ 📖 PURE STORY SHIFT (Auto-Play) ]`** — Passive visual-novel / audiobook experience: animated sprites, full TTS character narration, auto-advancing dialogue, and automatic story decisions.
  - Players can toggle between Manual and Auto-Play at any time from the in-game options menu or pause overlay.

---

### B. Auto-Play Mini-Game Skipping & Stat Preservation
When a shift reaches an arcade mini-game phase (Twin-Stick Barcode Blaster, Pallet Haul, Baler Crush, Counter Commotion) while in Auto-Play mode:
* **Instant Bypass:** The mini-game is skipped without launching arcade controls.
* **Neutral Stat Preservation:**
  - The shift records a neutral "Standard Shift / Pass" completion.
  - **No stat gain or loss:** Morale, Engagement, and Attendance points are completely frozen across the skipped mini-game.
  - Story dialogue immediately resumes from the post-shift narrative node.
* **Boss Battles in Auto-Play:** Run via an automated AI combat loop (prioritizing status de-escalation and defense) with cinematic speed, or resolve as a dramatic story cutscene standoff.

---

### C. Auto-Play Narrative Heuristics (Archetype-Weighted Decision Engine)

To prevent Auto-Play from feeling like a random coin-flip, story choices are made dynamically based on the character's **current psychological state**:

#### Rule 1: The Hard Boundary (No Call-Outs / No Abandoning Shift)
* Auto-Play **strictly never selects options that call out sick, take the day off, or abandon a shift**.
* Any choice tagged with `autoPlayForbidden: true` (e.g. *"Call out sick and go back to sleep"*) is removed from the Auto-Play candidate list, ensuring the story always progresses into store events.

#### Rule 2: Choice Archetype Tags in Story Nodes
Each choice in `story/nodes.json` declares an `autoPlayArchetype` tag and base weight:
```json
[
  {
    "text": "Report Brenda's uncleaned lane immediately to the Assistant Manager.",
    "voiceKeyword": "report",
    "autoPlayArchetype": "SYCOPHANT",
    "autoPlayWeight": 80,
    "statModifiers": { "assKissing": 10, "morale": -5 }
  },
  {
    "text": "Shrug, mutter under your breath, and clean it yourself.",
    "voiceKeyword": "clean",
    "autoPlayArchetype": "MARTYR_WORKHORSE",
    "autoPlayWeight": 60,
    "statModifiers": { "engagement": -5, "morale": -10 }
  },
  {
    "text": "Duck into the single-stall restroom and check your phone for 20 minutes.",
    "voiceKeyword": "hide",
    "autoPlayArchetype": "SLACKER",
    "autoPlayWeight": 70,
    "statModifiers": { "morale": 10, "engagement": -15 }
  },
  {
    "text": "Burn an attendance point, call out, and turn off your alarm.",
    "voiceKeyword": "call out",
    "autoPlayForbidden": true
  }
]
```

#### Rule 3: Dynamic Selection Driven by Associate Psychological State
Auto-Play calculates choice priority by evaluating the associate's active stats:
| Associate State | Active Stat Trigger | Auto-Play Behavioral Archetype | Narrative Flavor |
|---|---|---|---|
| **Eager New Hire (Act I)** | Engagement $\ge 70\%$ | Prioritizes `#ENTHUSIASTIC` / `#COMPLIANT` | Tries hard, eager to please, volunteers for tasks. |
| **Corporate Brown-Noser** | Ass-Kissing Score $\ge 60$ | Prioritizes `#SYCOPHANT` / `#BROWN_NOSE` | Sucks up to supervisors, quotes policy, snitches on peers. |
| **Quiet Quitter (Act II–III)** | Morale $< 35\%$ or Engagement $< 40\%$ | Prioritizes `#SLACKER` / `#QUIET_QUIT` | Minimum effort, hides from managers, avoids confrontations. |
| **Paranoid Survivor (Act IV–V)** | Act IV+ and Morale $< 25\%$ | Prioritizes `#DEFIANT` / `#TRUTH_SEEKER` | Uncovers the frame-job, refuses to sign bogus write-ups, fights back. |

---

## 9. Dynamic Conflict Event Engine & Save State Architecture

To deliver immense replayability across 56 shifts and 4 department tracks without feeling repetitive or predictable, mid-shift narrative events and dilemmas are managed by a **Dynamic Conflict Event Engine**. Rather than hardcoding a static event into each shift, the game selects an eligible incident at runtime from a data-driven database (`assets/game/incidents/incidents.json`).

```mermaid
flowchart TD
    Shift[Shift N Begins] --> Pool[Load Incidents Pool from JSON]
    Pool --> Filter[Filter Candidate Incidents]
    
    subgraph FilterChecks["Candidate Eligibility Predicates"]
        Filter --> C1["1. Track Match (e.g. Sales Floor or All)"]
        C1 --> C2["2. Act Match (e.g. Current Act == Act II)"]
        C2 --> C3["3. History Check: occurrenceCount < maxOccurrences"]
        C3 --> C4["4. Cooldown Check: (currentShift - lastShiftTriggered) > cooldownShifts"]
        C4 --> C5["5. State & Flag Checks (Morale, Engagement, Required/Forbidden Flags)"]
    end

    C5 --> Lottery{Weighted Random Selection}
    Lottery -->|Event Chosen| Trigger[Trigger Event Dialogue & Choices]
    Trigger --> UpdateSave[Update Save File: occurrenceCount + 1, lastShiftTriggered = currentShift]
    Lottery -->|All on Cooldown| Fallback[Draw Minor Daily Shift Event / Ambient Floor Routine]
```

### A. Incident Data Schema (`incidents.json`)
Every incident is declared in data with strict occurrence and cooldown constraints:

```json
{
  "id": "event_connex_tryst",
  "title": "The Lawn & Garden Connex Trailer Tryst",
  "applicableTracks": ["all"],
  "eligibleActs": [2, 3],
  "maxOccurrences": 1,
  "cooldownShifts": 0,
  "weight": 85,
  "prerequisites": {
    "minMorale": 0,
    "maxMorale": 100,
    "requiredFlags": [],
    "forbiddenFlags": ["todd_fired", "taylor_transferred"]
  },
  "startNodeId": "node_connex_tryst_001"
}
```

```json
{
  "id": "event_microwave_fish",
  "title": "The Charred Fish Breakroom Biohazard",
  "applicableTracks": ["all"],
  "eligibleActs": [1, 2, 3],
  "maxOccurrences": 3,
  "cooldownShifts": 5,
  "weight": 120,
  "prerequisites": {
    "minMorale": 10,
    "maxMorale": 80,
    "requiredFlags": [],
    "forbiddenFlags": []
  },
  "startNodeId": "node_microwave_fish_001"
}
```

### B. Occurrence Caps & Cooldown Rules
1. **One-Time Major Plot Catalysts (`maxOccurrences: 1`):**
   - High-impact narrative events (e.g. catching Todd in the Connex trailer, the master key lockdown, the compactor fraud discovery, the Blackout) can only occur **once per campaign**. Once triggered, they are permanently retired from the candidate pool.
2. **Recurring Retail Nuances (`maxOccurrences: 2 - 4`):**
   - Common workplace friction (e.g. 45-minute bathroom disappearances, broken pallet jacks, go-back cart dumps, cold pizza crusts) can recur up to 2–4 times over 56 shifts.
3. **Cooldown Guardrail (`cooldownShifts: 4 - 8`):**
   - After an event triggers on Shift `S`, it cannot be selected again until at least Shift `S + cooldownShifts`. This strictly prevents back-to-back repetition of the same dilemma on consecutive shifts.
4. **Guaranteed Milestone Anchors:**
   - Specific milestone shifts (e.g., Shift 10, Shift 22, Shift 34, Shift 46, Shift 56) have locked boss confrontations that bypass the random pool.
5. **Safe Fallback Pool:**
   - If a player's unique combination of high cooldowns and specific flags exhausts the candidate pool, the engine draws from an infinite-occurrence `fallback_minor_nuances` pool (e.g., *Customer asking where the tape is*, *Price check chime*, *Routine dust mopping*).

---

### C. Save File Persistence Schema (`SaveGameData`)
The record of triggered incidents is saved directly within each of the 4 employee file save slots:

```kotlin
data class IncidentHistoryEntry(
    val occurrenceCount: Int = 0,
    val lastShiftTriggered: Int = -999
)

data class SaveSlotData(
    val slotIndex: Int,                         // Slot 1 to 4
    val employeeId: String,                     // e.g. "BADGE-1042-882"
    val departmentTrack: String,                // "front_end", "sales_floor", "backroom", "specialty"
    val currentAct: Int,                        // 1 to 5
    val currentShift: Int,                      // 1 to 56
    val morale: Int,                            // 0 to 100
    val engagement: Int,                        // 0 to 100
    val occurrences: Float,                     // e.g. 3.5 / 5.0
    val productivityScore: Int,                 // Output / velocity rating
    val assKissingScore: Int,                   // Sycophancy / compliance rating
    val storyFlags: Set<String>,                // Narrative state flags
    
    // Dynamic Incident Tracking Map (Event ID -> History Entry)
    val incidentHistory: Map<String, IncidentHistoryEntry> = emptyMap()
)
```

---

## 10. Architecture & File Structure

```
retail-inferno/
├── app/
│   ├── build.gradle.kts           # Configured with applicationId: com.voicerpg.retailinferno
│   └── src/main/
│       ├── assets/game/           # Content-neutral JSON data templates
│       │   ├── manifest.json      # Game title, initial scene, victory lines
│       │   ├── characters/        # Department starting identities
│       │   ├── classes/           # Associate roles & skill definitions
│       │   ├── encounters/        # Act boss battles
│       │   ├── enemies/           # Coworkers, customers, and corporate managers
│       │   ├── spells/            # Workplace actions, pages, and policies
│       │   ├── incidents/         # Data-driven dynamic conflict event database (incidents.json)
│       │   └── story/             # Dialogue nodes and scene definitions
│       └── java/com/voicerpg/engine/
├── docs/                          # Design documentation & balance data
├── OVERALL_DESIGN.md              # High-level architecture & mechanics (this file)
├── NARRATIVE_DESIGN.md            # Comprehensive 5-act narrative and dialogue outlines
├── CONFLICT_EVENTS.md             # Complete catalogue of retail situations and multi-perspective events
├── COMBAT_DESIGN.md               # POS terminal combat mechanics & Act I-II boss specs
├── STORY_SCRIPT_ACT1.md           # Act I interactive dialogue script and story nodes
└── README.md                      # Project introduction & setup
```


