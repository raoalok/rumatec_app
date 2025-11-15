package com.js_loop_erp.components.data_flow

import  com.js_loop_erp.components.fragments.access_controlled.TourApprovalSelectUserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object TripManagerApproveUserTrip {
    val _tripApproveUserDetails = MutableStateFlow<Array<TourApprovalSelectUserData>>(emptyArray())
    val tripApproveUserDetails: StateFlow<Array<TourApprovalSelectUserData>> = _tripApproveUserDetails

    fun updateTripApproveUserDetails(updates: Array<TourApprovalSelectUserData>){
        updates.iterator()
        _tripApproveUserDetails.value = updates
    }

    val _refreshUserApproveSelectedList = MutableStateFlow<Int>(0)
    val refreshUserApproveSelectedList: StateFlow<Int> = _refreshUserApproveSelectedList

    fun updateRefreshUserApproveSelectedList(valueInt: Int){
        _refreshUserApproveSelectedList.value = valueInt
    }
}