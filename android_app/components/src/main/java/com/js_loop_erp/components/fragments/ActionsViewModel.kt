package com.js_loop_erp.components.fragments

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ActionsViewModel: ViewModel() {

    val backPressed_ = MutableLiveData<Boolean>().apply{
        value = false
    }

    val _backPressed_: LiveData<Boolean> get() = backPressed_

    fun backPressed(item: Boolean){
        backPressed_.value = item
    }


    val openUri_ = MutableLiveData<Uri>().apply{
        value = null
    }

    val _openUri_: LiveData<Uri> get() = openUri_

    fun openUri(item: Uri){
        Log.d("TAG","openUri: .......... ")
        openUri_.value = item
    }

}