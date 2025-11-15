package com.js_loop_erp.components.data_class

data class UserSession(
    var userName: String = "Name: ",
    var userId: String = "0",
    var time: String = " ",
    var login: Boolean = false,
    var token: String = " ",
    var role: String = " ",
    var email: String = "",
    var adminPermission: Boolean = false
)
