package com.js_loop_erp.components.data_flow

import  com.js_loop_erp.components.fragments.daily_activity.ActivityListModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object ActivityListDataFlow {
    val _activityList = MutableStateFlow<Array<ActivityListModel>>(emptyArray())
    val activityList: StateFlow<Array<ActivityListModel>> = _activityList

    fun updateActivityList(updates: Array<ActivityListModel>){
        updates.iterator()
        _activityList.value = updates
    }

    fun addActivityList(update: ActivityListModel) {
        _activityList.value += update
    }

    fun removeActivityItemById(id: Int) {
        val currentList = _activityList.value
        val updatedList = currentList.filter { it.id != id }.toTypedArray()

        if (updatedList.size < currentList.size) {
            _activityList.value = updatedList
        }

        if(updatedList.size == 0){
            _activityList.value  = emptyArray()
        }
    }


    val _updateData = MutableStateFlow<Int>(0)
    val updateData: StateFlow<Int> = _updateData

    fun updateAllData(updates: Int) {
        _updateData.value  = updates // { updates } //.value = updates
    }
}