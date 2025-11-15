package com.js_loop_erp.components.data_class

data class TripSubmitReportSelectedTourPlan(
val id: Int? = null,
val routeId: Int? = null,
val fromAreaId: Int? = null,
val toAreaId: Int? = null,
val mode: String? = null,
val date: String? = null,
val remark: String? = null,
val createdAt: String? = null,
val createdBy: String? = null,
val updatedAt: String? = null,
val updatedBy: String? = null,
val deletedAt: String? = null,
val deletedBy: String? = null,
val approveAt: String? = null,
val approveBy: String? = null,
val approveRemark: String? = null,
val rejectAt: String? = null,
val rejectBy: String? = null,
val rejectRemark: String? = null,
val route: String? = null,
val fromArea: String? = null,
val toArea: String? = null,
val companions: MutableList<TripSubmitReportSelectedTourPlanCompanion> = mutableListOf(),
val doctors: MutableList<TripSubmitReportSelectedTourPlanDoctor> = mutableListOf(),
val parties: MutableList<TripSubmitReportSelectedTourPlanParty> = mutableListOf(),
val hospitals: MutableList<TripSubmitReportSelectedTourPlanHospital> = mutableListOf(),
val petshops: MutableList<TripSubmitReportSelectedTourPlanPetshop> = mutableListOf(),
val institutes: MutableList<TripSubmitReportSelectedTourPlanInstitute> = mutableListOf(),
val saleOrders: MutableList<TripSubmitReportSelectedTourPlanSaleOrder> = mutableListOf()
)

data class TripSubmitReportSelectedTourPlanCompanion(
    val name: String? = null,
    val companionId: Int? = null,
    var isSelected: Boolean? = false
)

data class TripSubmitReportSelectedTourPlanDoctor(
    val id: Int? = null,
    val tourId: Int? = null,
    val doctorId: Int? = null,
    var timestamp: String? = null,
    val companionIds: MutableList<SelectedCompanion> = mutableListOf(),
    val name: String? = null,
    val products: MutableList<ProductsTripReport> = mutableListOf(),
    val samples: MutableList<SamplesUpdate> = mutableListOf()
)

data class TripSubmitReportSelectedTourPlanParty(
    val id: Int? = null,
    val tourId: Int? = null,
    val partyId: Int? = null,
    var timestamp: String? = null,
    val companionIds: MutableList<SelectedCompanion> = mutableListOf(),
    val name: String? = null,
    val products: MutableList<ProductsTripReport> = mutableListOf(),
    val samples: MutableList<SamplesUpdate> = mutableListOf()
)

data class TripSubmitReportSelectedTourPlanHospital(
    val id: Int? = null,
    val tourId: Int? = null,
    val hospitalId: Int? = null,
    var timestamp: String? = null,
    val companionIds: MutableList<SelectedCompanion> = mutableListOf(),
    val name: String? = null,
    val products: MutableList<ProductsTripReport> = mutableListOf(),
    val samples: MutableList<SamplesUpdate> = mutableListOf()
)

data class TripSubmitReportSelectedTourPlanPetshop(
    val id: Int? = null,
    val tourId: Int? = null,
    val petshopId: Int? = null,
    var timestamp: String? = null,
    val companionIds: MutableList<SelectedCompanion> = mutableListOf(),
    val name: String? = null,
    val products: MutableList<ProductsTripReport> = mutableListOf(),
    val samples: MutableList<SamplesUpdate> = mutableListOf()
)

data class TripSubmitReportSelectedTourPlanInstitute(
    val id: Int? = null,
    val tourId: Int? = null,
    val instituteId: Int? = null,
    var timestamp: String? = null,
    val companionIds: MutableList<SelectedCompanion> = mutableListOf(),
    val name: String? = null,
    val products: MutableList<ProductsTripReport> = mutableListOf(),
    val samples: MutableList<SamplesUpdate> = mutableListOf()
)

data class TripSubmitReportSelectedTourPlanSaleOrder(
    val id: Int? = null,
    val tourId: Int? = null,
    val instituteId: Int? = null,
    var timestamp: String? = null,
    val companionIds: MutableList<SelectedCompanion> = mutableListOf(),
    val name: String? = null,
    val saleOrders: MutableList<Int> = mutableListOf(),
    val samples: MutableList<SamplesUpdate> = mutableListOf()
)

data class SamplesUpdate(
    var productId: Int? = 0,
    var quantity: Int? = 0,
    var name: String? = null
)

data class ProductsTripReport(
    var productId: Int? = null,
    var name: String? = null
)

data class SelectedCompanion(
    val name: String? = null,
    val id: Int? = null
)
data class TripSubmitReportSelectedTourPlanProducts(
    val id: Int? = null,
    val name: String? = null,
    val rate: String? = null,
    val unit: String? = null,
    val packUnit: String? = null,
    val tax: Int? = null,
    val hsn: String? = null,
    val division: String? = null,
    val packSize: Int? = null,
    val type: String? = null,
    val lastPurchaseRate: String? = null,
    val mrp: String? = null,
    var isSelected: Boolean? = null
)

data class TripSubmitReportSelectedTourPlanSamples(
    val id: Int? = null,
    val name: String? = null,
    val rate: String? = null,
    val unit: String? = null,
    val packUnit: String? = null,
    val tax: Int? = null,
    val hsn: String? = null,
    val division: String? = null,
    val packSize: Int? = null,
    val type: String? = null,
    val lastPurchaseRate: String? = null,
    val mrp: String? = null,
    var isSelected: Boolean? = null
)


/*
data class TripSubmitReportSelectedTourReportProducts(
    val name: String? = null,
    val id: Int? = null,
    var isSelected: Boolean? = null
)

data class TripSubmitReportSelectedTourReportSamples(
    val name: String? = null,
    val id: Int? = null,
    var isSelected: Boolean? = null
)*/
