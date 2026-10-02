# Retail Inferno — Act I Interactive Script & Story Nodes

> **Setting:** MegaMart Store #1042 — Suburbia  
> **Characters:** Cheryl (HR Manager), Donna (15-Year Associate), Store Manager Henderson, The Player  
> **Format:** Formatted for direct mapping into `story/nodes.json` and `story/scenes.json`

---

## Scene 1: The Application Kiosk & Cheryl's Pre-Hire Assessment

**Background Asset:** `story/bg_hiring_kiosk.png`  
**Music Track:** `audio/ambient_sterile_hum.ogg`  
**Speaker:** Cheryl (HR Manager) — Preferred Voice ID: `en-us-x-sfg#female_cheerful`

### Node `node_app_001`: The Kiosk Screen
* **Narrator:** The flickering CRT screen of the MegaMart Application Kiosk blinks under harsh fluorescent light. A printed laminate sign taped to the bezel reads: *"APPLICANTS MUST COMPLETE ALL QUESTIONS HONESTLY. INTEGRITY IS OUR #1 VALUE."*
* **Cheryl:** *"Welcome to the MegaMart family portal, prospective associate! Before we can process your application for Complex #1042, our automated integrity algorithm needs to understand your core workplace instincts. Ten quick questions. No wrong answers! Well... technically there are, but let's have fun with it!"*
* **Choices:**
  - `[A]` *"Begin the pre-employment assessment."*
    - **Tag:** `#ENTHUSIASTIC`
    - **Voice Keyword:** `Begin` / `Start`
    - **Target:** `node_app_q1`
    - **Effect:** Engagement +5%

---

### Node `node_app_q1`: Integrity Assessment — Question 1 of 10
* **Cheryl:** *"Question 1: You observe a coworker pocketing a \$1.29 pack of strawberry chewing gum from an open box on the loading dock. How do you respond?"*
* **Choices:**
  - `[A]` *"Immediately notify Security Manager Clint and submit an anonymous loss prevention report."*
    - **Tags:** `#SYCOPHANT`
    - **Voice Keyword:** `Report` / `Security`
    - **Effect:** Ass-Kissing +10, Morale -5
    - **Next:** `node_app_q2`
  - `[B]` *"Mind your own business. You didn't see anything; it's a dollar-thirty."*
    - **Tags:** `#SLACKER`
    - **Voice Keyword:** `Ignore` / `Nothing`
    - **Effect:** Morale +5, Integrity Flag: `slack_tolerant`
    - **Next:** `node_app_q2`
  - `[C]` *"Ask the coworker if you can have a piece too."*
    - **Tags:** `#DEFIANT`
    - **Voice Keyword:** `Share` / `Piece`
    - **Effect:** Defiance +15, Cheryl Eyebrow Twitch
    - **Next:** `node_app_q2`

---

### Node `node_app_q2`: Attendance Philosophy — Question 2 of 10
* **Cheryl:** *"Question 2: You wake up at 5:00 AM with a 102-degree fever, uncontrollable shivering, and a sore throat. Your shift starts in 60 minutes. What is the proper procedure?"*
* **Choices:**
  - `[A]` *"Take three horse-pill decongestants, wrap a scarf around your neck, and report for duty. Teamwork requires sacrifice!"*
    - **Tags:** `#SYCOPHANT`, `#ENTHUSIASTIC`
    - **Voice Keyword:** `Work` / `Report`
    - **Effect:** Ass-Kissing +15, Productivity +5
    - **Next:** `node_app_q3`
  - `[B]` *"Call the automated attendance hotline, log the absence, and go back to sleep with your phone on silent."*
    - **Tags:** `#SLACKER`
    - **Voice Keyword:** `Call out` / `Sleep`
    - **Effect:** Morale +10, Attendance Points +1.0 (Projected)
    - **Next:** `node_app_q3`
  - `[C]` *"Show up to work, cough loudly near Store Manager Henderson's desk, and wait for him to send you home so you don't take an occurrence."*
    - **Tags:** `#DEFIANT`
    - **Voice Keyword:** `Cough` / `Henderson`
    - **Effect:** Tactical Genius +10, Defiance +10
    - **Next:** `node_app_q3`

---

### Node `node_app_q10`: Overtime & Loyalty — Question 10 of 10
* **Cheryl:** *"Final question! It is 11:00 PM on a Saturday. Your shift has officially ended, but five pallets of seasonal merchandise remain untouched in the aisle. Overtime is strictly prohibited by corporate budget. What do you do?"*
* **Choices:**
  - `[A]` *"Offer to clock out on the terminal first, then stay an extra 45 minutes off-the-clock to finish the pallets out of company pride!"*
    - **Tags:** `#SYCOPHANT`
    - **Voice Keyword:** `Volunteer` / `Stay`
    - **Auto-Play Forbidden:** `false`
    - **Effect:** Ass-Kissing +25, Cheryl Beam of Joy
    - **Next:** `node_app_offer`
  - `[B]` *"Clock out at 10:59 PM on the dot, walk briskly past the pallets without making eye contact, and exit the front sliding doors."*
    - **Tags:** `#SLACKER`
    - **Voice Keyword:** `Clock out` / `Leave`
    - **Effect:** Morale +15, Defiance +10
    - **Next:** `node_app_offer`
  - `[C]` *"Tell the Area Manager that the morning shift promised to finish them."*
    - **Tags:** `#DEFIANT`
    - **Voice Keyword:** `Blame` / `Morning`
    - **Effect:** Shift Dumping Skill +10
    - **Next:** `node_app_offer`

