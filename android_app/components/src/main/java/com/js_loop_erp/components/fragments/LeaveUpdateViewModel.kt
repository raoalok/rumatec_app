package com.js_loop_erp.components.fragments

data class LeaveUpdateViewModel(
    val id: Int? = 0,
    val type: String? = " ",
    val startDate: String? = " ",
    val endDate: String? = " ",
    val remark: String? = "",
    val userId: Int? = 0,
    val createdAt: String? = "",
    val createdBy: String? = "",
    val updatedAt: String? = "",
    val updatedBy: String? = "",
    val deletedAt: String? = "",
    val deletedBy: String? = "",
    val approvedAt: String? = "",
    val approvedBy: String? = null,
    val rejectedAt: String? = "",
    val rejectedBy: String? = null,
    val actionRemark: String? = "",
    val askedBy: String? = ""
) {

}

/*
{
    "id": 1,
    "type": "EL",
    "startDate": "2025-03-31T18:30:00.000Z",
    "endDate": "2025-04-30T18:29:59.999Z",
    "remark": null,
    "userId": null,
    "createdAt": "2025-05-20T11:12:26.931Z",
    "createdBy": "Alok Yadav",
    "updatedAt": "2025-05-20T11:13:02.142Z",
    "updatedBy": "Alok Yadav",
    "deletedAt": null,
    "deletedBy": null,
    "approvedAt": null,
    "approvedBy": null,
    "rejectedAt": null,
    "rejectedBy": null,
    "actionRemark": null,
    "askedBy": null
}
    */