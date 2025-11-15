# Material 2 Migration & App Permission Fix — Task List

This file lists **all changes, steps, and deliverables** required to migrate the Kotlin Android app to **Material Components (Material 2)** and to diagnose & fix the **App Permission** page that currently does not open. Use this as a task assignment for an agent or developer.

---

## Summary / Objectives
- Migrate UI across the app to **Material Components (MDC / Material 2)**.
- Create a single source of truth for tokens (colors, typography, spacing, shapes).
- Replace/standardize core components with MDC equivalents (MaterialToolbar, MaterialButton, TextInputLayout, MaterialCardView, etc.).
- Diagnose and fix the **App Permission** page so it opens reliably.
- Produce PR-ready artifacts, screenshots, QA checklist, and rollback plan.

---

## Acceptance Criteria
- App builds and runs without UI/theme crashes.
- Updated screens use MDC components and tokens; no remaining hard-coded color/dimen usage in updated screens.
- App Permission screen opens and permission flow works (repro steps documented).
- Accessibility checks: text scaling, touch target sizes (≥48dp), `contentDescription` on icons, reasonable contrast.
- Deliverables: theme files, refactored screen code, changelog, before/after screenshots, PR description.

---

## Files to inspect (start here)
- `app/src/main/res/values/styles.xml`
- `app/src/main/res/values/themes.xml` (if present)
- `app/src/main/res/values/colors.xml`
- `app/src/main/res/values/dimens.xml`
- `app/src/main/res/layout/*.xml` (all layouts)
- `app/src/main/java/.../ui/*` (activities/fragments/composables)
- `app/src/main/AndroidManifest.xml`
- `app/src/main/res/navigation/*.xml` (Navigation graph)
- `app/src/main/res/drawable/*`, `res/mipmap/*`

Additionally locate the App Permission page files:
- Layout/composable file (e.g., `res/layout/activity_app_permissions.xml` or `AppPermissionFragment.kt`)
- Activity/Fragment class handling the page (`AppPermissionActivity.kt` / `AppPermissionFragment.kt`)
- Navigation graph entry or call site where the page is launched

---

## High-level Tasks (assignable units)

### 1. Repo survey & baseline (Task owner: Agent A)
- Run these commands in repo root and attach results:
  - `rg "#[0-9A-Fa-f]{3,8}" -g '!**/build/**'`
  - `rg '\b\d+(dp|sp)\b' -g '!**/build/**'`
  - `rg 'setBackgroundColor|Color\.parseColor|Theme\.|TextInputLayout' -g '!**/build/**'`
- Produce a short report listing top files with hard-coded values and the App Permission page files discovered.

Deliverable: `survey_report.md` listing files and number of hits.

### 2. Add / verify Material Components dependency (Agent B)
- Open `app/build.gradle` and ensure:
  ```gradle
  implementation 'com.google.android.material:material:1.9.0' // or project-stable version
  ```
- Sync project and resolve any version conflicts. Document the final version used.

Deliverable: `build_gradle_change.patch` and note in changelog.

### 3. Create design tokens (Agent C)
- Add / update these resource files (res/values):
  - `colors.xml` — primary, primaryDark, accent, background, surface, error, onPrimary, onSurface.
  - `dimens.xml` — spacing_xs/sm/md/lg, touch_min_size (48dp), appbar_height, corner sizes.
  - `styles.xml` — define `AppTheme` inheriting from `Theme.MaterialComponents.*` and `TextAppearance` entries.
  - Optional: `themes.xml` for day/night variants.

Deliverable: `res/values/colors.xml`, `res/values/dimens.xml`, `res/values/styles.xml` (files ready to apply).

### 4. Implement Material Components theme & manifest updates (Agent C)
- Ensure `AndroidManifest.xml` activities use the new `@style/AppTheme` where necessary.
- If some activities require a theme overlay (dialogs, permission screen), add `android:theme` in manifest.

Deliverable: `AndroidManifest.xml` patch snippet and note of changed activities.

### 5. Migrate core components (Agent D)
- Replace/standardize the following across layouts:
  - `Toolbar` → `com.google.android.material.appbar.MaterialToolbar`
  - `Button` → `com.google.android.material.button.MaterialButton`
  - `CardView` → `com.google.android.material.card.MaterialCardView` (or style CardView)
  - Input fields → `TextInputLayout` + `TextInputEditText`
  - Switches/Checkboxes → `MaterialSwitch`, `MaterialCheckBox`
- Replace hard-coded paddings/colors with `@dimen` / `@color` tokens.

Deliverable: Example converted layout file(s) and a short migration note per file.

### 6. Fix App Permission page (Agent E) — Highest priority
Steps to perform (in order):
1. Identify call site (navigation action or Intent) that should open the App Permission page.
2. Check navigation graph: ensure destination exists and `id` matches call site.
3. Verify Activity/Fragment class exists and name/package match the call.
4. Launch the app and reproduce crash; capture logs with:
   - `adb logcat -c` (clear)
   - reproduce open action
   - `adb logcat -d | rg Exception -n --context 5` (capture stack trace)
