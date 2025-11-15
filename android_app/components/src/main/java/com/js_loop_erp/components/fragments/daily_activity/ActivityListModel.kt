package com.js_loop_erp.components.fragments.daily_activity

data class ActivityListModel(
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
    val approvedBy: String? = null,
    val approveRemark: String? = "",
    val rejectAt: String? = "",
    val rejectedBy: String? = null,
    val rejectRemark: String? = "",
    var category: String? = "",
    var fromArea: String? = "",
    var toArea: String? = "",
    var activity: String? = "",
    var activityKms: Double? = 0.0,
    var startTime: String? = null,
    var endTime: String? = null
)

data class ActivityListModelServer(
    var type: String = "",
    var category: String = "",
    var mode: String = "",
    var kilometers: String = "0",
    var startDate: String = "",
    var endDate: String = "",
    var remark: String = ""
)

data class TripWrapper(
    val activities: List<ActivityListModelServer>
)

/*    var type: String = "",
    var category: String = "",
    var mode: String = "",
    var kilometers: Int = 0,
    var startDate: String = "",
    var endDate: String = "",
    var remark: String = "",
    var userId: String = "",
    var createdAt: String = "",
    var createdBy: String = "",
    var updatedAt: String = "",
    var updatedBy: String = "",
    var deletedAt: String = "",
    var deletedBy: String = "",
    var approvedAt: String = "",
    var approvedBy: String = "",
    var rejectedAt: String = "",
    var rejectedBy: String = "",
    var actionRemark: String = ""*/