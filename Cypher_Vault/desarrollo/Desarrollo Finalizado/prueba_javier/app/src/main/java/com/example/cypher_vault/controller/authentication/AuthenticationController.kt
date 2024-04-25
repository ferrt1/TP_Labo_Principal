package com.example.cypher_vault.controller.authentication

import androidx.compose.runtime.MutableState
import androidx.navigation.NavController

class AuthenticationController(private val navController: NavController) {

    // La app ya empieza en register

    fun navigateToCamera() {
        navController.navigate("camera")
    }

    fun navigateToConfirmation() {
        navController.navigate("confirmation")
    }

    fun navigateToLogin() {
        navController.navigate("login")
    }


    // ... acá iria el método de javi por ejemplo

    fun navigatelistlogin(){
        navController.navigate("list")
    }
    fun navigateRegister(){
        navController.navigate("register")
    }
    //.... fin de los metodos de javi

    fun registerUser(
        email: String,
        name: String,
        showDialog: MutableState<Boolean>,
        errorMessage: MutableState<String>
    ) {
        if (!validateFields(email, name) && validateMail(email) && validateName(name)) {
            navigateToCamera()
        }
        else if(validateFields(name, email)){
            showDialog.value = true
            errorMessage.value = "Por favor, rellena todos los campos correctamente."
        }
        else if (!validateMail(email)) {
            showDialog.value = true
            errorMessage.value = "El email debe ser válido"
        }
        else if (!validateName(name)){
            showDialog.value = true
            errorMessage.value = "El nombre debe tener más de 3 carácteres"
        }
    }

    private fun validateMail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validateName(name: String): Boolean{
        return name.length >= 3;
    }

    private fun validateFields(email: String, name: String): Boolean{
        return name.isEmpty() || email.isEmpty()
    }

}

