package com.js_loop_erp.components.data_flow

import  com.js_loop_erp.components.data_class.TripTourPlanChemistSelection
import  com.js_loop_erp.components.data_class.TripTourPlanDoctorSelection
import  com.js_loop_erp.components.data_class.TripTourPlanHospitalSelection
import  com.js_loop_erp.components.data_class.TripTourPlanStockistSelection
import  com.js_loop_erp.components.data_class.TripTourPlanCompanionSelection
import  com.js_loop_erp.components.data_class.TripTourPlanInstituteSelection
import  com.js_loop_erp.components.data_class.TripTourPlanMeetingDetailsSelection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object TripPlanFormSharedFlow {

    val _tripPlanFormCompanion = MutableStateFlow<Array<TripTourPlanCompanionSelection>>(emptyArray())
    val tripPlanFormCompanion: StateFlow<Array<TripTourPlanCompanionSelection>> = _tripPlanFormCompanion

    fun updateTripPlanFormCompanion(updates: Array<TripTourPlanCompanionSelection>){
        updates.iterator()
        _tripPlanFormCompanion.value = updates
    }

    val _tripPlanFormDoctor = MutableStateFlow<Array<TripTourPlanDoctorSelection>>(emptyArray())
    val tripPlanFormDoctor: StateFlow<Array<TripTourPlanDoctorSelection>> = _tripPlanFormDoctor

    fun updateTripPlanFormDoctor(updates: Array<TripTourPlanDoctorSelection>){
        updates.iterator()
        _tripPlanFormDoctor.value = updates
    }

    val _tripPlanFormHospital = MutableStateFlow<Array<TripTourPlanHospitalSelection>>(emptyArray())
    val tripPlanFormHospital: StateFlow<Array<TripTourPlanHospitalSelection>> = _tripPlanFormHospital

    fun updateTripPlanFormHospital(updates: Array<TripTourPlanHospitalSelection>){
        updates.iterator()
        _tripPlanFormHospital.value = updates
    }

    val _tripPlanFormChemist = MutableStateFlow<Array<TripTourPlanChemistSelection>>(emptyArray())
    val tripPlanFormChemist: StateFlow<Array<TripTourPlanChemistSelection>> = _tripPlanFormChemist

    fun updateTripPlanFormChemist(updates: Array<TripTourPlanChemistSelection>){
        updates.iterator()
        _tripPlanFormChemist.value = updates
    }

    val _tripPlanFormStockist = MutableStateFlow<Array<TripTourPlanStockistSelection>>(emptyArray())
    val tripPlanFormStockist: StateFlow<Array<TripTourPlanStockistSelection>> = _tripPlanFormStockist

    fun updateTripPlanFormStockist(updates: Array<TripTourPlanStockistSelection>){
        updates.iterator()
        _tripPlanFormStockist.value = updates
    }

    val _tripPlanFormInstitute = MutableStateFlow<Array<TripTourPlanInstituteSelection>>(emptyArray())
    val tripPlanFormInstitute: StateFlow<Array<TripTourPlanInstituteSelection>> = _tripPlanFormInstitute

    fun updateTripPlanFormInstitute(updates: Array<TripTourPlanInstituteSelection>){
        updates.iterator()
        _tripPlanFormInstitute.value = updates
    }

    val _tripPlanFormMeetingDetails = MutableStateFlow<Array<TripTourPlanMeetingDetailsSelection>>(emptyArray())
    val tripPlanFormMeetingDetails: StateFlow<Array<TripTourPlanMeetingDetailsSelection>> = _tripPlanFormMeetingDetails

    fun updateTripPlanFormMeetingDetails(updates: Array<TripTourPlanMeetingDetailsSelection>){
        updates.iterator()
        _tripPlanFormMeetingDetails.value = updates
    }

    val _refreshTripPlanFormSubmit = MutableStateFlow<Int>(0)
    val refreshTripPlanFormSubmit: StateFlow<Int> = _refreshTripPlanFormSubmit

    fun updateRefreshTripPlanFormSubmit(valueInt: Int){
        _refreshTripPlanFormSubmit.value = valueInt
    }
}