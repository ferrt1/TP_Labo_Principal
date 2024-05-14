package com.example.cypher_vault.controller.messages

import com.example.cypher_vault.model.message.*

fun registrationValidation(email: String, name: String, pin: String): Boolean {
    return validate(email, name, pin)
}

fun getMessageClarification(State : String): String {
    return clarificationMessage(State )
}

fun getMessageError(email: String, name: String, pin: String): String? {
    return errorMessage(email, name, pin)
}

fun getvalidatePasswordCharacters(password: String): Boolean{
    return validatePasswordCharacters(password)
}

fun getvalidatePasswordLength(password: String): Boolean{
    return validatePasswordLength(password)
}