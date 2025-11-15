package com.js_loop_erp.components.fragments

data class TripPlanEditViewModel(
    val id: Int? = 0,
    val name: String? = null,
    val expenseBy: Int? = 0,
    val description: String? = "",
    val amount: String? = "0.00",
    val createdAt: String? = "",
    val createdBy: Int? = 0,
    val deletedAt: String? = null,
    val deletedBy: Int? = null,
    val isApproved: Boolean? = null,
    val approvalRemark: String? = null,
    val approvedAt: String? = null,
    val approvedBy: Int? = null,
    val expenseFiles: List<TripPlanFile> = listOf()
)

data class TripPlanFile(
    val id: Int? = 0,
    val url: String? = "",
    val uploadedBy: String? = ""
)