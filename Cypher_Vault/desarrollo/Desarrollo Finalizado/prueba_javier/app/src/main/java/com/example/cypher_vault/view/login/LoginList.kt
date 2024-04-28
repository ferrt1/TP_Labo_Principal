package com.example.cypher_vault.view.login

import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.cypher_vault.controller.authentication.AuthenticationController
import com.example.cypher_vault.view.registration.CameraPreview

@Composable
fun NavigationLogin(authenticationController: AuthenticationController) {
    val users by authenticationController.users.collectAsState()

    var selectedPersona by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(users) { user ->
                    Button(
                        onClick = {
                            selectedPersona = user.firstName
                        },
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.padding(8.dp) // Añade un espacio entre los botones
                            .width(150.dp) // Ancho del botón
                            .height(80.dp) // Alto del botón


                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp) // Espacio entre los elementos
                        )  {
                            Text(
                                text = user.firstName?.take(7)?.let { if (it.length < 7) it else "$it..." } ?: "",
                                fontSize = 20.sp // Tamaño de fuente más grande para el nombre
                            )
                            Text(
                                text = user.email?.take(10)?.let { if (it.length < 10) it else "$it..." } ?: "",
                                fontSize = 14.sp // Tamaño de fuente más pequeño para el correo electrónico
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp)) // Agregar un espacio entre la lista de botones y el botón "Registrarse"
            OutlinedButton(
                onClick = { authenticationController.navigateToRegister() },
                border = BorderStroke(0.dp, Color.Transparent),
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Registrarse")
            }
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