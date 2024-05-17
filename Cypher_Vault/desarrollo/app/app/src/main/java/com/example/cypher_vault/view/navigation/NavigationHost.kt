package com.example.cypher_vault.view.navigation


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cypher_vault.controller.authentication.AuthenticationController
import com.example.cypher_vault.view.login.ConfirmationLoginScreen
import com.example.cypher_vault.view.login.LoginCamera
import com.example.cypher_vault.view.login.NavigationLogin
import com.example.cypher_vault.view.registration.ConfirmationScreen
import com.example.cypher_vault.view.gallery.Gallery
import com.example.cypher_vault.view.registration.InitialScreen
import com.example.cypher_vault.view.registration.RegistrationCameraScreen
import com.example.cypher_vault.controller.gallery.GalleryController
import com.example.cypher_vault.view.gallery.UserProfile

@Composable
fun NavigationHost() {
    val navController = rememberNavController()
    val authenticationController = AuthenticationController(navController)
    val galleryController = GalleryController()

    NavHost(navController, startDestination = "register") {
        composable("register") {
            InitialScreen(authenticationController)
        }
        composable("camera/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            if (userId != null) {
                RegistrationCameraScreen(authenticationController, userId)
            } else {
            }
        }
        composable("cameralogin/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            if (userId != null) {
                LoginCamera(authenticationController, userId)
            } else {

            }
        }

        composable("confirmation/{userId}") {backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            if (userId != null) {
                ConfirmationScreen(authenticationController, userId)
            } else {
            }
        }
        composable("authenticate/{userId}") {backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            if (userId != null) {
                ConfirmationLoginScreen(authenticationController, userId)
            } else {
                // Manejar el caso en que el userId no se pudo recuperar
            }
        }
        composable("gallery/{userId}") {backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            if (userId != null) {
                LaunchedEffect(userId) {
                    galleryController.performUserIncomeInsertion(userId)
                }
                Gallery(authenticationController, userId, galleryController)
            } else {
                // Manejar el caso en que el userId no se pudo recuperar
            }
        }
        composable("profile/{userId}") {backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            if (userId != null) {
                UserProfile(authenticationController, userId)
            } else {
                // Manejar el caso en que el userId no se pudo recuperar
            }
        }

        composable("list") {
            NavigationLogin(authenticationController)
        }


    }
}