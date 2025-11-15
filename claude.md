# UI Consistency Task — Kotlin Location Tracking & Trip App

## Overview
This document describes the task for making the **user interface consistent** across a Kotlin-based location tracking and trip organizing Android application. It is written as a clear, actionable prompt/instruction set suitable for a developer or a Claude-style assistant to execute.

---

## Branding Update Task
Before proceeding with UI consistency work, ensure all legacy branding references are updated:

**Find and replace the following references:**
- `panav_biotech` → `rumatec_vetcare`
- `panav` → `rumatec`
- `biotech` → `vetcare`

**Search locations:**
- All source code files (`.kt`, `.java`)
- Resource files (`strings.xml`, `colors.xml`, etc.)
- Package names and directory structures
- Asset files and drawable resources
- Configuration files (`build.gradle`, `AndroidManifest.xml`)
- Documentation and comments

**Helpful commands:**
```bash
# Search for panav_biotech references
rg "panav_biotech" -g '!**/build/**'

# Search for panav references
rg "panav" -i -g '!**/build/**'

# Search for biotech references
rg "biotech" -i -g '!**/build/**'
```

---

## Goal
Review the codebase and refactor UI styles/components so the entire application has a single, consistent appearance and consistent interaction patterns. No feature changes unless strictly necessary to enable consistent UI behavior.

---

## Input files (look for these in the repo)
- `app/src/main/res/values/styles.xml`, `themes.xml`, or any theme files.
- Compose theme files (e.g., `Theme.kt`, `AppTheme.kt`) if the app uses Jetpack Compose.
- Styles and dimensions: `colors.xml`, `dimens.xml`, `typography.xml` (or equivalents).
- Layouts: `layout/*.xml` (activities/fragments), or `ui/*` composables if Compose is used.
- Drawables and icons: `drawable/*`, `mipmap/*`.
- Any existing design tokens or style helper classes.
- Screens to inspect: Main Activity, Trip list, Trip detail, Map screen/tracking screen, Add/Edit Trip screen, Settings, Login/Register, Notifications, and any custom dialogs.

> If the repository contains a `README.md` or `design/` folder with visual references, use those as a secondary reference.

---

## Acceptance Criteria
Each of the items below must be satisfied before the task is considered done:

1. **Design tokens:** There is a single source-of-truth for commonly used values: colors, typography (font sizes/styles), spacing, corner radii, and elevations.
2. **No hard-coded styles:** Replace hard-coded color/size values found in layouts or code with tokens from the centralized theme file(s).
3. **Component consistency:** All core components (AppBar, Buttons, Text Fields, Cards, FAB, Bottom Nav, Dialogs, List Items) use the same styles across screens and consistent states: default, pressed, disabled, focused, and error.
4. **Navigation & layout:** Navigation components and insets (status bar, bottom nav, safe area) behave consistently across screens.
5. **Icons & images:** Icons are consistent (same stroke weight/visual style) and are vectorized where practical. Icon tinting follows theme tokens.
6. **Accessibility:** Text scales correctly with system font size; color contrast meets WCAG AA for regular text; touch targets are at least 48dp where applicable; content descriptions provided for non-decorative icons.
7. **Screenshots:** Provide before/after screenshots for each modified screen.
8. **PR & Changelog:** Produce a PR that is limited in scope (UI/style refactors), includes a changelog, and demonstrates no functional regressions in critical flows.

---

## Deliverables
- `claude.md` (this file — task spec)
- A consolidated theme/design tokens file (e.g., `Theme.kt` or `themes.xml`) with comments explaining each token.
- Reusable UI components or composables for core elements (buttons, fields, list items, cards).
- Updated layouts/composables referencing the centralized tokens.
- A short changelog (Markdown) listing files changed and rationale.
- Before & after screenshots (PNG) for every screen modified, saved under `design/screenshots/before` and `design/screenshots/after`.
- A PR description template filled with the summary of changes and testing notes.

