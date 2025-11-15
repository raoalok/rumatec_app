package com.js_loop_erp.components.fragments.daily_activity

data class ActivityUpdateModel(
    val id: Int = 0,
    val category: String = " ",
    val type: String = " ",
    val startDate: String = " ",
    val endDate: String = " ",
    val mode: String = " ",
    val kilometers: String = " ",
    val remark: String = " ",
    val createdAt: String = " ",
    val createdBy: String = " ",
    val updatedAt: String? = null,
    val updatedBy: String? = null,
    val deletedAt: String? = null,
    val deletedBy: String? = null,
    val approvedAt: String? = null,
    val approvedBy: String? = null,
    val rejectedAt: String? = null,
    val rejectedBy: String? = null,
    val actionRemark: String? = null
)
