# Design Tokens Documentation

## Overview
This document describes the design token system for the Rumatec VetCare Android application. Design tokens are the centralized source of truth for all visual design decisions including colors, typography, spacing, and component dimensions.

---

## Purpose
Design tokens enable:
- **Consistency**: Single source of truth for design values
- **Maintainability**: Change values in one place, apply everywhere
- **Scalability**: Easy to extend with new tokens
- **Theming**: Support for light/dark modes
- **Accessibility**: Standardized touch targets and contrast ratios

---

## Token Categories

### 1. Colors (`values/colors.xml`)

#### Primary Colors
Brand colors for primary actions and key UI elements.

| Token Name | Value | Usage |
|------------|-------|-------|
| `color_primary` | `#011B10` | Primary brand color (Panav Biotech Green) |
| `color_primary_dark` | `#003300` | Darker shade for emphasis |
| `color_primary_light` | `#A5D6A7` | Lighter shade for backgrounds |
| `color_primary_variant` | `#056420` | Variant for selected states |

**Usage Example:**
```xml
<Button
    android:backgroundTint="@color/color_primary"
    android:textColor="@color/color_on_primary"/>
```

#### Secondary Colors
Complementary colors for secondary actions.

| Token Name | Value | Usage |
|------------|-------|-------|
| `color_secondary` | `#0288D1` | Secondary brand color (Blue) |
| `color_secondary_dark` | `#005B9F` | Darker shade |
| `color_secondary_light` | `#81D4FA` | Lighter shade |
| `color_secondary_variant` | `#FF039BE5` | Variant |

#### Text Colors
Semantic text colors for different hierarchies.

| Token Name | Value | Usage |
|------------|-------|-------|
| `color_text_primary` | `#FF000000` | Primary text (black) |
| `color_text_secondary` | `#BEBABA` | Secondary/hint text (grey) |
| `color_text_disabled` | `#9EC3D2EC` | Disabled text |
| `color_on_primary` | `#FFFFFFFF` | Text on primary color backgrounds |
| `color_on_secondary` | `#FFFFFFFF` | Text on secondary color backgrounds |
| `color_on_background` | `#FF000000` | Text on background surfaces |

#### State Colors
Colors for different UI states.

| Token Name | Value | Usage |
|------------|-------|-------|
| `color_error` | `#F44336` | Error messages, validation |
| `color_success` | `#8DFC6F` | Success messages |
| `color_warning` | `#DD763F` | Warning messages |
| `color_info` | `#0288D1` | Informational messages |

#### UI Element Colors

| Token Name | Value | Usage |
|------------|-------|-------|
| `color_border` | `#BEBABA` | Border lines |
| `color_divider` | `#E0E0E0` | Divider lines |
| `color_selected` | `#056420` | Selected state |
| `color_unselected` | `#FFFFFF` | Unselected state |
| `color_overlay_dark` | `#66000000` | Dark overlay (40% opacity) |
| `color_overlay_light` | `#4A000000` | Light overlay (29% opacity) |

---

### 2. Spacing (`values/dimens.xml`)

#### Spacing Scale
Consistent spacing values based on 8dp grid system.

| Token Name | Value | Usage |
|------------|-------|-------|
| `spacing_none` | `0dp` | No spacing |
| `spacing_xxs` | `2dp` | Extra extra small |
| `spacing_xs` | `4dp` | Extra small (tight spacing) |
| `spacing_sm` | `8dp` | Small (1 grid unit) |
| `spacing_md` | `16dp` | Medium (2 grid units) - most common |
| `spacing_lg` | `24dp` | Large (3 grid units) |
| `spacing_xl` | `32dp` | Extra large (4 grid units) |
| `spacing_xxl` | `48dp` | Extra extra large (6 grid units) |
| `spacing_xxxl` | `64dp` | Extra extra extra large (8 grid units) |

**Usage Example:**
```xml
<LinearLayout
    android:padding="@dimen/spacing_md"
    android:layout_margin="@dimen/spacing_lg">
```

#### Component-Specific Spacing

| Token Name | Value | Usage |
|------------|-------|-------|
| `spacing_card_padding` | `24dp` | Internal padding for cards |
| `spacing_card_margin` | `20dp` | External margin for cards |
| `spacing_list_item_padding` | `16dp` | Padding for list items |
| `spacing_button_padding_horizontal` | `40dp` | Horizontal padding for buttons |
| `spacing_button_padding_vertical` | `20dp` | Vertical padding for buttons |
| `spacing_input_padding` | `8dp` | Padding for input fields |

