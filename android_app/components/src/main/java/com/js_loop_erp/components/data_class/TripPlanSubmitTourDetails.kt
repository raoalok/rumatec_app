package com.js_loop_erp.components.data_class

data class TripPlanSubmitTourDetails(
val routeId: Int?,
val fromAreaId: Int?,
val toAreaId: Int?,
val mode: String?,
val date: String?,
val remark: String?,
val companionIds: List<Int>?,
val doctorIds: List<Int>?,
val partyIds: List<Int>?,
val hospitalIds: List<Int>?,
val petshopIds: List<Int>?,
val instituteIds: List<Int>?
)

