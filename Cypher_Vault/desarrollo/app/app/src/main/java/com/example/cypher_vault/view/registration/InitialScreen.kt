package com.example.cypher_vault.view.registration

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import com.example.cypher_vault.controller.authentication.AuthenticationController

@Composable
fun InitialScreen(authenticationController: AuthenticationController) {
    // Agrega estados para los campos de texto
    val emailState = remember { mutableStateOf(TextFieldValue()) }
    val nameState = remember { mutableStateOf(TextFieldValue()) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            // Campo de texto para el correo electrónico
            TextField(
                value = emailState.value,
                onValueChange = { emailState.value = it },
                label = { Text("Correo electrónico") }
            )

            // Campo de texto para el nombre
            TextField(
                value = nameState.value,
                onValueChange = { nameState.value = it },
                label = { Text("Nombre") }
            )

            // Botón de la cámara
            Button(onClick = { authenticationController.navigateToCamera()  }) {
                Text("Registrarse")
            }
        }
    }
}