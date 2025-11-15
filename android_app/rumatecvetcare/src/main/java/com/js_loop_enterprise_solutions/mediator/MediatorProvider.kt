package com.js_loop_enterprise_solutions.mediator

import com.js_loop_enterprise.common_mediator.Mediator
import com.js_loop_enterprise.common_mediator.Receiver

class MediatorProvider: Mediator {
    private val receivers = mutableListOf<Receiver>()

    override fun sendMessage(message: String) {
        receivers.forEach { it.receiveMessage(message) }
    }

    override fun registerReceiver(receiver: Receiver) {
        receivers.add(receiver)
    }

    override fun unregisterReceiver(receiver: Receiver) {
        receivers.remove(receiver)
    }
}