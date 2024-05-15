package com.example.cypher_vault.model.message
import android.util.Log


//Funciones en donde se valida los campos de registro, son funciones booleanas-----------------------

fun validateNameLettersOnly(name: String): Boolean {
    val isValid = name.all { it.isLetter() }
    Log.d("MiTag", "validateNameLettersOnly: $isValid")
    return isValid
}

fun validateMail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun validateNameNumbers(name: String): Boolean {
    return name.any { it.isDigit() }
}

fun validateNameSpacesAndLineBreaks(name: String): Boolean {
    return name.contains(" ") || name.contains("\n") || name.contains("\r\n")
}

fun validateName(name: String): Boolean{
    return name.length in 3..50
}

fun validateNameMax(name: String): Boolean {
    return name.length > 50
}


fun validateFields(email: String, name: String): Boolean{
    return name.isEmpty() || email.isEmpty()
}

fun validatePasswordCharacters(password: String): Boolean {
    return password.any { it.isLetterOrDigit() } && password.any { !it.isLetterOrDigit() }
}


fun validatePasswordLength(password: String): Boolean {
    return password.length >= 16
}

fun validatePasswordLengthMax(password: String): Boolean {
    return password.length <= 32
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
    return "El password debe contener un minimo de 16 carácteres y 1 espacial"
}
private fun passwordCharactersMesseger(): String{
    return  "El password debe tener un carácter especial"
}

private fun passwordLengthMaxMessger(): String{
    return "El password no debe ser mayor a 32 caracteres"
}


private fun emailLettersOnlyMessege(): String{
    return "no puede exitir espacios en un correo "
}

//--------------------------------------------------------------------------------------------------


//esta funcion se encarga verificar si todos los datos que ingreso el usuario son valido y luego lo devuelve al controllador (true o false)
// si es valido se alamacena en la base de datos
fun validate(email: String, name: String, pin: String): Boolean {
    Log.d("MiTag", "estoy en la parte logica y mi valoes que me dieron fueron:  $email,$name,$pin ")
    return (validateNameLettersOnly(name) && !validateNameSpacesAndLineBreaks(name) && validateMail(email) && !validateNameNumbers(name) && validateName(name)
            && validatePasswordCharacters(pin) && validatePasswordLength(pin) && validatePasswordLengthMax(pin) && !validateFields(email, name))
}



//-------------------------------------------------------------------------------------------------//

//todas las condicones del campo de nombre (si hay numero, espacio,menor que 3 caracteres)
fun fullnamefield (name : String): String{
    if(!validateName(name))
        return nameMesseger()
    else if (validateNameNumbers(name))
        return nameNumbersMesseger()
    else if (validateNameSpacesAndLineBreaks(name))
        return  nameSpacesAndLineBreaksMesseger()
    return null.toString()
}

//toda las condiciones del campo de correo (si hay espacio o no es unc correo valido)

fun fullemailfield (email : String): String{
    if(validateNameSpacesAndLineBreaks(email))
        return emailLettersOnlyMessege()
    else if (!validateMail(email))
        return mailMesserger()
    return null.toString()
}


//toda la condiciones del campo de contraseña (si es mayor que 16 caracteres, si es menor que 32 caracteres)
fun fullpassword(password: String): String{
    if(!validatePasswordLength(password))
        return passwordLengthMessger()
    else if(!validatePasswordLengthMax(password))
        return passwordLengthMaxMessger()
    return null.toString()
}

//Esta funciom se encargar de asignar el mensaje correspondiente al error, (zocalo de mensaje que esta abajo del boton de REGISTRARSE)
fun errorMessage(email: String, name: String, pin: String): String {
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
    else if(!validatePasswordLengthMax(pin))
        return passwordLengthMaxMessger()
    return ""
}
