# Material 3 Migration — Task List & Implementation Guide

**Goal:** Migrate the Android app UI to **Material 3** (Compose Material3 and/or Material3-compatible View themes) — including components, theme, colors, spacing, typography, icons, buttons, cards, and layouts — and provide all artifacts, instructions and verification steps required for a safe rollout.

---

## Overview
This document describes an actionable migration plan to move an existing Kotlin Android application to Material 3. It covers dependency updates, theming, tokens, component migrations (Compose and View), icons, spacing scale, accessibility, testing, and PR/rollback guidance.

Target outcomes:
- Single source of truth for design tokens (colors, typography, shapes, spacing).
- App uses Material 3 components (Material 3 Compose + MaterialComponents for View where required).
- Consistent buttons, cards, spacing, and iconography across the app.
- Migration delivered in small, reviewable PRs with verification steps.

---

## Acceptance Criteria
- App builds and launches with no theme- or inflation-related crashes.
- Key screens (Main, Trip List, Trip Detail, Map/Tracking, Add/Edit Trip, Settings, App Permission) use Material 3 components or Material 3 styled wrappers.
- No hard-coded color/dimen values remain in migrated files; tokens are used instead.
- Buttons, cards, and spacing follow a single design system (tokens + components).
- Accessibility: content descriptions, text scaling, and touch targets >= 48dp.
- Provide before/after screenshots for all migrated screens.

---

## High-level Plan (phased)
1. **Survey & baseline** — locate theme files, layouts, Compose themes, and the App Permission page. Capture current screenshots and known crashes. (1–2 days)
2. **Add/verify dependencies** — include Material3 Compose library and latest Material Components if View screens remain. (0.5 day)
3. **Create design tokens** — colors, typography, shapes, spacing centralized in `ui/theme` (Compose) and `res/values` (View). (1 day)
4. **Create Material 3 Theme** — `Theme.kt` (Compose) and `themes.xml` (View) implementing Material 3 tokens and dynamic color support optionally. (1 day)
5. **Migrate core components** — AppBar, Buttons, Text Fields, Cards, Lists, Dialogs, Switches. Provide reusable composables and styled widgets. (2–4 days)
6. **Refactor screens** — migrate representative screens, starting with high-impact ones. (3–7 days)
7. **Fix App Permission page / navigation issues** — ensure permissions flow works under new theme. (Highest priority; 1–2 days)
8. **Accessibility, QA, and polish** — run checks, update assets, finalize PRs. (2–3 days)

---

## Dependencies
**app/build.gradle (module)** — add or verify:
```gradle
// Compose BOM / Compose Material3
implementation platform('androidx.compose:compose-bom:2025.01.00')
implementation 'androidx.compose.material3:material3'
implementation 'androidx.compose.material3:material3-window-size-class' // optional

// If using View-based Material Components (for non-Compose screens)
implementation 'com.google.android.material:material:1.9.0'
```
Pick library versions compatible with the project's Kotlin and Android Gradle Plugin versions.

---

## Design Tokens (single source of truth)
Place Compose tokens under `app/src/main/java/.../ui/theme/` and View tokens in `res/values/`.

### Compose (Kotlin)
Files: `Color.kt`, `Type.kt`, `Shape.kt`, `Spacing.kt`, `Theme.kt`.

**Color.kt (example)**
```kotlin
val md_theme_light_primary = Color(0xFF6750A4)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_secondary = Color(0xFF625B71)
val md_theme_light_background = Color(0xFFFFFBFE)
// ... add rest of MD3 tokens and dynamicColor fallback
```

**Spacing.kt**
```kotlin
object AppSpacing {
  val xs = 4.dp
  val sm = 8.dp
  val md = 16.dp
  val lg = 24.dp
  val xl = 32.dp
}
```

**Shape.kt**
```kotlin
val AppShapes = Shapes(
  small = RoundedCornerShape(8.dp),
  medium = RoundedCornerShape(12.dp),
  large = RoundedCornerShape(16.dp)
)
```

**Theme.kt**
Implement `MaterialTheme` using `MaterialTheme(colorScheme = ..., typography = ..., shapes = ... )` and expose `AppTheme` composable. Add dynamic color support for Android 12+.

### View (res/values)
Files: `colors.xml`, `dimens.xml`, `styles.xml`.
Provide color tokens that match Compose tokens and create a `themes.xml` inheriting from `Theme.MaterialComponents.DayNight`.

---

## Theming & Application setup
- For Compose-first apps: wrap the root Activity `setContent { AppTheme { NavHost(...) } }` so all composables use the theme.
- For mixed apps: ensure Activities hosting View layouts use a `Theme.MaterialComponents.*` theme and Compose-hosting activities use `MaterialTheme` values aligned with `res/values` tokens.

**Ensure:** Any layout referencing Material attributes is inflated with a MaterialComponents theme to avoid `InflateException`.

---

## Component Migration Guidance
**Buttons**
- Compose: use `Button`, `FilledTonalButton`, `OutlinedButton`, `IconButton` from Material3. Create `AppButton` wrapper for consistent paddings, corner radius, and minHeight.
- View: use `com.google.android.material.button.MaterialButton` with style overlay and `materialButtonStyle` in `styles.xml`.

**Top App Bar**
- Compose: `SmallTopAppBar`, `CenterAlignedTopAppBar` (Material3)
- View: `com.google.android.material.appbar.MaterialToolbar` with M3-like styling via theme overlays.

