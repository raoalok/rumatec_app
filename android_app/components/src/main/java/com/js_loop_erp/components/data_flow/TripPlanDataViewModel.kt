package com.js_loop_erp.components.data_flow

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TripPlanDataViewModel: ViewModel() {

    val tripEditCompanion_ = MutableLiveData<String>().apply {
        value = "palceholder"
    }

    var tripEditCompanion__ = "placeholder"

    val _tripEditCompanion_: LiveData<String> get() = tripEditCompanion_

    fun tripEditCompanion(item :String){
        Log.d("TAG", "tripEditCompanion: ${item}")
        //tripEditCompanion_.value = item
        tripEditCompanion_.value = item
        tripEditCompanion__ = item.toString()
    }

}