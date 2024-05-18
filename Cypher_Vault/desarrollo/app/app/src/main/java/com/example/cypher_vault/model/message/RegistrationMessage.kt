package com.example.cypher_vault.model.message
import android.util.Log
import com.example.cypher_vault.model.dbmanager.DatabaseManager
import com.example.cypher_vault.model.dbmanager.DatabaseManager.getAllUsers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

//-----------FUNCIONES EN DONDE SE VALIDA LOS CAMPOS DE REGISTRO, SON FUNCIONES BOOLEANAS-----------------------//

    fun validateNameLettersOnly(name: String): Boolean {
        val isValid = name.all { it.isLetter() }
        Log.d("MiTag", "validateNameLettersOnly: $isValid")
        return isValid
    }

    fun validateMail(email: String): Boolean {
        val emailRegex =   "^[a-zA-Z0-9](?:[a-zA-Z0-9._%+-]*[a-zA-Z0-9])?@[a-zA-Z.-]+\\.[a-zA-Z]{2,}$".toRegex()
        val isValid = emailRegex.matches(email)
        return isValid
    }

    fun isEmailRegistered(email: String): Boolean {
        var result = false
        runBlocking {
            launch(Dispatchers.IO) {
                val users = getAllUsers()
                for (user in users) {
                    if (user.email == email) {
                        result = true
                        break
                    }
                }
            }
        }
        return result
    }

    fun validateEmailNotRegistered(email: String): Boolean {
        return !isEmailRegistered(email)
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


    fun validateFields(email: String, name: String , password: String): Boolean{
        return name.isEmpty() || email.isEmpty() || password.isEmpty()
    }


   fun validatePasswordSpecialcharacters(password: String): Boolean {
       val alphanumericOnly = password.filter {!it.isLetterOrDigit() }
       return alphanumericOnly.length >= 1
   }


fun validatePasswordSpacioCharacters(password: String): Boolean {
    // Verifica si hay al menos un espacio en la contraseña
    return password.contains(' ')
}


    fun validateNamedSpecialcharacters(name: String): Boolean {
        val alphanumericOnly = name.filter {!it.isLetterOrDigit() }
        return alphanumericOnly.length >= 1
    }


    fun validatePasswordCharacters(password: String): Boolean {
        val alphanumericOnly = password.filter { it.isLetterOrDigit() }
        return alphanumericOnly.length >= 15
    }


    fun validatePasswordLength(password: String): Boolean {
        return password.length >= 16
    }

    fun validatePasswordLengthMax(password: String): Boolean {
        return password.length <= 32
    }
//---------------------------------------------------------------------------------------------------//



//---------FUNCIONES EN DONDE SE ENVIA EL MENSAJE DEL ERROR O ACLARACION, SON FUNCIONES CON STRING----------//

    private fun nameLettersOnlyMesseger(): String{
        return "El nombre debe contener únicamente caracteres alfabéticos." }

    private fun validateNameMaxMessage(): String{
        return "El nombre de usuario supera los 50 caracteres alfabéticos"
    }

private fun mailMesserger(): String{
        return "El correo electrónico debe ser válido."
    }
    private fun emailRegisteredMesseger(): String{
        return "El correo electrónico ya está registrado."
    }

    private fun nameNumbersMesseger(): String{
        return "El nombre no puede contener números"
    }
    private fun nameSpacesAndLineBreaksMesseger(): String{
        return "El nombre no puede contener espacios en blanco."
    }
    private fun nameMesseger(): String{
        return "El nombre debe tener entre 3 y 50 caracteres alfabético."
    }

private fun fieldsMesseger(): String{
        return "Por favor, rellena todos los campos correctamente."
    }

private fun passwordLengthMessger(): String{
        return "La contraseña debe contener un mínimo de 15 caracteres alfanumerico y al menos 1 caracter especial ."
    }
    private fun passwordCharactersMesseger(): String{
        return  "La contraseña debe contener al menos un carácter especial"
    }


    private fun passwordLengthMaxMessger(): String{
        return "La contraseña no debe exceder los 32 caracteres."
    }

private fun validateNamedSpecialcharactersMesseger():String{
    return "No se admite caracteres especiales o espacios"
}



    private fun emailLettersOnlyMessege(): String{
        return "No pueden haber espacios en un correo."
    }

private fun validatePasswordCharactersMessage(): String{
    return "Tu contraseña solo contiene caracteres alfanuméricos, falta un carácter especial."
}
private fun validatepasswordspecialcharacters(): String{
    return "A tu contraseña le faltan caracteres alfanuméricos"
}

private fun validatePasswordSpacioCharacters(): String{
    return "No se permite el espacio como caracter especial"
}

//--------------------------------------------------------------------------------------------------//


//esta funcion se encarga verificar si todos los datos que ingreso el usuario son valido y luego lo devuelve al controllador (true o false)
// si es valido se almacena en la base de datos
    fun validate(email: String, name: String, password: String): Boolean {
        return (validateEmailNotRegistered(email) && validateNameLettersOnly(name) && !validateNamedSpecialcharacters(name) &&
                !validateNameSpacesAndLineBreaks(name) && validateMail(email) && !validateNameNumbers(name) && validateName(name)
                && validatePasswordCharacters(password) && validatePasswordSpecialcharacters(password) && validatePasswordLength(password) &&
                validatePasswordLengthMax(password) && !validateFields(email, name, password) && !validatePasswordSpacioCharacters(password))
    }



//-------------------------------------------------------------------------------------------------//





//-------FUNCION PARA GENERAR UN MENSAJE EN CADA SUBCAMPO EN EL CASO DE QUE EXISTA UN ERROR O UNA ACLARACION---------//

//todas las condicones del campo de nombre (si hay numero, espacio,menor que 3 caracteres)
    fun fullnamefield (name : String): String{
        if (validateNamedSpecialcharacters(name))
            return validateNamedSpecialcharactersMesseger()
        else if(validateNameMax(name))
            return validateNameMaxMessage()
       else if(!validateName(name))
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
        else if (!validateEmailNotRegistered(email))
            return emailRegisteredMesseger()
        return null.toString()
    }


//toda la condiciones del campo de contraseña (si es mayor que 16 caracteres, si es menor que 32 caracteres)
    fun fullpassword(password: String): String{
         if(validatePasswordSpacioCharacters(password))
           return validatePasswordSpacioCharacters()
        else if(!validatePasswordLength(password))
            return passwordLengthMessger()
        else if(!validatePasswordLengthMax(password))
            return passwordLengthMaxMessger()
        else if(!validatePasswordCharacters(password))
            return validatepasswordspecialcharacters()
        else if(!validatePasswordSpecialcharacters(password))
            return validatePasswordCharactersMessage()
        return null.toString()
    }


//--------------------------------------------------------------------------------------------------------------//



//Esta funciom se encargar de asignar el mensaje correspondiente al error, (zocalo de mensaje que esta abajo del boton de REGISTRARSE)
    fun errorMessage(email: String, name: String, password: String): String {
        if(validateFields(email, name, password))
            return fieldsMesseger()
        else if(validateNameSpacesAndLineBreaks(name))
            return nameSpacesAndLineBreaksMesseger()
        else if (validateNamedSpecialcharacters(name))
            return validateNamedSpecialcharactersMesseger()
        else if(!validateNameLettersOnly(name))
            return nameLettersOnlyMesseger()
        else if(!validateMail(email))
            return mailMesserger()
        else if (!validateEmailNotRegistered(email))
            return emailRegisteredMesseger()
        else if(validateNameNumbers(name))
            return nameNumbersMesseger()
        else if(!validateName(name))
            return nameMesseger()
        else if(validatePasswordSpacioCharacters(password))
            return validatePasswordSpacioCharacters()
        else if(validatePasswordCharacters(password))
            return passwordCharactersMesseger()
        else if(!validatePasswordSpecialcharacters(password))
            return validatePasswordCharactersMessage()
        else if(!validatePasswordLength(password))
            return passwordLengthMessger()
        else if(!validatePasswordLengthMax(password))
            return passwordLengthMaxMessger()

        return ""
    }
//------------------------------------------------------------------------------------------------//
