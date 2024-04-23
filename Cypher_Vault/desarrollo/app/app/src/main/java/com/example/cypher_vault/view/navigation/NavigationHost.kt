package com.example.cypher_vault.view.navigation


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cypher_vault.viewmodel.authentication.AuthenticationViewModel
import com.example.cypher_vault.view.login.NavigationLogin
import com.example.cypher_vault.view.registration.ConfirmationScreen
import com.example.cypher_vault.view.registration.InitialScreen
import com.example.cypher_vault.view.registration.RegistrationCameraScreen

@Composable
fun NavigationHost(authenticationViewModel: AuthenticationViewModel) {
    val navController = rememberNavController()

    // Observa navigateTo y navega cuando cambie
    val destination by authenticationViewModel.navigateTo.collectAsState()

    destination?.let {
        navController.navigate(it) {
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }


    NavHost(navController, startDestination = "register") {
        composable("register") {
            InitialScreen(authenticationViewModel)
        }
        composable("camera") {
            RegistrationCameraScreen(authenticationViewModel)
        }
        composable("confirmation") {
            ConfirmationScreen(authenticationViewModel)
        }
        composable("login") {
            // Aquí puedes agregar la vista de inicio de sesión
        }
        composable("list") {
            NavigationLogin(authenticationViewModel)
        }
    }
}
