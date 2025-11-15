package com.js_loop_enterprise.common_mediator

interface Mediator {
    fun sendMessage(message: String)
    fun registerReceiver(receiver: Receiver)
    fun unregisterReceiver(receiver: Receiver)
}