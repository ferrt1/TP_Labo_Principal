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

private fun validatePIN(pin: String): Boolean{
    return  pin.length == 4
}

private fun validatePinNumbers(pin: String): Boolean {
    return pin.all { it.isDigit() }
}
//---------------------------------------------------------------------------------------------------


// --------funciones en donde se envia el mensaje del error (son funciones con String)---------------

private fun NameLettersOnlyMesseger(): String{
    return "El nombre debe contener caracteres alfabéticos únicamente"
}
private fun MailMesserger(): String{
    return "El email debe ser válido"
}
private fun NameNumbersMesseger(): String{
    return "El nombre no puede tener números"
}
private fun NameSpacesAndLineBreaksMesseger(): String{
    return "El nombre no puede contener espacios en blanco"
}
private fun NameMesseger(): String{
    return "El nombre debe tener más de 3 carácteres y menos de 50"
}
private fun FieldsMesseger(): String{
    return "Por favor, rellena todos los campos correctamente."
}
private fun PINMesseger(): String{
    return "El PIN debe contener 4 carácteres"
}
private fun PinNumbersMesseger(): String{
    return  "El PIN debe contener 4 carácteres numericos"
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
            && validatePinNumbers(pin) && validatePIN(pin) && !validateFields(email, name))
}

//Esta funciom se encargar de asignar el mensaje correspondiente al error (TIPO ERROR)
fun ErrorMessage(email: String, name: String, pin: String): String? {
     if(validateFields(email, name))
        return FieldsMesseger()
     else if(validateNameSpacesAndLineBreaks(name))
         return NameSpacesAndLineBreaksMesseger()
    else if(!validateNameLettersOnly(name))
        return NameLettersOnlyMesseger()
    else if(!validateMail(email))
        return MailMesserger()
    else if(validateNameNumbers(name))
        return NameNumbersMesseger()
    else if(!validateName(name))
        return NameMesseger()
    else if(!validatePinNumbers(pin))
        return PinNumbersMesseger()
    else if(!validatePIN(pin))
        return PINMesseger()
    return null
}



//Esta funciom se encargar de asignar el mensaje correspondiente al campo que esta pocisionado el
//usuario, se en via informacion de lo que tiene que completar (TIPO ACLARACION)
fun ClarificationMessage(estado: String ): String {
    if(estado=="email")
        return email()
    else if(estado=="name")
        return name()
    else
        return "null"
}

//fun warningMessage (Completo el campo pero una parte es incorrecta y la otra correcta) (TIPO ADVERTENCIA)