---

### 3. Typography (`values/dimens.xml`)

Type scale for consistent text sizing.

| Token Name | Value | Usage |
|------------|-------|-------|
| `text_size_h1` | `36sp` | Page titles, hero text |
| `text_size_h2` | `28sp` | Section headers |
| `text_size_h3` | `24sp` | Subsection headers |
| `text_size_h4` | `22sp` | Card titles |
| `text_size_h5` | `20sp` | Toolbar titles |
| `text_size_h6` | `18sp` | Button text, small headers |
| `text_size_body1` | `16sp` | Primary body text |
| `text_size_body2` | `14sp` | Secondary body text |
| `text_size_caption` | `12sp` | Captions, helper text |
| `text_size_small` | `10sp` | Very small text (use sparingly) |

**Usage Example:**
```xml
<TextView
    style="@style/AppText.H1"
    android:text="Welcome"/>

<TextView
    android:textSize="@dimen/text_size_body1"
    android:text="Description"/>
```

---

### 4. Component Dimensions

#### Icon Sizes

| Token Name | Value | Usage |
|------------|-------|-------|
| `icon_size_small` | `16dp` | Small inline icons |
| `icon_size_medium` | `24dp` | Standard icons |
| `icon_size_large` | `32dp` | Large icons |
| `icon_size_xlarge` | `48dp` | Extra large icons |
| `icon_size_xxlarge` | `70dp` | Hero icons, avatars |

#### Corner Radii

| Token Name | Value | Usage |
|------------|-------|-------|
| `corner_radius_none` | `0dp` | Square corners |
| `corner_radius_sm` | `4dp` | Slightly rounded |
| `corner_radius_md` | `8dp` | Medium rounded (cards) |
| `corner_radius_lg` | `16dp` | Large rounded |
| `corner_radius_xl` | `20dp` | Extra large (buttons) |
| `corner_radius_xxl` | `30dp` | Extra extra large (dialogs, inputs) |
| `corner_radius_round` | `50dp` | Fully rounded (pills) |

#### Elevation

| Token Name | Value | Usage |
|------------|-------|-------|
| `elevation_none` | `0dp` | Flat, no shadow |
| `elevation_sm` | `2dp` | Subtle elevation |
| `elevation_md` | `4dp` | Standard elevation (cards) |
| `elevation_lg` | `8dp` | Raised elevation |
| `elevation_xl` | `12dp` | High elevation |
| `elevation_xxl` | `20dp` | Maximum elevation (dialogs) |

#### Component Heights

| Token Name | Value | Usage |
|------------|-------|-------|
| `height_button` | `60dp` | Standard button height |
| `height_input_field` | `50dp` | Input field height |
| `height_toolbar` | `56dp` | Toolbar/AppBar height |
| `height_bottom_nav` | `56dp` | Bottom navigation height |
| `height_list_item` | `72dp` | List item height |
| `height_card_small` | `145dp` | Small card height |

#### Border Widths

| Token Name | Value | Usage |
|------------|-------|-------|
| `border_width_thin` | `1dp` | Thin borders |
| `border_width_medium` | `2dp` | Standard borders |
| `border_width_thick` | `4dp` | Thick emphasis borders |

---

### 5. Touch Targets (Accessibility)

| Token Name | Value | Usage |
|------------|-------|-------|
| `touch_target_min` | `48dp` | Minimum touch target (WCAG) |
| `touch_target_comfortable` | `56dp` | Comfortable touch target |

**Important**: All interactive elements (buttons, icons, list items) should meet the minimum 48dp touch target requirement.

---

## Component Styles

### Using Standardized Styles

The app provides pre-built styles that use design tokens. Always prefer these over custom styling.

#### Button Styles

```xml
<!-- Primary button -->
<Button
    style="@style/AppButton.Primary"
    android:text="Submit"/>

<!-- Secondary button -->
<Button
    style="@style/AppButton.Secondary"
    android:text="Cancel"/>

<!-- Outlined button -->
<Button
    style="@style/AppButton.Outlined"
    android:text="Learn More"/>
```

#### Card Styles

