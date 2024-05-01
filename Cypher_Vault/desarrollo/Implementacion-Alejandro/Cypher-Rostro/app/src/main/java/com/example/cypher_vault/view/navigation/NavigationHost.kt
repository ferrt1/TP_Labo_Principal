package com.example.cypher_vault.view.navigation


import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cypher_vault.controller.authentication.AuthenticationController
import com.example.cypher_vault.view.login.NavigationLogin
import com.example.cypher_vault.view.registration.CameraPreviewScreen
import com.example.cypher_vault.view.registration.ConfirmationScreen
import com.example.cypher_vault.view.registration.InitialScreen
import com.example.cypher_vault.view.registration.RegistrationCameraScreen

@Composable
fun NavigationHost() {
    val navController = rememberNavController()
    val authenticationController = AuthenticationController(navController)
    NavHost(navController, startDestination = "register") {
        composable("register") {
            Log.d("faceDetection", "Inicio InitialScreen")
            InitialScreen(authenticationController)
            Log.d("faceDetection", "Salida InitialScreen")
        }
        composable("camera/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toLongOrNull()
            if (userId != null) {
                Log.d("faceDetection", "Inicio CameraPreviewScreen")
                CameraPreviewScreen(authenticationController, userId)
                Log.d("faceDetection", "Salida CameraPreviewScreen")
            } else {
                // Manejar el caso en que el userId no se pudo recuperar
            }
        }
        composable("confirmation") {
            Log.d("faceDetection", "Inicio ConfirmationScreen")
            ConfirmationScreen(authenticationController)
            Log.d("faceDetection", "Salida ConfirmationScreen")
        }
        composable("login") {
            Log.d("faceDetection", "Inicio login")
            // Aquí puedes agregar la vista de inicio de sesión
        }
        composable("list") {
            Log.d("faceDetection", "Inicio NavigationLogin")
            NavigationLogin(authenticationController)
            Log.d("faceDetection", "Salida NavigationLogin")
        }

    }
}