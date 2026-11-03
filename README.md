<p align="center">
  <a href="https://ko-fi.com/todd0042">
    <img src="https://raw.githubusercontent.com/Todd0042/creator-assets/main/banners/todd_creator_banner_v1.0.png" alt="Support Todd on Ko-fi" width="100%" />
  </a>
</p>

---

# Retail Inferno (Working Title) — Project Plan & Design Notes

> **Repository:** `retail-inferno`  
> **Package ID (`applicationId`):** `com.voicerpg.retailinferno`  
> **Engine Base:** `voice-rpg-engine` (Hands-free voice-commanded JRPG architecture)

---

## 1. Title Research & Brand Verification

A web search confirmed that **"Retail Hell"** was released on Steam (May 2026 by LEAP Game Studios) as a convenience store survival horror simulation.

### Selected Unique Working Title: **Retail Inferno**
- **Distinctive & Available:** "Retail Inferno" is completely clear of existing mobile and Steam releases.
- **Tone:** Evokes the multi-layered "circles of hell" structure (Dante's Inferno mapped onto department store floors / mega-retail departments).

### Alternative Title Options
If we want to tweak the title during planning, here are pre-cleared alternatives:
1. **Closing Shift Inferno**
2. **Infinite Shift**
3. **Black Friday Inferno**
4. **Purgatory Mart**

---

## 2. High-Level Game Concept

*Retail Inferno* is a voice-controlled satirical corporate dungeon-crawler / JRPG. Instead of ancient ruins and dragons, you are an underpaid retail clerk battling through the corporate hierarchy and customer chaos to clock out.

### The Thematic Translation:
| Classic JRPG Element | *Retail Inferno* Equivalent |
|----------------------|-----------------------------|
| **Dungeons / Floors** | Store Departments (Aisle 4: Cereal Void, Floor 3: Electronics Abyss, Customer Service Purgatory, Executive Penthouse) |
| **Classes / Roles** | Cashier (DPS / Rapid Scan), Stocker (Tank / Heavy Lift), Customer Service Lead (Debuff / Karen Tamer), Floor Supervisor (Support / Break Scheduler) |
| **Spells / Incantations** | Corporate Buzzwords, Intercom Pages, Policy Quotes ("Clean up on Aisle 3!", "Check the Back Room!", "Manager Override!") |
| **Enemies** | "Entitled Karen", "Coupon Hoarder", "Phantom Shoplifter", "District Manager", "Loss Prevention Sentinel" |
| **Currency / Economy** | Store Credit / Overtime Hours / Employee Discount Tokens |

---

## 3. Project Configuration & Identifiers

The standalone engine copy has been established with separate identifiers:

- **Directory:** `/home/todd/Documents/GitHub/retail-inferno`
- **Root Project Name:** `Retail-Inferno` (`settings.gradle.kts`)
- **Android `applicationId`:** `com.voicerpg.retailinferno` (`app/build.gradle.kts`)
- **App String:** `Retail Inferno` (`app/src/main/res/values/strings.xml`)
- **Git Repo:** Initialized locally (`git init`) on `master`/`main`

---

## 4. Next Phase Roadmap (When Ready)

1. **Manifest & Story Setup:** Customize `manifest.json`, define initial opening scene (The Morning Huddle).
2. **Enemy & Class Design:** Design corporate/retail themed archetypes, chants, and voice keywords.
3. **Audio & TTS Voice Tuning:** Configure narrator and NPC voices for customer/manager dialogue.

---

<p align="center">
  <a href="https://ko-fi.com/todd0042">
    <img src="https://raw.githubusercontent.com/Todd0042/creator-assets/main/banners/todd_creator_banner_v2.0.png" alt="Support Todd on Ko-fi" width="100%" />
  </a>
</p>

