package com.js_loop_erp.components.fragments

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.js_loop_erp.components.R
import com.js_loop_erp.components.adapter.AppPermissionLayoutAdapter
import com.js_loop_erp.components.data_class.AppPermissionMenuItem
import com.js_loop_erp.components.databinding.AppPermissionDialogBinding
import androidx.core.net.toUri

class AppPermissionDialogFragment: DialogFragment(), IOnAppPermissionItemClickListener  {
    var binding_ : AppPermissionDialogBinding? = null
    private val binding get() = binding_!!


    private lateinit var recyclerViewMenuGrid: RecyclerView

    private lateinit var adapter: AppPermissionLayoutAdapter

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.DialogThemeNoTitle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding_ = AppPermissionDialogBinding.inflate(inflater, container, false)

        recyclerViewMenuGrid = binding_!!.menuAppPermissionRecyclerView
        recyclerViewMenuGrid.layoutManager = LinearLayoutManager(context)

        val mainMenuItems = listOf(
            AppPermissionMenuItem(
                id = 1,
                menuItemName = "Location",
                menuItemImage = R.drawable.baseline_add_24,
                isPermissionGranted = requireContext()!!.isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION),//false,
                manifestPermission = Manifest.permission.ACCESS_FINE_LOCATION,
                manifestPermissionRequiredInThisSdk = {true}
            ),
            AppPermissionMenuItem(
                id = 2,
                menuItemName = "Media",
                menuItemImage = R.drawable.baseline_add_24,
                isPermissionGranted = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU  && requireContext()!!.isPermissionGranted(Manifest.permission.READ_MEDIA_IMAGES)){true} else {false},//false,
                manifestPermission = Manifest.permission.READ_MEDIA_IMAGES,
                manifestPermissionRequiredInThisSdk = { Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU }
            ),
            AppPermissionMenuItem(
                id = 3,
                menuItemName = "Storage",
                menuItemImage = R.drawable.baseline_add_24,
                isPermissionGranted = if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && requireContext()!!.isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)){true} else {false} ,//false,
                manifestPermission = Manifest.permission.READ_EXTERNAL_STORAGE,
                manifestPermissionRequiredInThisSdk =  {Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU}
            ),
            AppPermissionMenuItem(
                id = 4,
                menuItemName = "Disable Battery Optimization",
                menuItemImage = R.drawable.baseline_add_24,
                isPermissionGranted = requireContext()!!.isPermissionGranted(Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS), //false,
                manifestPermission = Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                manifestPermissionRequiredInThisSdk =  {true}//Build.VERSION.SDK_INT > Build.VERSION_CODES.M
            ),
            AppPermissionMenuItem(
                id = 5,
                menuItemName = "Notifications",
                menuItemImage = R.drawable.baseline_add_24,
                isPermissionGranted = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && requireContext()!!.isPermissionGranted(Manifest.permission.POST_NOTIFICATIONS)){true} else {false} ,//false,
                manifestPermission = Manifest.permission.POST_NOTIFICATIONS,
                manifestPermissionRequiredInThisSdk =  {Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU}
            ),
            AppPermissionMenuItem(
                id = 6,
                menuItemName = "Camera",
                menuItemImage = R.drawable.baseline_add_24,
                isPermissionGranted = requireContext()!!.isPermissionGranted(Manifest.permission.CAMERA),
                manifestPermission = Manifest.permission.CAMERA,
                manifestPermissionRequiredInThisSdk =  {true}
            ),
            AppPermissionMenuItem(
                id = 7,
                menuItemName = "Microphone",
                menuItemImage = R.drawable.baseline_add_24,
                isPermissionGranted = false,
                manifestPermissionRequiredInThisSdk =  {false}
            ),
            AppPermissionMenuItem(
                id = 8,
                menuItemName = "Bluetooth",
                menuItemImage = R.drawable.baseline_add_24,
                isPermissionGranted = false,
                manifestPermissionRequiredInThisSdk = {false}//{Build.VERSION.SDK_INT >= Build.VERSION_CODES.S}
            ),
            AppPermissionMenuItem(
                id = 9,
                menuItemName = "Phone Access",
                menuItemImage = R.drawable.baseline_add_24,
                isPermissionGranted = false,
                manifestPermissionRequiredInThisSdk =  {false}
            )

        )
        //mainMenuItems.filter { !it.isMenuItemAdmin }, this@MainActivity
        adapter = AppPermissionLayoutAdapter(mainMenuItems.filter {it.manifestPermissionRequiredInThisSdk()}, this@AppPermissionDialogFragment )
        recyclerViewMenuGrid.adapter = adapter

        return binding_!!.root
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation // Set your animation style here
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()

        val mainMenuItems = listOf(
            AppPermissionMenuItem(
                id = 1,
                menuItemName = "Location",
                menuItemImage = R.drawable.baseline_add_24,
                isPermissionGranted = requireContext()!!.isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION),//false,
                manifestPermission = Manifest.permission.ACCESS_FINE_LOCATION,
                manifestPermissionRequiredInThisSdk = {true}
            ),
            AppPermissionMenuItem(
                id = 2,
                menuItemName = "Media",
                menuItemImage = R.drawable.baseline_add_24,
                isPermissionGranted = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU  && requireContext()!!.isPermissionGranted(Manifest.permission.READ_MEDIA_IMAGES)){true} else {false},//false,
                manifestPermission = Manifest.permission.READ_MEDIA_IMAGES,
                manifestPermissionRequiredInThisSdk = { Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU }
            ),
            AppPermissionMenuItem(
                id = 3,
                menuItemName = "Storage",
                menuItemImage = R.drawable.baseline_add_24,
                isPermissionGranted = if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && requireContext()!!.isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)){true} else {false} ,//false,
                manifestPermission = Manifest.permission.READ_EXTERNAL_STORAGE,
                manifestPermissionRequiredInThisSdk =  {Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU}
            ),
            AppPermissionMenuItem(
                id = 4,
                menuItemName = "Disable Battery Optimization",
                menuItemImage = R.drawable.baseline_add_24,
                isPermissionGranted = requireContext()!!.isPermissionGranted(Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS), //false,
                manifestPermission = Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                manifestPermissionRequiredInThisSdk =  {true}//Build.VERSION.SDK_INT > Build.VERSION_CODES.M
            ),
            AppPermissionMenuItem(
                id = 5,
                menuItemName = "Notifications",
                menuItemImage = R.drawable.baseline_add_24,
                isPermissionGranted = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && requireContext()!!.isPermissionGranted(Manifest.permission.POST_NOTIFICATIONS)){true} else {false} ,//false,
                manifestPermission = Manifest.permission.POST_NOTIFICATIONS,
                manifestPermissionRequiredInThisSdk =  {Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU}
            ),
            AppPermissionMenuItem(
                id = 6,
                menuItemName = "Camera",
                menuItemImage = R.drawable.baseline_add_24,
                isPermissionGranted = requireContext()!!.isPermissionGranted(Manifest.permission.CAMERA),
                manifestPermission = Manifest.permission.CAMERA,
                manifestPermissionRequiredInThisSdk =  {true}
            ),
            AppPermissionMenuItem(
                id = 7,
                menuItemName = "Microphone",
                menuItemImage = R.drawable.baseline_add_24,
                isPermissionGranted = false,
                manifestPermissionRequiredInThisSdk =  {false}
            ),
            AppPermissionMenuItem(
                id = 8,
                menuItemName = "Bluetooth",
                menuItemImage = R.drawable.baseline_add_24,
                isPermissionGranted = false,
                manifestPermissionRequiredInThisSdk = {false}//{Build.VERSION.SDK_INT >= Build.VERSION_CODES.S}
            ),
            AppPermissionMenuItem(
                id = 9,
                menuItemName = "Phone Access",
                menuItemImage = R.drawable.baseline_add_24,
                isPermissionGranted = false,
                manifestPermissionRequiredInThisSdk =  {false}
            )

        )
        //mainMenuItems.filter { !it.isMenuItemAdmin }, this@MainActivity
        adapter = AppPermissionLayoutAdapter(mainMenuItems.filter {it.manifestPermissionRequiredInThisSdk()}, this@AppPermissionDialogFragment )
        recyclerViewMenuGrid.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    override fun onItemClick(item: AppPermissionMenuItem) {
        val context = requireContext()
        val packageName = context.packageName
        val permission = item.manifestPermission

        if (permission != null) {

            if(item.menuItemName == "Disable Battery Optimization"){
                val context = requireContext()
                val packageName = "com.js_loop_erp.rumatec_vetcare_erp" //context.packageName
                val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager

                if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
                    val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                        data = "package:$packageName".toUri()
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(context, "Battery optimization already disabled", Toast.LENGTH_SHORT).show()
                }

                return
            }

            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                // Redirect to app settings for permission
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:$packageName")
                }
                startActivity(intent)
            } else {
                // Permission granted - do nothing or handle accordingly
            }
        } else {
            when (item.menuItemName) {
                "Disable Battery Optimization" -> {
                    val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
                    val isIgnoring = powerManager.isIgnoringBatteryOptimizations(packageName)

                    if (!isIgnoring) {
                        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                            data = Uri.parse("package:$packageName")
                        }
                        startActivity(intent)
                    } else {
                        Toast.makeText(context, "Battery optimization already disabled", Toast.LENGTH_SHORT).show()
                    }
                }

                "Media" -> {
                    // Redirect to app settings so user can allow media access manually
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.parse("package:$packageName")
                    }
                    startActivity(intent)
                }

                // Add other special cases if needed
            }
        }
    }



    fun Context.isPermissionGranted(permission: String) = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED


    companion object{
        val REQUEST_CODE = Math.random().toInt()
    }

}

interface IOnAppPermissionItemClickListener {
    fun onItemClick(item: AppPermissionMenuItem)
}