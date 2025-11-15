package com.js_loop_erp.components.fragments.attendance

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AttendanceListModel(
    var id: Int? = 0,
    val routeId: Int? = 0,
    val fromAreaId: Int? = 0,
    val toAreaId: Int? = 0,
    var mode: String? = "",
    var date: String? = "",
    var remark: String? = "",
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
    var route: String? = "",
    var fromArea: String? = "",
    var toArea: String? = "",
    var activity: String? = ""
): Parcelable