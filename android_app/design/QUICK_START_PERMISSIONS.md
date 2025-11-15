# Quick Start - Using the Redesigned Permission Page

## For Developers

### What Changed?

The permission page layouts have been modernized with Material 3 design. Here's what you need to know:

---

## 🚀 Quick Setup

### 1. Update Your RecyclerView Setup

**Change this:**
```kotlin
binding.menuAppPermissionRecyclerView.apply {
    layoutManager = GridLayoutManager(context, 3)
    adapter = permissionAdapter
}
```

**To this:**
```kotlin
binding.menuAppPermissionRecyclerView.apply {
    layoutManager = LinearLayoutManager(context)
    adapter = permissionAdapter
}
```

### 2. Update ViewHolder (if needed)

The card layout IDs remain the same, but you may want to use the new chevron icon:

```kotlin
class PermissionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val icon: ImageView = view.findViewById(R.id.item_icon)
    val title: TextView = view.findViewById(R.id.item_title)
    val statusIcon: ImageView = view.findViewById(R.id.permission_status_icon)
    
    fun bind(permission: Permission) {
        title.text = permission.name
        icon.setImageResource(permission.iconRes)
        
        // Update status icon based on permission state
        statusIcon.setImageResource(
            if (permission.isGranted) R.drawable.ic_check
            else R.drawable.ic_chevron_right
        )
    }
}
```

---

## 📝 Available Layouts

### 1. Main Permission Page
**File:** `app_permission_dialog.xml`  
**Use for:** Full-screen permission management page

```kotlin
setContentView(R.layout.app_permission_dialog)
```

### 2. Permission Card
**File:** `app_permission_card.xml`  
**Use for:** Individual permission items in RecyclerView

```kotlin
// In your adapter
override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = LayoutInflater.from(parent.context)
        .inflate(R.layout.app_permission_card, parent, false)
    return ViewHolder(view)
}
```

### 3. Permission Menu Item
**File:** `app_permission_menu_item_card.xml`  
**Use for:** Navigation item to permission page (e.g., in settings)

```kotlin
// In your settings/menu layout
<include layout="@layout/app_permission_menu_item_card" />
```

---

## 🎨 Customization

### Change Card Colors

Use theme attributes for easy customization:

```xml
<!-- In your theme -->
<item name="colorSurface">@color/your_surface_color</item>
<item name="colorPrimary">@color/your_primary_color</item>
```

### Adjust Spacing

All spacing uses design tokens, so you can adjust globally:

```xml
<!-- In dimens.xml -->
<dimen name="spacing_md">20dp</dimen> <!-- Change from 16dp to 20dp -->
```

### Custom Icons

Replace the default icons:

```kotlin
// In your adapter
holder.icon.setImageResource(R.drawable.your_custom_icon)
holder.icon.imageTintList = ColorStateList.valueOf(
    ContextCompat.getColor(context, R.color.your_color)
)
```

---

## 🔧 Common Tasks

### Add Click Listeners

```kotlin
holder.itemView.setOnClickListener {
    onPermissionClick(permission)
}
```

### Show Permission Status

```kotlin
fun updatePermissionStatus(holder: ViewHolder, isGranted: Boolean) {
    holder.statusIcon.setImageResource(
        if (isGranted) R.drawable.ic_check_circle
        else R.drawable.ic_warning
    )
    holder.statusIcon.imageTintList = ColorStateList.valueOf(
        ContextCompat.getColor(
            holder.itemView.context,
            if (isGranted) R.color.success else R.color.warning
        )
    )
}
```

### Handle Permission Requests

```kotlin
private fun requestPermission(permission: String) {
    if (ContextCompat.checkSelfPermission(this, permission) 
        != PackageManager.PERMISSION_GRANTED) {
        
        ActivityCompat.requestPermissions(
            this,
            arrayOf(permission),
            PERMISSION_REQUEST_CODE
        )
    }
}

override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    
    if (requestCode == PERMISSION_REQUEST_CODE) {
        // Update UI
        adapter.notifyDataSetChanged()
    }
}
```

---

## 📱 Testing Checklist

- [ ] Test on small screens (< 360dp width)
- [ ] Test on large screens (tablets)
- [ ] Test with TalkBack enabled
- [ ] Test touch targets (should be easy to tap)
- [ ] Test scroll behavior
- [ ] Test with different permission states
- [ ] Test dark theme (if implemented)
- [ ] Test with long permission names
- [ ] Test with many permissions (20+)

---

## 🐛 Troubleshooting

### Cards not showing?
Check that your adapter is properly set up and data is being passed.

### Icons not displaying?
Ensure drawable resources exist and are referenced correctly.

### Layout looks wrong?
Verify you're using the correct layout file and RecyclerView is set to LinearLayoutManager.

### Touch targets too small?
All cards should have minimum 48dp height. Check your data isn't overriding minHeight.

### Spacing looks off?
Ensure you're using the design token dimensions, not hardcoded values.

---

## 📚 Resources

- **Design Tokens:** `values/dimens.xml`
- **Component Styles:** `values/component_styles.xml`
- **Colors:** `values/colors.xml`
- **Strings:** `values/strings.xml`
- **Full Documentation:** `design/APP_PERMISSION_REDESIGN.md`

---

## 💡 Tips

1. **Use Design Tokens:** Always use `@dimen/spacing_*` instead of hardcoded dp values
2. **Consistent Icons:** Use 24dp for small icons, 48dp for large icons
3. **Accessibility:** Always set contentDescription for ImageViews
4. **Touch Feedback:** Cards already have ripple effects built-in
5. **Performance:** LinearLayoutManager is more efficient than GridLayoutManager for this use case

---

## 🎯 Example Implementation

```kotlin
class PermissionActivity : AppCompatActivity() {
    
    private lateinit var binding: AppPermissionDialogBinding
    private lateinit var adapter: PermissionAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AppPermissionDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupRecyclerView()
        loadPermissions()
    }
    
    private fun setupRecyclerView() {
        adapter = PermissionAdapter { permission ->
            handlePermissionClick(permission)
        }
        
        binding.menuAppPermissionRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@PermissionActivity)
            adapter = this@PermissionActivity.adapter
            setHasFixedSize(true)
        }
    }
    
    private fun loadPermissions() {
        val permissions = listOf(
            Permission("Camera", R.drawable.ic_camera, Manifest.permission.CAMERA),
            Permission("Location", R.drawable.ic_location, Manifest.permission.ACCESS_FINE_LOCATION),
            Permission("Storage", R.drawable.ic_storage, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            // Add more permissions...
        )
        adapter.submitList(permissions)
    }
    
    private fun handlePermissionClick(permission: Permission) {
        if (!permission.isGranted) {
            requestPermission(permission.manifestPermission)
        } else {
            // Show permission details or settings
            showPermissionDetails(permission)
        }
    }
}
```

---

**Need help?** Check the full documentation in `APP_PERMISSION_REDESIGN.md`