---

### Node `node_app_offer`: Automated Offer & Track Selection
* **Narrator:** The kiosk chime dings cheerfully. A thermal paper receipt prints from the slot: *"APPLICATION STATUS: CONDITIONALLY ACCEPTED. COMPATIBILITY RATING: 98.4%."*
* **Cheryl:** *"Wonderful! The algorithm loves your energy! Now, Complex #1042 has four critical staffing vacancies. Where does your retail calling lie?"*
* **Choices:**
  - `[A]` **Front End Cashier:** *"Put me on the registers. Fast hands, customer smiles, and 4011 bananas."*
    - **Voice Keyword:** `Cashier` / `Front end`
    - **Target:** `scene_day1_front_end`
    - **Sets Flag:** `track_front_end`
  - `[B]` **Sales Floor Stocker:** *"Give me the aisles. Pallet jacks, box cutters, and building towers of cereal."*
    - **Voice Keyword:** `Floor` / `Stock`
    - **Target:** `scene_day1_sales_floor`
    - **Sets Flag:** `track_sales_floor`
  - `[C]` **Backroom Unloader:** *"Send me to the loading dock. Heavy freight, loud music, and hydraulic balers."*
    - **Voice Keyword:** `Dock` / `Backroom`
    - **Target:** `scene_day1_backroom`
    - **Sets Flag:** `track_backroom`
  - `[D]` **Specialty Counter:** *"Electronics, Tech, and Fine Goods. Demonstrating gadgets and closing high-margin warranties."*
    - **Voice Keyword:** `Tech` / `Specialty`
    - **Target:** `scene_day1_specialty`
    - **Sets Flag:** `track_specialty`

---

## Scene 2: Day 1 — The Locker Room & Donna's Advice

**Background Asset:** `story/bg_locker_room.png`  
**Music Track:** `audio/ambient_fluorescent_dull.ogg`  
**Characters:** Donna (15-Year Associate), The Player

### Node `node_locker_001`: The Metal Lockers
* **Narrator:** The associate locker room smells of ozone, industrial floor wax, and stale instant coffee. Rows of dented battleship-gray lockers line the walls.
* **Donna:** A weathered woman in her late 50s sits on a metal bench, methodically filling in a Sunday newspaper crossword puzzle with a stubby pencil. Her company vest is weighed down with dozens of enamel anniversary pins.
* **Donna:** *(Without looking up)* *"Fresh meat. Locker 42 is yours. Don't use Locker 41; the latch sticks and Brenda kicks it when she's having one of her spells."*
* **Player Dialogue:**
* **Choices:**
  - `[A]` *"Thanks! I'm really excited to be here. Any tips for making a good impression on management?"*
    - **Tags:** `#ENTHUSIASTIC`, `#SYCOPHANT`
    - **Voice Keyword:** `Tips` / `Impression`
    - **Next:** `node_locker_donna_advice`
    - **Effect:** Morale +5
  - `[B]` *"How long have you been here? Those pins look like they have stories."*
    - **Tags:** `#SLACKER`
    - **Voice Keyword:** `Pins` / `Stories`
    - **Next:** `node_locker_donna_lore`
    - **Effect:** Donna Affinity +10
  - `[C]` *"Where do people hide when they need a quiet 20-minute break?"*
    - **Tags:** `#DEFIANT`
    - **Voice Keyword:** `Hide` / `Break`
    - **Next:** `node_locker_donna_secret`
    - **Effect:** Slacker IQ +10

---

### Node `node_locker_donna_advice`: Donna's Three Rules
* **Donna:** She lowers her pencil and looks over the rim of her tortoiseshell glasses.
* **Donna:** *"Three rules, kid. Remember them and you might survive your 90-day probationary period."*
* **Donna:** *"Rule one: Never run on the sales floor. If you run, managers think you're panicked, and customers think you know where the Worcestershire sauce is."*
* **Donna:** *"Rule two: If a piece of equipment works—a scanner, a tape gun, a pallet jack—you guard it with your life. The second you set it down to scratch your ear, someone from the morning crew will stash it behind the pet food."*
* **Donna:** *"Rule three: Store Manager Henderson's smile never reaches his eyes. When his left eyebrow twitches, someone's getting transferred to cart-pushing in a blizzard."*
* **Choices:**
  - `[A]` *"Got it. Head down, keep my scanner close."*
    - **Voice Keyword:** `Understood` / `Got it`
    - **Target:** `node_huddle_001`
    - **Effect:** Engagement +5

