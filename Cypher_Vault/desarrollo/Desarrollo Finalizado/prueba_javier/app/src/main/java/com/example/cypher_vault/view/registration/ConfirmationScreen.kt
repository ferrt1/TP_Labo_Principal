package com.example.cypher_vault.view.registration

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.cypher_vault.controller.authentication.AuthenticationController

@Composable
fun ConfirmationScreen(authenticationController: AuthenticationController) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.wrapContentSize()
        ) {
            Text( "¡Ya estás registrado! ")
            Button(onClick = {
                authenticationController.navigateToLogin()
            }) {
                Text("Iniciar sesión")
            }
        }
    }
}
