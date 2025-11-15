package com.js_loop_erp.components.data_flow

import android.util.Log
import  com.js_loop_erp.components.data_class.ProductList
import  com.js_loop_erp.components.data_class.ProductListSamples
import  com.js_loop_erp.components.data_class.TourPlanUpdateCompanion
import  com.js_loop_erp.components.data_class.TourPlanUpdateStockist
import  com.js_loop_erp.components.data_class.TripReportApprovedModel
import  com.js_loop_erp.components.data_class.TripSubmitReportSelectedTourPlan
import  com.js_loop_erp.components.data_class.TripSubmitReportSelectedTourPlanCompanion
import  com.js_loop_erp.components.data_class.TripSubmitReportSelectedTourPlanDoctor
import  com.js_loop_erp.components.data_class.TripSubmitReportSelectedTourPlanHospital
import  com.js_loop_erp.components.data_class.TripSubmitReportSelectedTourPlanInstitute
import  com.js_loop_erp.components.data_class.TripSubmitReportSelectedTourPlanParty
import  com.js_loop_erp.components.data_class.TripSubmitReportSelectedTourPlanPetshop
import  com.js_loop_erp.components.data_class.TripSubmitReportSelectedTourPlanProducts
import  com.js_loop_erp.components.data_class.TripSubmitReportSelectedTourPlanSamples
import  com.js_loop_erp.components.fragments.tripReportSubmit.TripApprovedPlanDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

object TripTravelReportSubmitSharedFlow {

    val _tripTravelReportSubmitSharedFlow = MutableStateFlow(TripReportApprovedModel())
    val tripTravelReportSubmitSharedFlow: StateFlow<TripReportApprovedModel> = _tripTravelReportSubmitSharedFlow

    fun updateTripTravelReportSubmitSharedFlow(updates: TripReportApprovedModel) {
        _tripTravelReportSubmitSharedFlow.value = updates
    }

    val _currentTripTravelRoutePlanDetails = MutableStateFlow(TripSubmitReportSelectedTourPlan())
    val currentTripTravelRoutePlanDetails: StateFlow<TripSubmitReportSelectedTourPlan> = _currentTripTravelRoutePlanDetails

    fun updateCurrentTripTravelRoutePlanDetails(updates: TripSubmitReportSelectedTourPlan) {
        _currentTripTravelRoutePlanDetails.value  = updates // { updates } //.value = updates
        Log.d("TAG", "updateCurrentTripTravelRoutePlanDetails: ${updates}")
    }

    fun resetCurrentTripTravelRoutePlanDetails() {
        _currentTripTravelRoutePlanDetails.value = TripSubmitReportSelectedTourPlan()
    }

    val _updateData = MutableStateFlow<Int>(0)
    val updateData: StateFlow<Int> = _updateData

    fun updateAllData(updates: Int) {
        _updateData.value  = updates // { updates } //.value = updates
    }
    
    
    /*---------------------------------------------*/

    val _tripPlanReportDoctor = MutableStateFlow<TripSubmitReportSelectedTourPlanDoctor?>(null)
    val tripPlanReportDoctor: StateFlow<TripSubmitReportSelectedTourPlanDoctor?> = _tripPlanReportDoctor

    fun updatePlanReportTripPlanReportDoctor(planReports: TripSubmitReportSelectedTourPlanDoctor) {
        //planReports.iterator()
        //_tripPlanReportDoctor.value = planReports.first()
        _tripPlanReportDoctor.value = planReports
    }

    val _tripPlanReportParty = MutableStateFlow<TripSubmitReportSelectedTourPlanParty?>(null)
    val tripPlanReportParty: StateFlow<TripSubmitReportSelectedTourPlanParty?> = _tripPlanReportParty

    fun updatePlanReportTripPlanReportParty(planReports: TripSubmitReportSelectedTourPlanParty) {
        //planReports.iterator()
        //_tripPlanReportParty.value = planReports.first()
        _tripPlanReportParty.value = planReports
    }

