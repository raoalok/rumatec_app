# Design Consistency Migration Guide

**Date:** 2025-11-15  
**Status:** In Progress  
**Goal:** 100% consistent spacing, buttons, and cards across all screens

---

## ✅ Completed

### 1. Design Tokens Updated
**File:** `components/src/main/res/values/dimens.xml`

**Added Tokens:**
```xml
<!-- Card Dimensions -->
<dimen name="card_margin_horizontal">16dp</dimen>
<dimen name="card_margin_vertical">8dp</dimen>
<dimen name="card_padding_content">16dp</dimen>
<dimen name="card_corner_radius_standard">12dp</dimen>
<dimen name="card_elevation_standard">2dp</dimen>

<!-- Content Spacing -->
<dimen name="content_padding_horizontal">16dp</dimen>
<dimen name="content_padding_vertical">16dp</dimen>
<dimen name="content_margin_small">8dp</dimen>
<dimen name="content_margin_medium">16dp</dimen>
<dimen name="content_margin_large">24dp</dimen>
```

### 2. Component Styles Updated
**File:** `components/src/main/res/values/component_styles.xml`

**Updated Styles:**
- `AppCard` - Standardized with proper margins and padding
- `AppCard.Compact` - For grid layouts
- `AppButton.Icon` - For icon-only buttons
- `AppButton.Icon.Primary` - Primary colored icons
- `AppButton.Icon.Danger` - Delete/danger actions

### 3. Layouts Migrated

#### ✅ trip_plan_fragment_card_layout.xml
**Changes:**
- CardView → MaterialCardView
- Hard-coded margins (20dp, 5dp) → Design tokens
- Hard-coded padding (30dp, 5dp) → Design tokens
- Hard-coded colors (@color/black) → Semantic tokens (@color/text_primary)
- Hard-coded text sizes (22sp, 18sp) → Typography tokens
- Custom background → Theme surface color
- ImageView buttons → MaterialButton (better accessibility)
- Nested LinearLayouts → ConstraintLayout (better performance)

**Before:**
- 8dp corner radius
- 20dp/5dp margins
- 30dp internal padding
- Hard-coded colors
- 3 levels of nesting

**After:**
- 12dp corner radius (standard)
- 16dp/8dp margins (tokens)
- 16dp padding (token)
- Semantic colors
- 1 level of nesting

#### ✅ menu_recycler_view_card.xml
**Changes:**
- CardView → MaterialCardView
- Hard-coded margins (4dp) → AppCard.Compact style
- Hard-coded padding (8dp) → Design tokens (16dp)
- Hard-coded text size (16sp) → Typography token (text_size_body2)
- Hard-coded icon size (48dp) → Icon token (icon_size_xl)
- Added icon tint
- Added ripple effect

#### ✅ app_permission_card.xml
**Already Compliant** - Recently redesigned with Material 3

#### ✅ _template_standard_card.xml
**Created** - Reference template for future cards

---

## 🔄 In Progress

### Layouts to Migrate (Priority Order)

#### High Priority (User-Facing, High Traffic)
1. ⏳ `attendance_card_layout.xml`
2. ⏳ `leave_card_layout.xml`
3. ⏳ `activity_card_layout.xml`
4. ⏳ `trip_report_card_layout.xml`
5. ⏳ `expense_submit_fragment.xml`

#### Medium Priority (Frequently Used)
6. ⏳ `product_list_fragment_card_layout.xml`
7. ⏳ `inventory_card_layout.xml`
8. ⏳ `cnf_fragment_card_layout.xml`
9. ⏳ `sale_invoice_fragment_card_layout.xml`
10. ⏳ `menu_item_about_card.xml`

#### Lower Priority (Less Frequent)
11. ⏳ `tour_plan_add_doctor_card_layout.xml`
12. ⏳ `tour_plan_add_hospital_card_layout.xml`
13. ⏳ `leave_approve_reject_card_layout.xml`
14. ⏳ All other card layouts

---

## 📋 Migration Checklist

For each layout file, apply these changes:

### Step 1: Update Card Component
```xml
<!-- BEFORE -->
<androidx.cardview.widget.CardView
    android:layout_marginStart="20dp"
    android:layout_marginTop="5dp"
    android:layout_marginEnd="20dp"
    app:cardCornerRadius="8dp">

<!-- AFTER -->
<com.google.android.material.card.MaterialCardView
    style="@style/AppCard"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">
```

### Step 2: Replace Hard-Coded Spacing
```xml
<!-- BEFORE -->
android:layout_marginStart="30dp"
android:layout_marginTop="5dp"
android:padding="20dp"

<!-- AFTER -->
android:layout_marginStart="@dimen/content_padding_horizontal"
android:layout_marginTop="@dimen/spacing_xs"
android:padding="@dimen/content_padding_horizontal"
```

### Step 3: Replace Hard-Coded Colors
```xml
<!-- BEFORE -->
android:textColor="@color/black"
android:background="@drawable/background1"

<!-- AFTER -->
android:textColor="@color/text_primary"
android:background="@color/surface"
```

### Step 4: Replace Hard-Coded Text Sizes
```xml
<!-- BEFORE -->
android:textSize="22sp"  <!-- Title -->
android:textSize="18sp"  <!-- Body -->
android:textSize="14sp"  <!-- Small -->

<!-- AFTER -->
android:textSize="@dimen/text_size_h4"      <!-- Title -->
android:textSize="@dimen/text_size_body1"   <!-- Body -->
android:textSize="@dimen/text_size_body2"   <!-- Small -->
```