---

## Step-by-step Implementation Checklist

1. **Project survey**
   - Run a quick code search for hard-coded hex colors (e.g., `#` patterns), hard-coded `sp`/`dp` numbers and duplicated drawables.
   - Identify all theme/style files and list them.

2. **Create design tokens**
   - Define color palette: primary, primaryVariant, secondary, background, surface, error, onPrimary, onSecondary, onBackground, onSurface.
   - Define typography scale: H1, H2, H3, body1, body2, caption, button.
   - Define spacing scale: spacing_xs, spacing_sm, spacing_md, spacing_lg.
   - Define corner radii and elevation tokens.
   - Place tokens in `colors.xml` + `dimens.xml` + `styles.xml` or `Theme.kt` for Compose.

3. **Refactor components**
   - Replace custom, duplicated buttons with a single `AppButton` or standardized Material Button styles.
   - Standardize AppBar/Toolbar via a single layout/composable.
   - Create shared `TextField` style (error, focused, disabled states).
   - Consolidate list item layout into a single reusable component.

4. **Replace hard-coded values**
   - Systematically update layouts/composables to reference tokens instead of literal values.

5. **Icons & Assets**
   - Replace mismatched icons with a single icon set (material or curated vector set).
   - Ensure tinting uses theme tokens.

6. **Accessibility fixes**
   - Add `contentDescription` to icons that require screen reader support.
   - Check contrast ratios between text and background; adjust tokens as necessary.
   - Ensure minimum touch target sizes.

7. **Testing & QA**
   - Run the app locally and test critical flows: start/stop tracking, create/edit trip, view trip details, login flow.
   - Take before/after screenshots using `adb` or Android Studio screen capture.
   - Run `./gradlew lint` and fix obvious warnings.

8. **Documentation & PR**
   - Create a changelog: list of modified files with short rationales.
   - Attach before/after screenshots to the PR.
   - Include instructions on how to verify changes locally.

---

## Example PR Template (fill when opening PR)

**Title:** UI: Consolidate theme & standardize UI components

**Summary:**
- Centralized design tokens in `Theme.kt` and `colors.xml`.
- Replaced multiple button styles with `AppButton`.
- Consolidated list item layout.
- Replaced hard-coded colors and dimensions with theme tokens.

**Before/After Screenshots:**
- `design/screenshots/before/<screen>.png`
- `design/screenshots/after/<screen>.png`

**Testing performed:**
- Manual smoke test of: tracking flow, create/edit trip, login.
- Ran `./gradlew lint` and fixed style warnings.

**Notes for reviewer:**
- This PR contains only UI/style changes. No business logic was altered.

---

## Helpful Commands & Tips
- Search for hex colors and dp/sp usage:
  - `rg "#[0-9A-Fa-f]{3,8}" -g '!**/build/**'`
  - `rg "\b\d+(dp|sp)\b" -g '!**/build/**'`
- Run lint: `./gradlew lint` or `./gradlew ktlintCheck` if ktlint is configured.
- Capture screenshots with adb:
  - `adb shell screencap -p /sdcard/screen.png`
  - `adb pull /sdcard/screen.png design/screenshots/before/`

---

## Notes & Constraints
- Avoid changing app behavior or business logic. If a functional change is required to enable consistent UI (rare), document it clearly and isolate it in its own small commit with explanatory notes.
- If the app mixes View system and Jetpack Compose heavily, prioritize centralizing tokens in whichever system is used for most screens. Provide a short migration note for the other system.

---

## Timeline Suggestion (optional)
- Survey & tokens: 1–2 days.
- Core components refactor: 2–4 days.
- Replace usages & QA: 2–3 days.
- Screenshots, docs, PR: 1 day.

(Adjust as needed — this is only guidance.)

---

## Contact / Handover
- Add a brief README `design/TOKENS.md` summarizing the decisions for colors/typography so future contributors follow the tokens.


---

_End of file._
