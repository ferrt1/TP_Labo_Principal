package com.example.cypher_vault.view.login

import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
@Composable
fun NavigationLogin() {
    val personas = listOf("Juan", "Miguel", "Pedro", "Isabel", "Miguelina")

    var isCameraOpen by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(personas) { persona ->
            Button(
                onClick = {
                    isCameraOpen = true
                },
                modifier = Modifier.padding(8.dp) // Añade un espacio entre los botones
            ) {
                Text(text = persona)
            }
        }
    }

    if (isCameraOpen) {
        RegistrationCameraScreen()
    }
}
@Composable
fun RegistrationCameraScreen(){
val context = LocalContext.current
val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
val lifecycleOwner = LocalLifecycleOwner.current
val isCameraOpen = remember { mutableStateOf(true) }
val cameraSelector = CameraSelector.Builder()
    .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
    .build()

val cameraProvider = cameraProviderFuture.get()
val preview = Preview.Builder().build()

LaunchedEffect(cameraProviderFuture) {
    cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
}

Box(
modifier = Modifier.fillMaxSize(),
contentAlignment = Alignment.BottomCenter
) {
    if (isCameraOpen.value) {
        CameraPreview(preview)
        CloseCameraButton(isCameraOpen, cameraProvider)
    }
}
}


@Composable
fun CloseCameraButton(isCameraOpen: MutableState<Boolean>, cameraProvider: ProcessCameraProvider) {
    Button(onClick = {
        // Cierra la cámara
        isCameraOpen.value = false
        cameraProvider.unbindAll()
    }) {
        Text(text = "Cerrar cámara")
    }
}

@Composable
fun CameraPreview(preview: Preview) {
    AndroidView(
        factory = {
            PreviewView(it).apply {
                preview.setSurfaceProvider(this.surfaceProvider)
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}