### Step 5: Replace ImageView Buttons
```xml
<!-- BEFORE -->
<androidx.appcompat.widget.AppCompatImageView
    android:id="@+id/edit_button"
    android:layout_width="wrap_content"
    android:layout_height="30dp"
    android:src="@drawable/baseline_edit_24" />

<!-- AFTER -->
<com.google.android.material.button.MaterialButton
    android:id="@+id/edit_button"
    style="@style/AppButton.Icon"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:contentDescription="@string/edit"
    app:icon="@drawable/baseline_edit_24" />
```

### Step 6: Optimize Layout Hierarchy
```xml
<!-- BEFORE: Nested LinearLayouts -->
<LinearLayout>
    <LinearLayout>
        <LinearLayout>
            <TextView />
        </LinearLayout>
    </LinearLayout>
</LinearLayout>

<!-- AFTER: ConstraintLayout -->
<androidx.constraintlayout.widget.ConstraintLayout>
    <TextView
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />
</androidx.constraintlayout.widget.ConstraintLayout>
```

---

## 🎯 Design Standards

### Spacing Scale (Use Only These)
- `4dp` - `@dimen/spacing_xs` - Tiny gaps
- `8dp` - `@dimen/spacing_sm` - Small spacing
- `16dp` - `@dimen/spacing_md` - Standard spacing
- `24dp` - `@dimen/spacing_lg` - Large spacing
- `32dp` - `@dimen/spacing_xl` - Extra large spacing
- `48dp` - `@dimen/spacing_xxl` - Huge spacing

### Typography Scale (Use Only These)
- `32sp` - `@dimen/text_size_h1` - Display/Headline 1
- `24sp` - `@dimen/text_size_h2` - Display/Headline 2
- `20sp` - `@dimen/text_size_h3` - Headline 3
- `18sp` - `@dimen/text_size_h4` - Headline 4
- `16sp` - `@dimen/text_size_body1` - Body 1 (primary)
- `14sp` - `@dimen/text_size_body2` - Body 2 (secondary)
- `12sp` - `@dimen/text_size_caption` - Caption/small text

### Color Tokens (Use Only These)
- `@color/text_primary` - High emphasis text
- `@color/text_secondary` - Medium emphasis text
- `@color/text_tertiary` - Low emphasis text
- `@color/primary` - Brand color
- `@color/secondary` - Secondary brand color
- `@color/success` - Success states
- `@color/warning` - Warning states
- `@color/error` - Error states
- `@color/surface` - Card/surface backgrounds
- `@color/background` - Page backgrounds
- `@color/divider` - Dividers and borders

### Card Standards
- **Component:** MaterialCardView
- **Style:** `@style/AppCard` (standard) or `@style/AppCard.Compact` (grid)
- **Corner Radius:** 12dp (automatic via style)
- **Elevation:** 2dp (automatic via style)
- **Margins:** 16dp horizontal, 8dp vertical (automatic via style)
- **Padding:** 16dp (automatic via style)

### Button Standards
- **Text Buttons:** `@style/AppButton.Primary` or `@style/AppButton.Secondary`
- **Icon Buttons:** `@style/AppButton.Icon`
- **Minimum Size:** 48dp (automatic via style)
- **Ripple Effect:** Automatic with MaterialButton

---

## 🔧 Tools & Commands

### Find Hard-Coded Values
```bash
# Find hard-coded margins/padding
rg "android:(layout_)?margin|android:padding" --type xml -g '!**/build/**'

# Find hard-coded colors
rg 'android:.*Color="@color/black"' --type xml -g '!**/build/**'

# Find hard-coded text sizes
rg 'android:textSize="[0-9]+sp"' --type xml -g '!**/build/**'

# Find CardView (should be MaterialCardView)
rg 'androidx\.cardview\.widget\.CardView' --type xml -g '!**/build/**'
```

### Verify Consistency
```bash
# Check all layouts use design tokens
rg '@dimen/(spacing|text_size|card_)' android_app/components/src/main/res/layout/

# Check all cards use MaterialCardView
rg 'MaterialCardView' android_app/components/src/main/res/layout/
```

---

## 📊 Progress Tracking

### Overall Progress: 5%

**Completed:** 4 layouts
**Total:** ~120 layouts
**Estimated Time:** 20-30 hours

### By Category:

| Category | Total | Completed | Progress |
|----------|-------|-----------|----------|
| Card Layouts | 50 | 3 | 6% |
| Form Layouts | 30 | 0 | 0% |
| Dialog Layouts | 20 | 1 | 5% |
| Fragment Layouts | 20 | 0 | 0% |

---

## 🎯 Success Criteria

### Visual Consistency
- ✅ All cards have 12dp corner radius
- ✅ All cards have 16dp/8dp margins
- ✅ All text uses typography scale
- ✅ All spacing uses 4dp grid
- ✅ All colors are semantic tokens

### Code Quality
- ✅ No hard-coded dimensions
- ✅ No hard-coded colors
- ✅ No hard-coded text sizes
- ✅ MaterialCardView everywhere
- ✅ MaterialButton for actions
- ✅ Optimized layout hierarchies

### Accessibility
- ✅ 48dp minimum touch targets
- ✅ Proper content descriptions
- ✅ WCAG AA color contrast
- ✅ Scalable text (sp units)

---

## 📝 Notes

### Breaking Changes
**None** - All changes are visual only, no API changes

### Testing Required
- Visual regression testing
- Accessibility testing (TalkBack)
- Different screen sizes
- Different font scales

### Rollback Plan
Each layout can be reverted individually via git:
```bash
git checkout HEAD~1 -- path/to/layout.xml
```

---

**Status:** Active Migration  
**Next Update:** After 10 more layouts migrated  
**Estimated Completion:** 2-3 days of focused work