**Text fields**
- Compose: `OutlinedTextField` / `TextField` with label/placeholder and leading/trailing icons.
- View: `TextInputLayout` + `TextInputEditText` (Material Components) for improved label handling and error states.

**Cards**
- Compose: `Card` with `CardDefaults.cardElevation` and shapes from tokens. Create `AppCard` wrapper that applies padding and inner layout.
- View: `MaterialCardView` styled with `shapeAppearance` overlays and unified elevation, inner padding read from dimens.

**Lists / Item rows**
- Use `ListItem` (Compose Material3) or `MaterialComponents` list styles for View to maintain consistent leading/trailing icon sizing and paddings.

**Dialogs & Bottom Sheets**
- Use `MaterialDialog` patterns from Material3 (Compose) or `MaterialAlertDialogBuilder` (View) with theme overlays.

**Icons**
- Prefer Material Symbols (outlined/rounded/filled consistent style) — include as vector drawables or Compose `Icons` from `androidx.compose.material.icons` or custom vector resources.
- Use consistent stroke/weight style across icon set.

---

## Migration Steps (detailed)
1. **Survey repo**
   - Run searches for hard-coded colors/dimens, list top offenders.
   - Identify all activities/fragments/composables and capture current screenshots.

2. **Add dependencies & sync** (see above). Fix any Gradle conflicts.

3. **Create tokens & Theme** (Compose + View parity)
   - Implement `Color.kt`, `Spacing.kt`, `Shape.kt`, `Type.kt`, `Theme.kt`.
   - Create `res/values/colors.xml`, `dimens.xml`, `styles.xml` for View parity.

4. **Create reusable components (Compose)**
   - `AppButton`, `AppCard`, `AppTopBar`, `AppTextField`, `AppListItem` — each reads tokens from `AppTheme`.

5. **Migrate one screen at a time**
   - Start with low-risk screens (Settings, About). Then move to Trip List, Trip Detail, Map tracking.
   - For each screen: replace hard-coded paddings and components, run UI, take before/after screenshots.

6. **Fix runtime issues**
   - If inflation errors occur for View layouts, ensure the Activity theme inherits from `Theme.MaterialComponents`.
   - If navigation fails (e.g., App Permission page), inspect nav graph / manifest / Intent call site and fix route/class names.

7. **Ensure accessibility**
   - Add `contentDescription` to icons, confirm text scaling, and verify appropriate contrast ratios.

8. **QA & Performance checks**
   - Run `./gradlew lint` and fix Compose/Android Lint issues related to UI.
   - Profile for jank on heavy screens (Map + lists) and ensure default animations are not heavy.

9. **Create PRs**
   - Small, focused PRs: theme tokens, component library, App Permission fix, screen batches.
   - Include before/after screenshots and testing steps per PR.

---

## File & Code Snippets (ready-to-apply)
**Example Compose Theme.kt (skeleton)**
```kotlin
@Composable
fun AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (useDarkTheme) darkColorScheme(...) else lightColorScheme(...)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
```

**Example AppButton (Compose)**
```kotlin
@Composable
fun AppButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) { Text(text = text) }
}
```

**Example styles.xml (View) — partial**
```xml
<style name="AppTheme" parent="Theme.MaterialComponents.DayNight.NoActionBar">
    <item name="colorPrimary">@color/md_theme_light_primary</item>
    <item name="materialCardViewStyle">@style/Widget.App.MaterialCardView</item>
    <item name="materialButtonStyle">@style/Widget.App.MaterialButton</item>
</style>

<style name="Widget.App.MaterialButton" parent="Widget.MaterialComponents.Button">
    <item name="cornerRadius">12dp</item>
    <item name="android:minHeight">48dp</item>
</style>
```

---

## App Permission page — special notes
- Ensure that the Activity or Fragment hosting the App Permission UI uses a Material 3 compatible theme (for View screens: a MaterialComponents theme; for Compose screens: the `AppTheme` composable).
- If the App Permission page fails to open: capture `adb logcat` when reproducing and look for `InflateException`, `Resources.NotFoundException`, `NoClassDefFoundError`, or `NullPointerException` stack traces. Common root causes:
  - Layout references Material attributes but Activity theme is not MaterialComponents.
  - Navigation graph entry is missing or wrong id/route.
  - Class/package name mismatch on Intent/Fragment creation.

Include sample reproduction commands and sample `logcat` snippets in the PR when fixing.

---

## Testing & Verification
- Build & install: `./gradlew assembleDebug && ./gradlew installDebug`
- Lint: `./gradlew lint` and fix issues.
- Capture screenshots via `adb shell screencap -p /sdcard/screen.png` and `adb pull`.
- Manual smoke tests: navigation, create/edit trip, start/stop tracking, open App Permission page and trigger permission flow.
- Accessibility checks: font scaling, TalkBack navigation, contrast.

---

## PR Template (for each PR)
**Title:** `feat(ui): migrate <scope> to Material 3`

**Summary:**
- What changed
- Files changed (high level)
- How to test (step-by-step)
- Screenshots (before/after)

**Notes:** Any required follow-ups or risk areas.

---

## Rollback Plan
- Keep PRs small. If a PR causes regressions, revert the PR.
- For theme-wide regressions, revert theme PRs first, then component PRs.

---

## Estimated timeline (example)
- Survey & setup: 1–2 days
- Tokens & theme: 1 day
- Core components: 2–4 days
- Screen migration (batch): 1–2 weeks depending on app size
- QA & polish: 2–4 days

---

_End of document._