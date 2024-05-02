package com.example.cypher_vault.view.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cypher_vault.controller.authentication.AuthenticationController
import com.example.cypher_vault.view.login.NavigationLogin
import com.example.cypher_vault.view.registration.ConfirmationScreen
import com.example.cypher_vault.view.registration.InitialScreen
import com.example.cypher_vault.view.registration.RegistrationCameraScreen

@Composable
fun NavigationHost() {
    val navController = rememberNavController()
    val authenticationController = AuthenticationController(navController)
    NavHost(navController, startDestination = "register") {
        composable("register") {
            InitialScreen(authenticationController)
        }
        composable("camera/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            if (userId != null) {
                RegistrationCameraScreen(authenticationController, userId)
            } else {
                // Manejar el caso en que el userId no se pudo recuperar
            }
        }
        composable("confirmation/{userId}") {backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            if (userId != null) {
                ConfirmationScreen(authenticationController, userId)
            } else {
                // Manejar el caso en que el userId no se pudo recuperar
            }
        }
        composable("login") {
            // Aquí puedes agregar la vista de inicio de sesión
        }
        composable("list") {
            NavigationLogin(authenticationController)
        }

    }
}