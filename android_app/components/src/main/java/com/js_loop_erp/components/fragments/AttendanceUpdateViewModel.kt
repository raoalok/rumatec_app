package com.js_loop_erp.components.fragments

data class AttendanceUpdateViewModel(
    val id: Int? = 0,
    val salesPersonId: Int? = 0,
    val productId: Int? = 0,
    val batch: String? = "",
    val quantity: Int? = 0,
    val doctorId: Int? = 0,
    val person: String? = "",
    val remark: String? = "",
    val createdAt: String? = "",
    val createdBy: String? = "",
    val deletedAt: String? = null,
    val deletedBy: String? = null,
    val product: String? = ""
) {

}

