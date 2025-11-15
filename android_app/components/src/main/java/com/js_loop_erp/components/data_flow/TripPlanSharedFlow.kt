package com.js_loop_erp.components.data_flow

import android.util.Log
import  com.js_loop_erp.components.data_class.TourPlanUpdateChemist
import  com.js_loop_erp.components.data_class.TourPlanUpdateCompanion
import  com.js_loop_erp.components.data_class.TourPlanUpdateDoctor
import  com.js_loop_erp.components.data_class.TourPlanUpdateHospital
import  com.js_loop_erp.components.data_class.TourPlanUpdateStockist
import  com.js_loop_erp.components.data_class.TourUpdateCompanionList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object TripPlanSharedFlow {
    val _dataFlow = MutableStateFlow<String>("Initial Value")
    val dataFlow: StateFlow<String> = _dataFlow

    fun updateData(newValue: String) {
        _dataFlow.value = newValue
    }

    val _tripUpdateCompanion = MutableStateFlow<Array<TourPlanUpdateCompanion>>(emptyArray())
    val tripUpdateCompanion: StateFlow<Array<TourPlanUpdateCompanion>> = _tripUpdateCompanion

    fun updateTripUpdateCompanion(updates: Array<TourPlanUpdateCompanion>) {
        updates.iterator()
        _tripUpdateCompanion.value = updates
    }

    val _tripUpdateDoctor = MutableStateFlow<Array<TourPlanUpdateDoctor>>(emptyArray())
    val tripUpdateDoctor: StateFlow<Array<TourPlanUpdateDoctor>> = _tripUpdateDoctor

    fun updateTripUpdateDoctor(updates: Array<TourPlanUpdateDoctor>) {
        updates.iterator()
        _tripUpdateDoctor.value = updates
    }

    val _tripUpdateHospital = MutableStateFlow<Array<TourPlanUpdateHospital>>(emptyArray())
    val tripUpdateHospital: StateFlow<Array<TourPlanUpdateHospital>> = _tripUpdateHospital

    fun updateTripUpdateHospital(updates: Array<TourPlanUpdateHospital>) {
        updates.iterator()
        _tripUpdateHospital.value = updates
    }

    val _tripUpdateChemist = MutableStateFlow<Array<TourPlanUpdateChemist>>(emptyArray())
    val tripUpdateChemist: StateFlow<Array<TourPlanUpdateChemist>> = _tripUpdateChemist

    fun updateTripUpdateChemist(updates: Array<TourPlanUpdateChemist>) {
        updates.iterator()
        _tripUpdateChemist.value = updates
    }

    val _tripUpdateStockist = MutableStateFlow<Array<TourPlanUpdateStockist>>(emptyArray())
    val tripUpdateStockist: StateFlow<Array<TourPlanUpdateStockist>> = _tripUpdateStockist

    fun updateTripUpdateStockist(updates: Array<TourPlanUpdateStockist>) {
        updates.iterator()
        _tripUpdateStockist.value = updates
    }

}