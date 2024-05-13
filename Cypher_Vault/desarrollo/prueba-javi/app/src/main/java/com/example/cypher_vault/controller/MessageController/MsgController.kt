package com.example.cypher_vault.controller.MessageController

import com.example.cypher_vault.model.message.*

fun registrationValidation(email: String, name: String, pin: String): Boolean {
    return validate(email, name, pin)
}

fun getMessageClarification(State : String): String {
    return ClarificationMessage(State )
}

fun getMessageError(email: String, name: String, pin: String): String? {
    return ErrorMessage(email, name, pin)
}

fun getvalidatePasswordCharacters(password: String): Boolean{
    return validatePasswordCharacters(password)
}

fun getvalidatePasswordLength(password: String): Boolean{
    return validatePasswordLength(password)
}
