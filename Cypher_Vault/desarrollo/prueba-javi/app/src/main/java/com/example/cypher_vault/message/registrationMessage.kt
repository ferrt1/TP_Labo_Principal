package com.example.cypher_vault.message

import android.util.Log







private fun validateNameLettersOnly(name: String): Boolean {
    val isValid = name.all { it.isLetter() }
    Log.d("MiTag", "validateNameLettersOnly: $isValid")
    return isValid
}


private fun validateMail(email: String): Boolean {
    val isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    Log.d("MiTag", "validateMail (estoy dentro del metodo): $isValid")
    return isValid
}

private fun validateNameNumbers(name: String): Boolean {
    val isValid =  name.any { it.isDigit()
    }
    Log.d("MiTag", "validateNameNumbers: $isValid")
    return isValid
}

private fun validateNameSpacesAndLineBreaks(name: String): Boolean {
    val isValid = name.contains(" ") || name.contains("\n") || name.contains("\r\n")
    Log.d("MiTag", "validateNameSpacesAndLineBreaks: $isValid")
    return isValid
}

private fun validateName(name: String): Boolean{
    val isValid= name.length in 3..50
    Log.d("MiTag", "validateName: $isValid")
    return isValid
}

private fun validateFields(email: String, name: String): Boolean{
    val isValid= name.isEmpty() || email.isEmpty()
    Log.d("MiTag", "validateFields: $isValid")
    return isValid
}

private fun validatePIN(pin: String): Boolean{
    val isValid=  pin.length == 4
    Log.d("MiTag", "validatePIN: $isValid")
    return isValid
}

private fun validatePinNumbers(pin: String): Boolean {
    val isValid=  pin.all { it.isDigit() }
    Log.d("MiTag", "validatePinNumbers: $isValid")
    return isValid
}


fun email(): String{
    return "tiene que ingresar un correo electronico valido"
}

fun name(): String{
    return "tiene que ingresar un nombre valido minimo de 3 caracteres No numerico"
}


//esta funcion se encarga verificar si todos los datos son valido y luego lo devuelve (true o false)
 fun validate(email: String, name: String, pin: String): Boolean {
    Log.d("MiTag", "estoy en la parte logica y mi valoes que me dieron fueron:  $email,$name,$pin ")
    return (validateNameLettersOnly(name) && !validateNameSpacesAndLineBreaks(name) && validateMail(email) && !validateNameNumbers(name) && validateName(name)
            && validatePinNumbers(pin) && validatePIN(pin) && !validateFields(email, name))

}

//esta funcion se encarga de generar el tipo de mensaje a la parte de la vista (primero pasa por el msgManager)
fun Message(estado: String ): String {
    if(estado=="email")
        return email()
    if(estado=="name")
        return name()
    else
        return "null"
}



