package com.example.cypher_vault.model.message

import android.util.Log

//Funciones en donde se valida los campos de registro, son funciones booleanas-----------------------

private fun validateNameLettersOnly(name: String): Boolean {
    val isValid = name.all { it.isLetter() }
    Log.d("MiTag", "validateNameLettersOnly: $isValid")
    return isValid
}

private fun validateMail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

private fun validateNameNumbers(name: String): Boolean {
    return name.any { it.isDigit() }
}

private fun validateNameSpacesAndLineBreaks(name: String): Boolean {
    return name.contains(" ") || name.contains("\n") || name.contains("\r\n")
}

private fun validateName(name: String): Boolean{
    return name.length in 3..50
}

private fun validateFields(email: String, name: String): Boolean{
    return name.isEmpty() || email.isEmpty()
}

fun validatePasswordCharacters(password: String): Boolean {
    return password.any { it.isLetterOrDigit() } && password.any { !it.isLetterOrDigit() }
}


fun validatePasswordLength(password: String): Boolean {
    return password.length >= 16
}
//---------------------------------------------------------------------------------------------------


// --------funciones en donde se envia el mensaje del error (son funciones con String)---------------

private fun nameLettersOnlyMesseger(): String{
    return "El nombre debe contener caracteres alfabéticos únicamente"
}
private fun mailMesserger(): String{
    return "El email debe ser válido"
}
private fun nameNumbersMesseger(): String{
    return "El nombre no puede tener números"
}
private fun nameSpacesAndLineBreaksMesseger(): String{
    return "El nombre no puede contener espacios en blanco"
}
private fun nameMesseger(): String{
    return "El nombre debe tener más de 3 carácteres y menos de 50"
}
private fun fieldsMesseger(): String{
    return "Por favor, rellena todos los campos correctamente."
}
private fun passwordLengthMessger(): String{
    return "El PIN debe contener 16 carácteres"
}
private fun passwordCharactersMesseger(): String{
    return  "El PIN debe tener un carácter especial"
}



private fun email(): String{
    return "tiene que ingresar un correo electronico valido"
}

private fun name(): String{
    return "tiene que ingresar un nombre valido minimo de 3 caracteres No numerico"
}
//--------------------------------------------------------------------------------------------------


//esta funcion se encarga verificar si todos los datos que ingreso el usuario son valido y luego lo devuelve al controllador (true o false)
fun validate(email: String, name: String, pin: String): Boolean {
    Log.d("MiTag", "estoy en la parte logica y mi valoes que me dieron fueron:  $email,$name,$pin ")
    return (validateNameLettersOnly(name) && !validateNameSpacesAndLineBreaks(name) && validateMail(email) && !validateNameNumbers(name) && validateName(name)
            && validatePasswordCharacters(pin) && validatePasswordLength(pin) && !validateFields(email, name))
}

//Esta funciom se encargar de asignar el mensaje correspondiente al error (TIPO ERROR)
fun errorMessage(email: String, name: String, pin: String): String? {
    if(validateFields(email, name))
        return fieldsMesseger()
    else if(validateNameSpacesAndLineBreaks(name))
        return nameSpacesAndLineBreaksMesseger()
    else if(!validateNameLettersOnly(name))
        return nameLettersOnlyMesseger()
    else if(!validateMail(email))
        return mailMesserger()
    else if(validateNameNumbers(name))
        return nameNumbersMesseger()
    else if(!validateName(name))
        return nameMesseger()
    else if(!validatePasswordCharacters(pin))
        return passwordCharactersMesseger()
    else if(!validatePasswordLength(pin))
        return passwordLengthMessger()
    return null
}



//Esta funciom se encargar de asignar el mensaje correspondiente al campo que esta pocisionado el
//usuario, se en via informacion de lo que tiene que completar (TIPO ACLARACION)
fun clarificationMessage(estado: String ): String {
    if(estado=="email")
        return email()
    else if(estado=="name")
        return name()
    else
        return "null"
}

//fun warningMessage (Completo el campo pero una parte es incorrecta y la otra correcta) (TIPO ADVERTENCIA)


