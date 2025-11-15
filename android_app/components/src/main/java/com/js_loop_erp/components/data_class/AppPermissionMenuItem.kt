package com.js_loop_erp.components.data_class

import android.Manifest

data class AppPermissionMenuItem(
    val id: Int? = 0,
    val menuItemName: String? = "",
    val menuItemImage: Int = com.js_loop_erp.components.R.drawable.baseline_add_24,
    val isPermissionGranted: Boolean = false,
    val manifestPermission: String? = null,
    val manifestPermissionRequiredInThisSdk: () -> Boolean = { false }
){}
