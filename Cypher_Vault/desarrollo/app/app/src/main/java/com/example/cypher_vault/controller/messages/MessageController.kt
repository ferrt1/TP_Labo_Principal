package com.example.cypher_vault.controller.messages

import com.example.cypher_vault.model.message.*

//------------------GET PRINCIPALES PARA VALIDAR LOS DATOS DE USUARIO Y INDICAR EL ERROR (abajo del boton de regitrarse -----//


//Encargado de comunicarse entre el controller authentication y la parte logica de Message
fun registrationValidation(email: String, name: String, pin: String): Boolean {
    return validate(email, name, pin)
}


//Encargado de comunicarse entre la parte de vista y la parte logica cuando existe un error en un campo
fun getMessageError(email: String, name: String, pin: String): String {
   return errorMessage(email, name, pin).toString()
}

//------------------------------------------------------------------------------------------------//






//-----------------GET DE CADA ZOCALO DE CADA CAMPO CORRESPONDIENTE--------------------------------//

//Encargado de comunicarse entre la parte visual y logica si existe un error en el campo NOMBRE//
fun getfullnamefield(name :String): String{
    return  fullnamefield(name)
}

//Encargado de comunicarse entre la parte visual y logica si existe un error en el campo EMAIL//
fun getfullemailfield(email: String): String{
    return fullemailfield(email)
}

//Encargado de comunicarse entre la parte visual y logica si existe un error en el campo Contraseña//
fun getfullpasswordfield(password: String): String{
    return  fullpassword(password)
}


//-------------------------------------------------------------------------------------------------//





//--------------VALIDACIONES PARA EL CAMBIO DE COLORES DE LAS LETRAS O GENERAR LOS ICONOS (erorr, warning)-------------- //

fun getvalidatePasswordLength(password: String): Boolean {
    return validatePasswordLength(password)
}

fun getvalidateNameMax(name: String):Boolean{
    return validateNameMax(name)
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


fun getvalidatePasswordLengthMax(password: String):Boolean{
    return validatePasswordLengthMax(password)
}


fun getvalidateMail(email: String): Boolean{
    return validateMail(email)
}

fun getvalidateNamedSpecialcharacters(name: String): Boolean{
    return validateNamedSpecialcharacters(name)
}

fun getvalidatePasswordsSecialcharacters(password: String):Boolean{
    return validatePasswordSpecialcharacters(password)
}

fun getvalidatePasswordCharacters(password: String):Boolean{
    return validatePasswordCharacters(password)
}

//-------------------------------------------------------------------------------------------------//