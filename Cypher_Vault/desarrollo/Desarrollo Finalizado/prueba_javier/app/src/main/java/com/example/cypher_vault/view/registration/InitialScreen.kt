package com.example.cypher_vault.view.registration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.cypher_vault.controller.authentication.AuthenticationController

@Composable
fun InitialScreen(authenticationController: AuthenticationController) {
    val emailState = remember { mutableStateOf(TextFieldValue()) }
    val nameState = remember { mutableStateOf(TextFieldValue()) }
    val showDialog = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = emailState.value,
                onValueChange = { emailState.value = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.clip(RoundedCornerShape(10.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = nameState.value,
                onValueChange = { nameState.value = it },
                label = { Text("Nombre") },
                modifier = Modifier.clip(RoundedCornerShape(10.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                authenticationController.registerUser(emailState.value.text, nameState.value.text, showDialog, errorMessage)
            }) {
                Text("Registrarse")
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = {  authenticationController.navigatelistlogin() },
                border = BorderStroke(0.dp, Color.Transparent)
            ) {
                Text("Iniciar sesión")
            }
        }
    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Error") },
            text = { Text(errorMessage.value) },
            confirmButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text("Aceptar")
                }
            }
        )
    }
}