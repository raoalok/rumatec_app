package com.js_loop_erp.components.data_class


data class TripTourReportSubmitData(
val tourId: Int? = null,
val doctors: List<Entity>? = null,
val parties: List<Entity>? = null,
val hospitals: List<Entity>? = null,
val petshops: List<Entity>? = null,
val institutes: List<Entity>? = null,
val salesOrder: List<Entity>? = null
)

data class Entity(
    val doctorId: Int? = null,
    val timestamp: String? = null,
    val companionIds: List<Int>? = null,
    val products: List<Int>? = null,
    val samples: List<Sample>? = null
)

data class Sample(
    val productId: Int? = null,
    val quantity: Int? = null
)
