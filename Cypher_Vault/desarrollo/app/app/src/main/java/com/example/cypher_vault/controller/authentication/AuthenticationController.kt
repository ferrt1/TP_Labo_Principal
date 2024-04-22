package com.example.cypher_vault.controller.authentication

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
    // fun login()
}