```xml
<!-- Standard card -->
<androidx.cardview.widget.CardView
    style="@style/AppCard">
    <!-- Content -->
</androidx.cardview.widget.CardView>

<!-- Elevated card -->
<androidx.cardview.widget.CardView
    style="@style/AppCard.Elevated">
    <!-- Content -->
</androidx.cardview.widget.CardView>
```

#### Text Styles

```xml
<TextView
    style="@style/AppText.H1"
    android:text="Title"/>

<TextView
    style="@style/AppText.Body1"
    android:text="Description"/>

<TextView
    style="@style/AppText.Caption"
    android:text="Helper text"/>
```

#### EditText Styles

```xml
<EditText
    style="@style/AppEditText"
    android:hint="Enter name"/>

<com.google.android.material.textfield.TextInputLayout
    style="@style/AppTextInputLayout">
    <com.google.android.material.textfield.TextInputEditText
        android:hint="Email"/>
</com.google.android.material.textfield.TextInputLayout>
```

---

## Migration Guidelines

### Step 1: Identify Hard-Coded Values
Look for patterns like:
- Color hex codes: `#FFFFFF`, `#000000`
- Fixed dimensions: `24dp`, `16sp`
- Direct color names: `@color/white`, `@color/black`

### Step 2: Replace with Tokens

**Before:**
```xml
<TextView
    android:textColor="#000000"
    android:textSize="18sp"
    android:padding="16dp"/>
```

**After:**
```xml
<TextView
    style="@style/AppText.H6"
    android:padding="@dimen/spacing_md"/>
```

### Step 3: Use Component Styles

Instead of manually styling each element, use pre-defined component styles:

**Before:**
```xml
<Button
    android:layout_height="60dp"
    android:textSize="18sp"
    android:textColor="#FFFFFF"
    android:backgroundTint="#011B10"
    app:cornerRadius="20dp"
    android:padding="40dp"/>
```

**After:**
```xml
<Button
    style="@style/AppButton.Primary"
    android:text="Login"/>
```

---

## Best Practices

### DO ✓
- Use semantic color tokens (`color_primary`, `color_text_primary`)
- Use spacing tokens from the scale (`spacing_md`, `spacing_lg`)
- Use component styles when available (`@style/AppButton`)
- Ensure minimum 48dp touch targets for interactive elements
- Add `contentDescription` for accessibility

### DON'T ✗
- Hard-code hex colors (`#000000`)
- Hard-code dimensions (`24dp`, `16sp`)
- Create custom styles when standard ones exist
- Use arbitrary spacing values not in the scale
- Use `wrap_content` height for buttons (use token heights)

---

## Accessibility Checklist

When using design tokens, ensure:

1. **Color Contrast**: Text meets WCAG AA standards (4.5:1 for normal text)
2. **Touch Targets**: All interactive elements ≥ 48dp
3. **Content Descriptions**: All icons and images have `contentDescription`
4. **Text Scaling**: Use `sp` for text, `dp` for layout (tokens handle this)
5. **Focus Indicators**: Interactive elements show clear focus states

---

## Future Enhancements

Planned token additions:
- Animation duration tokens
- Shadow/elevation color tokens
- Expanded color palette for illustrations
- Component-specific tokens for complex widgets

---

## Quick Reference

### Most Common Tokens

**Colors:**
- Primary: `@color/color_primary`
- Text: `@color/color_text_primary`
- Background: `@color/color_background`

**Spacing:**
- Small: `@dimen/spacing_sm` (8dp)
- Medium: `@dimen/spacing_md` (16dp)
- Large: `@dimen/spacing_lg` (24dp)

**Text Sizes:**
- Title: `@dimen/text_size_h3` (24sp)
- Body: `@dimen/text_size_body1` (16sp)
- Caption: `@dimen/text_size_caption` (12sp)

**Components:**
- Button: `@style/AppButton.Primary`
- Card: `@style/AppCard`
- Text: `@style/AppText.H1`

---

## File Locations

- **Colors**: `components/src/main/res/values/colors.xml`
- **Dimensions**: `components/src/main/res/values/dimens.xml`
- **Styles**: `components/src/main/res/values/styles.xml`
- **Theme**: `components/src/main/res/values/themes.xml`
- **Dark Theme**: `components/src/main/res/values-night/themes.xml`

---

## Support

For questions or to propose new tokens, contact the development team or create an issue in the repository.

---

_Last updated: 2025-11-15_
_Version: 1.0_
