package com.example.cypher_vault.view

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.TextField
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture


@Composable
fun CameraButton(isCameraOpen: MutableState<Boolean>) {
    val context = LocalContext.current
    Button(onClick = {
        // Verifica si se tienen los permisos necesarios
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Inicia la cámara
            isCameraOpen.value = true
        } else {
            Toast.makeText(context, "Permiso de cámara no concedido", Toast.LENGTH_LONG).show()
        }
    }) {
        Text("Registrarse")
    }
}


@Composable
fun LoginView(cameraProviderFuture: ListenableFuture<ProcessCameraProvider>, lifecycleOwner: LifecycleOwner) {
    val isCameraOpen = remember { mutableStateOf(false) }
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
        .build()

    val cameraProvider = cameraProviderFuture.get()
    val preview = Preview.Builder().build()
    cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)

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
            CameraButton(isCameraOpen)

            // Vista previa de la cámara
            if (isCameraOpen.value) {
                CameraPreview(preview)
            }
        }
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