    val _tripPlanReportHospital = MutableStateFlow<TripSubmitReportSelectedTourPlanHospital?>(null)
    val tripPlanReportHospital: StateFlow<TripSubmitReportSelectedTourPlanHospital?> = _tripPlanReportHospital

    fun updatePlanReportTripPlanReportHospital(planReports: TripSubmitReportSelectedTourPlanHospital) {
        //planReports.iterator()
        //_tripPlanReportHospital.value = planReports.first()
        _tripPlanReportHospital.value = planReports
    }

    val _tripPlanReportPetShop = MutableStateFlow<TripSubmitReportSelectedTourPlanPetshop?>(null)
    val tripPlanReportPetShop: StateFlow<TripSubmitReportSelectedTourPlanPetshop?> = _tripPlanReportPetShop

    fun updatePlanReportTripPlanReportPetShop(planReports: TripSubmitReportSelectedTourPlanPetshop) {
        //planReports.iterator()
        //_tripPlanReportPetShop.value = planReports.first()
        _tripPlanReportPetShop.value = planReports
    }

    val _tripPlanReportInstitute = MutableStateFlow<TripSubmitReportSelectedTourPlanInstitute?>(null)
    val tripPlanReportInstitute: StateFlow<TripSubmitReportSelectedTourPlanInstitute?> = _tripPlanReportInstitute

    fun updatePlanReportTripPlanReportInstitute(planReports: TripSubmitReportSelectedTourPlanInstitute) {
        //planReports.iterator()
        //_tripPlanReportInstitute.value = planReports.first()
        _tripPlanReportInstitute.value = planReports
    }

    val _tripPlanReportCompanion = MutableStateFlow<Array<TripSubmitReportSelectedTourPlanCompanion>>(emptyArray())
    val tripPlanReportCompanion: StateFlow<Array<TripSubmitReportSelectedTourPlanCompanion>> = _tripPlanReportCompanion

    fun updatePlanReportTripPlanReportCompanion(planReports: Array<TripSubmitReportSelectedTourPlanCompanion>) {
        planReports.iterator()
        _tripPlanReportCompanion.value = planReports
    }

    val _tripPlanReportProducts = MutableStateFlow<Array<ProductList>>(emptyArray())
    val tripPlanReportProducts: StateFlow<Array<ProductList>> = _tripPlanReportProducts

    fun updatePlanReportTripPlanReportProducts(planReports: Array<ProductList>) {
        planReports.iterator()
        _tripPlanReportProducts.value = planReports
    }

    val _tripPlanReportSamples = MutableStateFlow<Array<ProductListSamples>>(emptyArray())
    val tripPlanReportSamples: StateFlow<Array<ProductListSamples>> = _tripPlanReportSamples

    fun updatePlanReportTripPlanReportSamples(planReports: Array<ProductListSamples>) {
        planReports.iterator()
        _tripPlanReportSamples.value = planReports
    }

    fun resetPlanReportTripPlanReportSamples(planReports: Array<ProductListSamples>) {
        planReports.iterator()
        _tripPlanReportSamples.value = planReports
    }

    val _tripPlanReportDate = MutableStateFlow<String>("")
    val tripPlanReportDate: StateFlow<String> = _tripPlanReportDate

    fun updatePlanReportTripPlanReportDate(planReports: String) {
        planReports.iterator()
        _tripPlanReportDate.value = planReports
    }

    val _tripReportProductListQtyCheck = MutableStateFlow<Array<ProductListSamples>>(emptyArray())
    val tripReportProductListQtyCheck: StateFlow<Array<ProductListSamples>> = _tripReportProductListQtyCheck

    fun updateTripReportProductListQtyCheck(productList: Array<ProductListSamples>) {
        productList.iterator()
        _tripReportProductListQtyCheck.value = productList
    }
}