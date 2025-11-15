package com.js_loop_erp.components.fragments

data class PresentScheduleViewModel(
    val id: Int,
    val salesPersonId: Int,
    val productId: Int,
    val batch: String,
    val stock: Int,
    val mfgDate: String,
    val expDate: String,
    val product: String,
    val user: String,
    val hsn: String
) {
}
