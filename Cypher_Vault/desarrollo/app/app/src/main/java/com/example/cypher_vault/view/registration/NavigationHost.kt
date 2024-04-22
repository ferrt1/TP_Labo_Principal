package com.example.cypher_vault.view.registration


import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cypher_vault.controller.authentication.AuthenticationController

@Composable
fun NavigationHost() {
    val navController = rememberNavController()
    val authenticationController = AuthenticationController(navController)
    NavHost(navController, startDestination = "register") {
        composable("register") {
            InitialScreen(authenticationController)
        }
        composable("camera") {
            RegistrationCameraScreen(authenticationController)
        }
        composable("confirmation") {
            ConfirmationScreen(authenticationController)
        }
        composable("login") {
            // Aquí puedes agregar la vista de inicio de sesión
        }
    }
}