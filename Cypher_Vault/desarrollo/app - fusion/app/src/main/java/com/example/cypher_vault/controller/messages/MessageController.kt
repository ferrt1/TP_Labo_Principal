package com.example.cypher_vault.controller.messages

import com.example.cypher_vault.model.message.*


import com.example.cypher_vault.model.message.*

fun registrationValidation(email: String, name: String, pin: String): Boolean {
    return validate(email, name, pin)
}


// posiblemente se borre esta funcion si se saca el socalo de mensaje abajo de todo
//fun getMessageError(email: String, name: String, pin: String): String? {
//   return errorMessage(email, name, pin)
//}




fun getfullnamefield(name :String): String{
    return  fullnamefield(name)
}

fun getfullemailfield(email: String): String{
    return fullemailfield(email)
}



//fun getnameLettersOnlyMesseger(name: String): String

//---------------------------------------
fun getvalidatePasswordCharacters(password: String): Boolean{
    return validatePasswordCharacters(password)
}
fun getvalidatePasswordLength(password: String): Boolean {
    return validatePasswordLength(password)
}

fun getvalidateNameSpacesAndLineBreaks(name: String): Boolean{
    return validateNameSpacesAndLineBreaks(name)
}

fun getvalidateNameNumbers(name: String): Boolean{
    return validateNameNumbers(name)
}

fun getvalidateName(name: String): Boolean{
    return validateName(name)
}

//validacion de emial

fun getvalidateMail(email: String): Boolean{
    return validateMail(email)
}
