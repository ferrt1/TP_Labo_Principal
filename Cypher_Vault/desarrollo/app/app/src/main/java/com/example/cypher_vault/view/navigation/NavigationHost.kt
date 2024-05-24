package com.example.cypher_vault.view.navigation


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cypher_vault.controller.navigation.NavController
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
    val navigationController = NavController(navController)
    val galleryController = GalleryController()

    NavHost(navController, startDestination = "register") {
        composable("register") {
            InitialScreen(navigationController)
        }

        composable("camera/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            if (userId != null) {
                RegistrationCameraScreen(navigationController, userId)
            } else {
            }
        }
        composable("cameralogin/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            if (userId != null) {
                LoginCamera(navigationController, userId)
            } else {

            }
        }

        composable("confirmation/{userId}") {backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            if (userId != null) {
                ConfirmationScreen(navigationController, userId)
            } else {
            }
        }
        composable("authenticate/{userId}") {backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            if (userId != null) {
                ConfirmationLoginScreen(navigationController, userId)
            } else {

            }
        }
        composable("gallery/{userId}") {backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            if (userId != null) {
                LaunchedEffect(userId) {
                    galleryController.performUserIncomeInsertion(userId)
                }
                Gallery(navigationController, userId, galleryController)
            } else {
                // Manejar el caso en que el userId no se pudo recuperar
            }
        }
        composable("profile/{userId}") {backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            if (userId != null) {
                UserProfile(navigationController, userId)
            } else {
                // Manejar el caso en que el userId no se pudo recuperar
            }
        }

        composable("list") {
            NavigationLogin(navigationController)
        }


    }
}