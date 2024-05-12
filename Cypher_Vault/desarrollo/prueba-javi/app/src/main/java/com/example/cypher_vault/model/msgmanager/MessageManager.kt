package com.example.cypher_vault.model.msgmanager

import androidx.compose.runtime.MutableState
import com.example.cypher_vault.message.*

fun getTotal(email: String, name: String, pin: String): Boolean {
    return validate(email, name, pin)
}

fun getMessage(estado: String): String {
    return Message(estado)
}
