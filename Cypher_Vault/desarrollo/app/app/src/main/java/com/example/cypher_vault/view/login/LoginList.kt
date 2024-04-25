package com.example.cypher_vault.view.login

import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.example.cypher_vault.controller.authentication.AuthenticationController
import com.example.cypher_vault.view.registration.CameraPreview

@Composable
fun NavigationLogin(authenticationController: AuthenticationController) {
    val users by authenticationController.users.collectAsState()

    var selectedPersona by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(users) { user ->
            Button(
                onClick = {
                    selectedPersona = user.firstName
                },
                modifier = Modifier.padding(8.dp) // Añade un espacio entre los botones
            ) {
                Text(text = user.firstName ?: "")
            }
        }

    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        OutlinedButton(
            onClick = { authenticationController.navigateToRegister() },
            border = BorderStroke(0.dp, Color.Transparent),
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Registrarse")
        }
    }



    selectedPersona?.let { user ->
        loginCamera(
            authenticationController = authenticationController,
            user = user
        )
    }

}

@Composable
fun loginCamera(authenticationController: AuthenticationController, user: String) {
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraProvider = cameraProviderFuture.get()
    val preview = Preview.Builder().build()
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
        .build()

    LaunchedEffect(cameraProviderFuture) {
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        CameraPreview(preview)
        CloseCameraButton(cameraProvider, authenticationController)
    }
}

@Composable
fun CloseCameraButton(cameraProvider: ProcessCameraProvider, authenticationController: AuthenticationController) {
    Button(onClick = {
        // Cierra la cámara
        cameraProvider.unbindAll()
        authenticationController.navigateToListLogin()
    }, modifier = Modifier.padding(bottom = 50.dp)) {
        Text(text = "Cerrar cámara")
    }

}