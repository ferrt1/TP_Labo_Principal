package com.example.cypher_vault.view.registration

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.cypher_vault.controller.authentication.AuthenticationController
import com.example.cypher_vault.database.ImagesRegister

@SuppressLint("UnsafeExperimentalUsageError")
@Composable
fun RegistrationCameraXScreen(authenticationController: AuthenticationController, userId: Int) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val isCameraOpen = remember { mutableStateOf(true) }
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
        .build()

    val cameraProvider = remember { mutableStateOf<ProcessCameraProvider?>(null) }
    val preview = remember { Preview.Builder().build() }
    val imageCapture = remember { ImageCapture.Builder().build() }

    LaunchedEffect(cameraProviderFuture) {
        cameraProvider.value = cameraProviderFuture.get()
        cameraProvider.value?.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        if (isCameraOpen.value) {
            CameraXPreview(preview)
            CloseCameraXButton(isCameraOpen, cameraProvider, authenticationController, userId)
        }
    }
}

@Composable
fun CloseCameraXButton(
    isCameraOpen: MutableState<Boolean>,
    cameraProvider: MutableState<ProcessCameraProvider?>,
    authenticationController: AuthenticationController,
    userId: Int // ID del usuario asociado a la imagen
) {
    val context = LocalContext.current
    val imageCapture = remember { ImageCapture.Builder().build() }

    Button(
        onClick = {
            // Cierra la cámara
            isCameraOpen.value = false
            cameraProvider.value?.unbindAll()

            // Captura la imagen
            imageCapture.takePicture(ContextCompat.getMainExecutor(context), object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    val buffer = image.planes[0].buffer
                    val bytes = ByteArray(buffer.remaining())
                    buffer.get(bytes)

                    // Guarda la imagen en la base de datos
                    authenticationController.getDataBaseManager().insertImageRegister(ImagesRegister(imageData = bytes, user_id = userId))

                    // Libera recursos
                    image.close()
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(context, "Error capturando imagen: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            })
        },
        modifier = Modifier.padding(bottom = 50.dp)
    ) {
        Text("Tomar foto")
    }
}

@Composable
fun CameraXPreview(preview: Preview) {
    val context = LocalContext.current

    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                preview.setSurfaceProvider(surfaceProvider)
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}