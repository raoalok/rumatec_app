# Screenshots

This directory contains before and after screenshots documenting the UI consistency refactor.

## Directory Structure

- `before/` - Screenshots taken before the UI refactor
- `after/` - Screenshots taken after the UI refactor

## Required Screenshots

Please capture screenshots for the following screens in both `before/` and `after/` directories:

### Core Screens
1. **main_screen.png** - Main activity/home screen
2. **trip_list.png** - List of trips
3. **trip_detail.png** - Individual trip details
4. **trip_form.png** - Create/edit trip form
5. **map_tracking.png** - Map screen with active tracking
6. **login.png** - Login screen

### UI Components
7. **buttons.png** - Examples of button styles (primary, secondary, text)
8. **text_fields.png** - Examples of text input fields
9. **cards.png** - Examples of card components
10. **dialogs.png** - Example alert dialog
11. **bottom_nav.png** - Bottom navigation (if applicable)
12. **drawer_menu.png** - Navigation drawer/menu (if applicable)

### States
13. **error_state.png** - Example of error validation
14. **loading_state.png** - Example of loading indicator
15. **empty_state.png** - Example of empty state (if applicable)

## Capturing Screenshots

### Using ADB (Android Debug Bridge)

1. **Navigate to the screen** you want to capture on the device/emulator

2. **Capture screenshot**:
   ```bash
   adb shell screencap -p /sdcard/screenshot_name.png
   ```

3. **Pull screenshot to your computer**:
   ```bash
   # For 'before' screenshots
   adb pull /sdcard/screenshot_name.png android_app/design/screenshots/before/

   # For 'after' screenshots
   adb pull /sdcard/screenshot_name.png android_app/design/screenshots/after/
   ```

4. **Clean up device** (optional):
   ```bash
   adb shell rm /sdcard/screenshot_name.png
   ```

### Using Android Studio

1. Open **Device File Explorer** (View → Tool Windows → Device File Explorer)
2. Navigate to the screen in your app
3. Click the **camera icon** in the Android Studio toolbar
4. Save the screenshot to the appropriate directory

### Naming Convention

Use descriptive, lowercase names with underscores:
- ✓ Good: `trip_detail_before.png`, `login_form_after.png`
- ✗ Avoid: `Screenshot1.png`, `IMG_2024.png`

## Comparison

When documenting changes:

1. Place corresponding before/after screenshots side-by-side in the PR
2. Highlight key differences (colors, spacing, typography, etc.)
3. Note any functional changes (even if none are expected)

## Example Command Sequence

```bash
# Capture 'before' state (before applying changes)
adb shell screencap -p /sdcard/main.png
adb pull /sdcard/main.png android_app/design/screenshots/before/
adb shell rm /sdcard/main.png

# Apply changes, rebuild, reinstall

# Capture 'after' state
adb shell screencap -p /sdcard/main.png
adb pull /sdcard/main.png android_app/design/screenshots/after/
adb shell rm /sdcard/main.png
```

## Notes

- Ensure screenshots are captured at the same resolution for accurate comparison
- Use the same device/emulator for both before and after screenshots
- Capture screenshots in light mode (dark mode comparison optional)
- Include screenshots showing accessibility features if possible (large text, TalkBack, etc.)

---

**Status**: Screenshots pending - to be captured during manual testing phase
