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
import androidx.compose.ui.graphics.Color
import com.example.cypher_vault.viewmodel.authentication.AuthenticationViewModel
import com.example.cypher_vault.view.registration.CameraPreview

@Composable
fun NavigationLogin(authenticationViewModel: AuthenticationViewModel) {
    val personas = listOf("Juan", "Miguel", "Pedro", "Isabel", "Miguelina")

    var selectedPersona by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(personas) { persona ->
            Button(
                onClick = {
                    selectedPersona = persona
                },
                modifier = Modifier.padding(8.dp) // Añade un espacio entre los botones
            ) {
                Text(text = persona)
            }
        }

    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        OutlinedButton(
            onClick = { authenticationViewModel.navigateToRegister() },
            border = BorderStroke(0.dp, Color.Transparent),
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Registrarse")
        }
    }



    selectedPersona?.let { persona ->
        RegistrationCameraScreen(
            authenticationViewModel = authenticationViewModel,
            persona = persona
        )
    }

}

@Composable
fun RegistrationCameraScreen(authenticationViewModel: AuthenticationViewModel, persona: String) {
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
        CloseCameraButton(cameraProvider, authenticationViewModel)
    }
}

@Composable
fun CloseCameraButton(cameraProvider: ProcessCameraProvider, authenticationViewModel: AuthenticationViewModel) {
    Button(onClick = {
        // Cierra la cámara
        cameraProvider.unbindAll()
        authenticationViewModel.navigateToListLogin()
    }, modifier = Modifier.padding(bottom = 50.dp)) {
        Text(text = "Cerrar cámara")
    }

}