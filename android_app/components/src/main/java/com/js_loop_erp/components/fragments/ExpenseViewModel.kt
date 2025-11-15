package com.js_loop_erp.components.fragments

data class ExpenseViewModel(
    val id: Int? = 0,
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
    val expenseFiles: List<ExpenseFile> = listOf()
)

data class ExpenseFile(
    val id: Int? = 0,
    val url: String? = "",
    val uploadedBy: String? = ""
)


data class ExpenseItemUpdated(
    val id: Int,
    val userId: Int,
    val description: String,
    val amount: String,
    val remark: String? = null,
    val createdAt: String,
    val createdBy: String,
    val updatedAt: String? = null,
    val updatedBy: String? = null,
    val deletedAt: String? = null,
    val deletedBy: String? = null,
    val approveAt: String? = null,
    val approveBy: String? = null,
    val approveRemark: String? = null,
    val rejectAt: String? = null,
    val rejectBy: String? = null,
    val rejectRemark: String? = null
)
