# Design Tokens Documentation

## Overview
This document describes the design token system for the Rumatec Vetcare Android application. Design tokens provide a single source of truth for visual design attributes, ensuring consistency across the entire application.

## Color Palette

### Primary Colors
The primary color represents the Rumatec Vetcare brand identity.

- **`primary`** (#011B10) - Main brand color, dark green used for primary actions and branding
- **`primary_dark`** (#000F08) - Darker variant for shadows and depth
- **`primary_light`** (#023A20) - Lighter variant for hover states
- **`rumatec_vetcare_green`** - Alias for `primary` (backward compatibility)

**Usage**: AppBar, primary buttons, key branding elements

### Secondary Colors
Secondary colors complement the primary palette.

- **`secondary`** (#0288D1) - Blue, used for secondary actions
- **`secondary_dark`** (#005B9F) - Dark blue
- **`secondary_light`** (#81D4FA) - Light blue

**Usage**: Secondary buttons, links, informational elements

### Accent Colors
Accent colors provide visual interest and highlights.

- **`accent`** (#8DFC6F) - Bright green for accents and highlights
- **`accent_light`** (#BFF6CE) - Light green for backgrounds

**Usage**: FAB (Floating Action Button), highlights, success indicators

### Semantic Colors
These colors convey meaning and state.

- **`success`** (#056420) - Green for successful operations
- **`warning`** (#DD763F) - Orange for warnings
- **`error`** (#D72822) - Red for errors and validation failures
- **`info`** (#0288D1) - Blue for informational messages

**Usage**: Alerts, toasts, validation messages, status indicators

### Neutral Colors
Grayscale palette for text, backgrounds, and dividers.

- **`black`** (#000000) - Pure black
- **`white`** (#FFFFFF) - Pure white
- **`gray_dark`** (#4D4D4D) - Dark gray
- **`gray`** (#BEBABA) - Medium gray
- **`gray_light`** (#E0E0E0) - Light gray
- **`gray_lighter`** (#F5F5F5) - Very light gray

**Usage**: Text, dividers, disabled states, backgrounds

### Surface & Background Colors

- **`background`** (#FFFFFF) - Main app background
- **`surface`** (#FFFFFF) - Card and elevated component surfaces
- **`surface_dark`** (#F5F5F5) - Alternative surface for contrast

**Usage**: Backgrounds, cards, dialogs, sheets

### Text Colors

- **`text_primary`** (#000000) - High emphasis text (87% opacity conceptually)
- **`text_secondary`** (#4D4D4D) - Medium emphasis text (60% opacity)
- **`text_tertiary`** (#BEBABA) - Low emphasis/disabled text (38% opacity)
- **`text_on_primary`** (#FFFFFF) - Text on primary color backgrounds
- **`text_on_secondary`** (#FFFFFF) - Text on secondary color backgrounds
- **`text_on_accent`** (#000000) - Text on accent color backgrounds

**Usage**: All text elements based on their emphasis level

### Interactive State Colors

- **`ripple_light`** (#20000000) - 12% black for ripple effects
- **`ripple_dark`** (#40000000) - 25% black for ripple effects
- **`overlay_light`** (#20000000) - Light overlay
- **`overlay_dark`** (#66000000) - Dark overlay (40% black)

**Usage**: Touch feedback, overlays, modals

### Utility Colors

- **`transparent`** (#00000000) - Fully transparent
- **`divider`** (#E0E0E0) - Divider lines
- **`border`** (#BEBABA) - Border and stroke colors

## Typography Scale

### Text Sizes
Based on Material Design type scale:

- **`text_size_h1`** (32sp) - Display/Headline 1
- **`text_size_h2`** (24sp) - Display/Headline 2
- **`text_size_h3`** (20sp) - Headline 3
- **`text_size_h4`** (18sp) - Headline 4
- **`text_size_body1`** (16sp) - Body 1 - Primary body text
- **`text_size_body2`** (14sp) - Body 2 - Secondary body text
- **`text_size_caption`** (12sp) - Caption/Small text
- **`text_size_button`** (14sp) - Button text
- **`text_size_overline`** (10sp) - Overline/Labels

### Font Families
- **sans-serif** - Default system font for body text
- **sans-serif-medium** - Medium weight for emphasis (headings, buttons)

### Text Appearance Styles
Pre-defined text appearances in `themes.xml`:

- `TextAppearance.App.Headline1`
- `TextAppearance.App.Headline2`
- `TextAppearance.App.Headline3`
- `TextAppearance.App.Body1`
- `TextAppearance.App.Body2`
- `TextAppearance.App.Button`
- `TextAppearance.App.Caption`

## Spacing Scale

Based on 4dp grid system (Material Design):

- **`spacing_xs`** (4dp) - Extra small spacing
- **`spacing_sm`** (8dp) - Small spacing
- **`spacing_md`** (16dp) - Medium spacing (standard)
- **`spacing_lg`** (24dp) - Large spacing
- **`spacing_xl`** (32dp) - Extra large spacing
- **`spacing_xxl`** (48dp) - 2X large spacing

**Usage**: Margins, padding, gaps between elements

## Corner Radii

- **`corner_radius_none`** (0dp) - No rounding (sharp corners)
- **`corner_radius_sm`** (4dp) - Small radius for cards and chips
- **`corner_radius_md`** (8dp) - Medium radius for buttons
- **`corner_radius_lg`** (16dp) - Large radius for dialogs
- **`corner_radius_xl`** (24dp) - Extra large radius
- **`corner_radius_pill`** (999dp) - Full pill shape (completely rounded)

## Elevation (Shadows)

- **`elevation_none`** (0dp) - No elevation
- **`elevation_sm`** (2dp) - Small elevation for cards
- **`elevation_md`** (4dp) - Medium elevation for buttons
- **`elevation_lg`** (8dp) - Large elevation for FAB
- **`elevation_xl`** (16dp) - Extra large elevation for dialogs and modals

## Component Dimensions

### Touch Targets (Accessibility)
- **`touch_target_min`** (48dp) - Minimum touch target size (WCAG AA compliance)

### Icons
- **`icon_size_sm`** (16dp) - Small icons
- **`icon_size_md`** (24dp) - Medium icons (standard)
- **`icon_size_lg`** (32dp) - Large icons
- **`icon_size_xl`** (48dp) - Extra large icons

### Buttons
- **`button_height`** (48dp) - Standard button height
- **`button_height_sm`** (36dp) - Small button height
- **`button_padding_horizontal`** (16dp) - Horizontal padding
- **`button_padding_vertical`** (12dp) - Vertical padding

### Cards
- **`card_padding`** (16dp) - Standard card content padding

### List Items
- **`list_item_height`** (56dp) - Standard list item height
- **`list_item_padding`** (16dp) - List item padding

### App Components
- **`appbar_height`** (56dp) - AppBar/Toolbar height
- **`bottom_nav_height`** (56dp) - Bottom navigation height

### Dividers & Borders
- **`divider_height`** (1dp) - Divider line height
- **`stroke_width`** (2dp) - Standard border/stroke width

## Component Styles

All reusable component styles are defined in `component_styles.xml`. Use these instead of creating custom styles:

### Buttons
- `AppButton.Primary` - Primary filled button
- `AppButton.Secondary` - Secondary outlined button
- `AppButton.Text` - Text-only button
- `AppButton.Small` - Small variant

### Text Fields
- `AppTextInputLayout` - Outlined text field
- `AppTextInputLayout.Filled` - Filled text field
- `AppEditText` - Standard edit text

### Cards
- `AppCard` - Standard card
- `AppCard.Elevated` - Elevated card with more shadow
- `AppCard.Flat` - Flat card with border, no elevation

### Dialogs
- `AppDialog` - Standard dialog
- `AppDialog.Alert` - Alert dialog with action buttons

### Other Components
- `AppListItem` - List item style
- `AppChip` - Chip/tag style
- `AppDivider` - Divider line
- `AppFAB` - Floating action button
- `AppFAB.Mini` - Mini FAB
- `AppTabLayout` - Tab layout
- `AppSpinner` - Spinner/dropdown

## Theme Configuration

The main app theme is defined in `themes.xml`:

```xml
<style name="Theme.rumatec_vetcare" parent="Theme.MaterialComponents.Light.NoActionBar">
```

### Key Theme Attributes
- `colorPrimary` - Primary brand color
- `colorSecondary` - Secondary color
- `colorSurface` - Surface color for cards, sheets
- `colorError` - Error color for validation
- `android:statusBarColor` - Status bar color

## Accessibility Guidelines

### Color Contrast
All color combinations meet WCAG AA standards for normal text (4.5:1 contrast ratio):

- Text Primary on White: ✓ Pass
- Text Secondary on White: ✓ Pass
- White on Primary: ✓ Pass
- White on Secondary: ✓ Pass

### Touch Targets
- All interactive elements use minimum 48dp touch target
- Defined by `touch_target_min` token
- Applied via `android:minHeight` in component styles

### Content Descriptions
- All icon-only buttons require `android:contentDescription`
- Decorative images should use `android:contentDescription="@null"`

## Migration Guide

### From Hard-Coded Values to Tokens

**Before:**
```xml
<TextView
    android:textSize="16sp"
    android:textColor="#000000"
    android:padding="16dp" />
```

**After:**
```xml
<TextView
    android:textSize="@dimen/text_size_body1"
    android:textColor="@color/text_primary"
    android:padding="@dimen/spacing_md" />
```

### From Custom Styles to Component Styles

**Before:**
```xml
<Button
    style="@style/go_to_login_button_style" />
```

**After:**
```xml
<Button
    style="@style/AppButton.Primary" />
```

## Legacy Colors

The following legacy colors are maintained for backward compatibility but should not be used in new code:

- `purple_200`, `purple_500`, `purple_700`, `purple`
- `teal_200`, `teal_700`
- `green`, `green_dark`, `green_light`, `lightgreen`
- `startblue`, `endblue`
- Various `light_blue_*` colors
- `graylight`, `selectedColor`, `unselectedColor`
- `button`, `background_color`
- `black_overlay`, `system_red`, `transparent_60`

Gradually migrate these to the new semantic tokens.

## Future Enhancements

1. **Dark Theme Support**: Create `values-night/colors.xml` and `values-night/themes.xml`
2. **Dynamic Color (Material You)**: Consider Android 12+ dynamic color support
3. **Custom Font**: Import and use a custom brand font family
4. **Animation Tokens**: Define standard animation durations and curves
5. **Iconography**: Standardize icon set (Material Icons vs custom)

## Questions & Support

For questions about using these design tokens, contact the design system maintainers or refer to Material Design guidelines at https://material.io/design.

---

**Last Updated**: 2025-11-15
**Maintained By**: Development Team
**Version**: 1.0