5. Common fixes to attempt:
   - If inflation fails with `InflateException`/`Resources.NotFoundException` referencing Material attributes, ensure activity theme inherits from `Theme.MaterialComponents`.
   - If `NullPointerException` in the page’s `onCreateView`/`onViewCreated`, fix view binding or IDs.
   - If navigation action missing, add an action to navigation graph or update the call site.
6. Update the layout to use Material components and tokens.
7. Replace deprecated permission code with `registerForActivityResult` where appropriate.

Deliverable: `app_permission_fix.patch`, `app_permission_debug.log`, reproduction steps, and explanation of root cause.

### 7. Update representative screens (Agent D)
- Convert at least these screens to MDC: Main Activity, Trip list, Trip detail, Map/tracking, Add/Edit Trip, Settings, App Permission.
- For each screen: provide before/after screenshots and file diffs.

Deliverable: collection under `design/screenshots/before` and `design/screenshots/after` and file diffs.

### 8. Accessibility & QA (Agent F)
- Ensure `contentDescription` is present for non-decorative icons.
- Verify text scales (use Developer Options or `adb shell settings put system font_scale 1.2`).
- Verify touch targets >= 48dp.
- Contrast checks: sample tests for primary text on backgrounds.

Deliverable: `accessibility_report.md` with checks and fixes.

### 9. Testing & verification (Agent G)
- Build and run: `./gradlew assembleDebug` and `./gradlew installDebug`.
- Manual test checklist to validate:
  - Launch app, navigate to Trip list, open trip detail.
  - Start/stop tracking on Map screen.
  - Create / edit trip forms and observe TextInputLayout behavior.
  - Open App Permission page and trigger permission requests.
- Capture `adb` screenshots for before/after:
  - `adb shell screencap -p /sdcard/screen.png`
  - `adb pull /sdcard/screen.png design/screenshots/<name>.png`

Deliverable: `testing_report.md` and screenshot folder.

### 10. Changelog & PR (Agent H)
- Prepare one or more PRs grouped by scope (theme tokens, App Permission fix, component refactors — keep PRs small).
- Fill in PR template (see below).
- Suggested commit message format:
  - `chore(ui): add Material Components dependency and base AppTheme`
  - `feat(ui): migrate TextInput to TextInputLayout in AddTrip screen`
  - `fix(permission): fix App Permission page inflation and navigation`

Deliverable: `PR_TEMPLATE.md`, PR descriptions, and branch names suggestions.

### 11. Rollback plan (Agent H)
- If the migration causes regressions, revert PRs in this order:
  1. Component refactors (smallest scope PRs)
  2. App Permission fix (only if it causes crashes unrelated to theme)
  3. Theme tokens / base theme
- Keep each PR small so reverts are isolated.

Deliverable: `rollback_plan.md` with exact `git revert` commands for each PR.

---

## PR Template (copy into PR description)

**Title:** `ui(mdc): migrate to Material Components & fix App Permission page`

**Summary:**
- Centralized design tokens in `res/values/`.
- Added Material Components dependency: `com.google.android.material:material:<version>`.
- Migrated [list of screens] to MDC components.
- Fixed App Permission page: root cause and fix summary.

**Files changed:**
- List of changed files (auto-generate from commit)

**How to test:**
1. Build & install: `./gradlew installDebug`.
2. Run through manual test checklist in `testing_report.md`.
3. Verify App Permission page opens and permission flow requests work.

**Screenshots:**
- `design/screenshots/before/*`
- `design/screenshots/after/*`

**Notes:**
- No business logic changes except small adjustments to permission flow.
- Accessibility checks added.

---

## Useful commands & snippets
- Find hard-coded colors: `rg "#[0-9A-Fa-f]{3,8}" -g '!**/build/**'`
- Find dp/sp uses: `rg "\b\d+(dp|sp)\b" -g '!**/build/**'`
- Clear and capture logcat:
  - `adb logcat -c`
  - reproduce
  - `adb logcat -d | rg Exception -n --context 5`
- Capture screenshot:
  - `adb shell screencap -p /sdcard/screen.png`
  - `adb pull /sdcard/screen.png design/screenshots/<name>.png`
- Build and install:
  - `./gradlew assembleDebug`
  - `./gradlew installDebug`

---

## Assignment template (for manager)
- **Agent A (Survey & Baseline):** due 2 days
- **Agent B (Dependency & Gradle):** due 1 day
- **Agent C (Tokens & Theme):** due 2 days
- **Agent D (Components & Screens):** due 4 days
- **Agent E (App Permission debug & fix):** due 1–2 days (highest priority)
- **Agent F (Accessibility & QA):** due 2 days
- **Agent G (Testing & Verification):** due ongoing during changes
- **Agent H (Changelog & PRs):** prepare PRs and handle merges

Each agent should produce a short status note and attach their deliverables as described above.

---

## Notes & constraints
- Avoid changing business logic unless strictly necessary to open the App Permission page.
- Keep PRs small and focused to ease review and rollback.
- If the codebase mixes Compose and View UI, prefer theming via `res/values` tokens and keep visual parity.

---

_End of task list._
