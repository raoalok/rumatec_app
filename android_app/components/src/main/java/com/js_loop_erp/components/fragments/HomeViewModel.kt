package com.js_loop_erp.components.fragments

import android.os.Handler
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.js_loop_erp.components.data_class.UserSession
import com.js_loop_erp.components.fragments.LoginFragment.Companion.TAG

class HomeViewModel : ViewModel() {


    public var _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
        Log.d("TAG", "sanjay ----2 " + value.toString())
    }

    var text: LiveData<String> =_text


    fun setText(value__ :String){
        Log.d("TAG", "sanjay ----1 " + value__.toString())
        text = MutableLiveData(value__)
    }

    val mutableSelectedItem = MutableLiveData<Float>().apply {
        value = 1000.0F
    }

    val selectedItem: LiveData<Float> get() = mutableSelectedItem

    fun scanPercantage(item: Float){
        Log.d("TAG", "sanjay ----1 " + item.toString())
        mutableSelectedItem.value = item
    }
//-----------------------------------------------------------------------------------------

    val userSessionLiveData = MutableLiveData<UserSession>().apply {
        value = UserSession()
    }

    fun updateUserSession(updatedSession: UserSession) {
        userSessionLiveData.value = updatedSession
    }

    val isLocationServiceRunning_ = MutableLiveData<String>().apply {
        value = "Status Offline"
    }

    val _isLocationServiceRunning_: LiveData<String> get() = isLocationServiceRunning_

    fun isLocationServiceRunning(item: String) {
        isLocationServiceRunning_.value = item
    }

    val logInLogOffButtonStatus_ = MutableLiveData<String>().apply{
        value = "Log in"
    }

    val _logInLogOffButtonStatus_: LiveData<String> get() = logInLogOffButtonStatus_

    fun logInLogOffButtonStatus(item:String){
        logInLogOffButtonStatus_.value = item
    }

    val loginButtonListener_ = MutableLiveData<Int>().apply{
        value = 0
    }

    val _loginButtonListener_: LiveData<Int> get() = loginButtonListener_

    fun loginButtonListener(item: Int){
        loginButtonListener_.value = item
    }

    val userId_ = MutableLiveData<String>().apply{
        value = "Id: "
    }

    val _userId_: LiveData<String> get() = userId_

    fun userId(item: String){
        userId_.value = item
    }

    val address_ = MutableLiveData<String>().apply{
        value = " "
    }

    val _address_: LiveData<String> get() = address_

    fun address(item: String){
        address_.value = item
    }


    companion object {
        val TAG = HomeViewModel::class.java.simpleName
    }

}