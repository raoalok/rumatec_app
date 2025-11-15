package com.js_loop_erp.components.data_flow

import android.util.Log
import androidx.lifecycle.ViewModel
import  com.js_loop_erp.components.data_class.TripSubmitReportSelectedTourPlan
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class TripTravelReportSubmitViewModel: ViewModel() {

    val _currentTripTravelRoutePlanDetails = MutableSharedFlow<TripSubmitReportSelectedTourPlan>(replay = 5)
    val currentTripTravelRoutePlanDetails = _currentTripTravelRoutePlanDetails.asSharedFlow()

    fun updateCurrentTripTravelRoutePlanDetails(updates: TripSubmitReportSelectedTourPlan) {
        TripTravelReportSubmitSharedFlow.updateCurrentTripTravelRoutePlanDetails(updates)
        _currentTripTravelRoutePlanDetails.tryEmit(updates)
    }
}