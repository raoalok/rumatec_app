package com.js_loop_erp.components.fragments.access_controlled

data class TripApproveModel(
    val id: Int? = 0,
    val routeId: Int? = 0,
    val fromAreaId: Int? = 0,
    val toAreaId: Int? = 0,
    val mode: String? = "",
    val date: String? = "",
    val remark: String? = "",
    val createdAt: String? = "",
    val createdBy: String? = "",
    val updatedAt: String? = "",
    val updatedBy: String? = "",
    val deletedAt: String? = "",
    val deletedBy: String? = "",
    val approveAt: String? = "",
    val approveBy: String? = "",
    val approveRemark: String? = "",
    val rejectAt: String? = "",
    val rejectBy: String? = "",
    val rejectRemark: String? = "",
    val route: String? = "",
    val fromArea: String? = "",
    val toArea: String? = "",
    var isChecked: Boolean? = false
){

}