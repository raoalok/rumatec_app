package com.js_loop_erp.components.data_flow

import android.util.Log
import  com.js_loop_erp.components.data_class.TripTourPlanCompanionSelection
import  com.js_loop_erp.components.fragments.attendance.AttendanceListModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object AttendanceListDataFlow {
    val _attendanceList = MutableStateFlow<Array<AttendanceListModel>>(emptyArray())
    val attendanceList: StateFlow<Array<AttendanceListModel>> = _attendanceList

    fun updateAttendanceList(updates: Array<AttendanceListModel>){
        updates.iterator()
        _attendanceList.value = updates
    }

    fun addAttendanceList(update: AttendanceListModel) {
        _attendanceList.value += update
    }

    fun removeAttendanceItemById(id: Int) {
        val currentList = _attendanceList.value
        val updatedList = currentList.filter { it.id != id }.toTypedArray()

        if (updatedList.size < currentList.size) {
            _attendanceList.value = updatedList
        }

        if(updatedList.size == 0){
            _attendanceList.value  = emptyArray()
        }
    }

    val _updateData = MutableStateFlow<Int>(0)
    val updateData: StateFlow<Int> = _updateData

    fun updateAllData(updates: Int) {
        _updateData.value  = updates // { updates } //.value = updates
    }

}