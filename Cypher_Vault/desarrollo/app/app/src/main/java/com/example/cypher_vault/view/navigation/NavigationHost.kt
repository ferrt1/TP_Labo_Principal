package com.example.cypher_vault.view.navigation


import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
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


@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun NavigationHost() {
    val navController = rememberNavController()
    val navigationController = NavController(navController)
    val galleryController = GalleryController()

    NavHost(navController, startDestination = "register") {
        composable("register") {
            BackHandler(true) {
                // Or do nothing
                Log.i("LOG_TAG", "Clicked back")
            }
            InitialScreen(navigationController)
        }
        composable("camera/{userId}") { backStackEntry ->
//            BackHandler(true) {
//                // Or do nothing
//                Log.i("LOG_TAG", "Clicked back")
//            }
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
        composable("confirmation/{userId}/{registerSuccessfull}") { backStackEntry ->
            BackHandler(true) {
                Log.i("LOG_TAG", "Clicked back")
            }
            val userId = backStackEntry.arguments?.getString("userId")
            val registerSuccessfulString = backStackEntry.arguments?.getString("registerSuccessfull")
            val registerSuccessful = registerSuccessfulString.toBoolean()
            if (userId != null) {
                ConfirmationScreen(navigationController, userId, registerSuccessful)
            }
        }

        composable("authenticate/{userId}") {backStackEntry ->
            BackHandler(true) {
                // Or do nothing
                Log.i("LOG_TAG", "Clicked back")
            }
            val userId = backStackEntry.arguments?.getString("userId")
            if (userId != null) {
                ConfirmationLoginScreen(navigationController, userId)
            } else {

            }
        }
        composable("gallery/{userId}") {backStackEntry ->
            BackHandler(true) {
                // Or do nothing
                Log.i("LOG_TAG", "Clicked back")
            }
            val userId = backStackEntry.arguments?.getString("userId")
            if (userId != null) {
                Gallery(navigationController, userId, galleryController)
            } else {
                // Manejar el caso en que el userId no se pudo recuperar
            }
        }

        composable("list") {
            BackHandler(true) {
                // Or do nothing
                Log.i("LOG_TAG", "Clicked back")
            }
            NavigationLogin(navigationController)
        }
    }
}