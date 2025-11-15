package com.js_loop_erp.components.data_flow

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object LoginDetails {

    val _loginToken = MutableStateFlow<String>("")
    val loginToken: StateFlow<String> = _loginToken

    fun updateLoginToken(updates: String){
        _loginToken.value = updates
    }


}