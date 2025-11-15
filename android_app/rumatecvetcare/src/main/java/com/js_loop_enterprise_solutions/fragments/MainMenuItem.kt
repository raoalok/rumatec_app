package com.js_loop_enterprise_solutions.fragments

import com.js_loop_erp.rumatec_vetcare_erp.R

data class MainMenuItem(
    val id: Int? = 0,
    val menuItemName: String? = "",
    val menuItemImage: Int = com.js_loop_erp.components.R.drawable.baseline_add_24,
    val isMenuItemAdmin: Boolean = false,
){}
