package com.js_loop_erp.components.data_class

data class TourPlanUpdateCompanion(
    val id: Int? = 0,
    val type: String? = " ",
    val name: String? = " ",
    val division: String? = " ",
    val unitId: Int? = 0,
    val hsnId: Int? = 0,
    val rate: String? = "0.00",
    val minimumStock: String? = "0.00",
    val maximumStock: String? = "1.00",
    val packSize: Int? = 1,
    val packUnitId: Int? = 0,
    val remark: String? = " ",
    val partyId: Int? = 0,
    val createdAt: String? = " ",
    val createdBy: Int? = 0,
    val updatedAt: String? = " ",
    val updatedBy: Int? = 0,
    val deletedAt: String? = " ",
    val deletedBy: Int? = 0,
    val isActive: Boolean? = false,
    val unit: String? = " ",
    val packUnit: String? = " ",
    val hsn: String? = " ",
    val tax: Int? = 0,
    val party: String? = " ",
    val expDate: String? = " ",
    val mrp: String? = " ",
    var isSelected: Boolean? = false
) {

}