---

## Scene 3: The Morning Huddle — Store Manager Henderson

**Background Asset:** `story/bg_action_alley.png`  
**Music Track:** `audio/corporate_cheer_march.ogg`  
**Characters:** Store Manager Henderson, Donna, Brenda, Kyle, Derrick, Chad

### Node `node_huddle_001`: The Milk Crate Pulpit
* **Narrator:** At 7:00 AM sharp, forty associates stand in a loose, shuffling circle in Center Action Alley between the paper towel display and seasonal lawn furniture.
* **Henderson:** Store Manager Henderson strides into the center wearing a sharply ironed corporate polo and gleaming white orthopedic running shoes. He steps onto an overturned yellow milk crate, his Bluetooth earpiece blinking a rhythmic corporate blue.
* **Henderson:** *"Good morning, Complex #1042! Look at this energy! Smell that fresh freight! Today is not just another Wednesday. Today is Regional Efficiency Benchmark Day! The district vice president is watching our real-time sales velocity from regional headquarters!"*
* **Henderson:** *"Now! Before we hit the floor, let's hear the store cheer! I need to hear it from the registers all the way to the loading docks! Give me an M!"*
* **Narrator:** The circle of weary associates gives a ragged, half-hearted response: *"M..."*
* **Henderson:** *"I can't hear you! Let the customers in the parking lot know we're hungry! GIVE ME AN E!"*
* **Choices:**
  - `[A]` **Scream at the top of your lungs:** *"E! WOOOO! LET'S GO #1042!"*
    - **Tags:** `#SYCOPHANT`, `#ENTHUSIASTIC`
    - **Voice Keyword:** `Cheer` / `Scream`
    - **Effect:** Ass-Kissing +20, Morale -10, Henderson Notice: `favored_rookie`
    - **Next:** `node_huddle_cheer_loud`
  - `[B]` **Mumble quietly while staring at the linoleum floor tiles:** *"E..."*
    - **Tags:** `#SLACKER`
    - **Voice Keyword:** `Mumble` / `Quiet`
    - **Effect:** Morale +0, Henderson Glare
    - **Next:** `node_huddle_cheer_neutral`
  - `[C]` **Discreetly check your watch and take a sip from your concealed travel mug:**
    - **Tags:** `#DEFIANT`
    - **Voice Keyword:** `Watch` / `Coffee`
    - **Effect:** Defiance +15, Donna Smirk
    - **Next:** `node_huddle_cheer_rebel`

---

### Node `node_huddle_cheer_loud`: Henderson's Recognition
* **Henderson:** Henderson's eyes lock onto you. A wide, blinding grin spreads across his face as he points an index finger directly at your chest.
* **Henderson:** *"YES! That's what I'm talking about! That is pure, unadulterated #1042 spirit right there! Look at our new rookie, people! Taking the lead! Here—"*
* **Narrator:** Henderson reaches into his clipboard pouch and hands you a small laminated cardboard token: `ITEM ACQUIRED: Free 16oz Fountain Drink Coupon (Valid Mon-Thu Only)`.
* **Donna:** Donna mutters quietly from three feet away: *"Kiss-ass."* But she winks.
* **Henderson:** *"All right, team! Break out to your departments! Remember: Speed, Service, and Shrink-Prevention! LET'S MOVE!"*
* **Choices:**
  - `[A]` *"Head to your assigned station for Shift 1."*
    - **Voice Keyword:** `Station` / `Start`
    - **Target:** `scene_shift1_start`
    - **Effect:** First Shift Initiated

---

### Node `node_huddle_cheer_rebel`: The Cold Eye of Henderson
* **Narrator:** As you take a slow sip of coffee, Henderson's Bluetooth headset flashes. His eyes sweep the front row and land squarely on your travel mug.
* **Henderson:** The cheer finishes with a ragged cheer, and the crowd begins to disperse. Henderson steps down off the milk crate, his white sneakers squeaking sharply on the wax.
* **Henderson:** *"Associate. A word."*
* **Henderson:** He stands uncomfortably close, adjusting his silver corporate name pin.
* **Henderson:** *"Enthusiasm isn't optional at #1042. It's a key performance indicator. When we don't bring 100% to the morning huddle, the customer feels that deficit in the checkout lane. Let's make sure our energy is aligned tomorrow morning. Understand?"*
* **Choices:**
  - `[A]` *"Understood, Mr. Henderson. Won't happen again."*
    - **Tags:** `#SYCOPHANT`
    - **Voice Keyword:** `Understood` / `Sorry`
    - **Effect:** Ass-Kissing +5, Morale -10
    - **Target:** `scene_shift1_start`
  - `[B]` *"Just pacing myself for the 8-hour sprint, sir."*
    - **Tags:** `#DEFIANT`
    - **Voice Keyword:** `Pacing` / `Sprint`
    - **Effect:** Defiance +10, Henderson Suspicion +1
    - **Target:** `scene_shift1_start`
