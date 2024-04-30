package com.example.cypher_vault.view.registration

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
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
import com.example.cypher_vault.model.facermanager.FaceDetectionActivity
import android.graphics.BitmapFactory
import android.util.Log
import com.google.common.util.concurrent.ListenableFuture
import java.io.ByteArrayOutputStream



@SuppressLint("UnsafeExperimentalUsageError")
@Composable
fun RegistrationCameraXScreen(authenticationController: AuthenticationController, userId: Int) {

//    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

//    val cameraSelector = CameraSelector.Builder()
//        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
//        .build()
//
//    val cameraProvider = cameraProviderFuture.get()
//    val imageCapture = ImageCapture.Builder().build()
//    val preview = Preview.Builder().build()
//
//    LaunchedEffect(cameraProviderFuture) {
//        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, imageCapture, preview)
//    }
//    val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val isCameraOpen = remember { mutableStateOf(true) }
    Log.d("faceDetection", "antes de imageCapture 1")
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
        .build()
    Log.d("faceDetection", "antes de imageCapture 2")
    val imageCapture = ImageCapture.Builder()
        .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
        .build()
    val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> = remember { ProcessCameraProvider.getInstance(context) }
    val processCameraProvider = cameraProviderFuture.get()
    val preview = Preview.Builder().build()
    Log.d("faceDetection", "antes de imageCapture 3")
    LaunchedEffect(cameraProviderFuture) {
        processCameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, imageCapture, preview)
    }
    Log.d("faceDetection", "antes de imageCapture 3.3")
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        if (isCameraOpen.value) {
            Log.d("faceDetection", "antes de imageCapture 4")
            CameraXPreview(preview)
            CloseCameraXButton(processCameraProvider, imageCapture,context, isCameraOpen, authenticationController, userId)
        }
    }
}

@Composable
fun CloseCameraXButton(
    processCameraProvider: ProcessCameraProvider,
    imageCapture: ImageCapture,
    context: Context,
    isCameraOpen: MutableState<Boolean>,
    authenticationController: AuthenticationController,
    userId: Int // ID del usuario asociado a la imagen){}){}){}){}){}){}
) {
    Button(
        onClick = {
            Log.d("faceDetection", "antes de imageCapture 6")
            // Captura la imagen
            imageCapture.takePicture(ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy) {
                        // Convertir la imagen a un bitmap
                        val bitmap = imageProxyToBitmap(image)

                        // Convertir el bitmap a bytes
                        val bytes = bitmapToByteArray(bitmap)

                        // Ejecutar la detección de rostros
                        val faceDetector = FaceDetectionActivity()
                        faceDetector.detectFaces(bitmap)

                        // Cerrar el ImageProxy después de usarlo
                        image.close()
                        // Cierra la cámara
                        isCameraOpen.value = false
                        processCameraProvider.unbindAll()
                    }
                    override fun onError(error: ImageCaptureException)
                    {
                        Log.d("faceDetection", "error: ImageCaptureException : $error")
                    }
                })
            authenticationController.navigateToConfirmation()
        },
        modifier = Modifier.padding(bottom = 50.dp)
    ) {
        Text("Tomar foto")
    }
}

@Composable
fun CameraXPreview(preview: Preview) {
    Log.d("faceDetection", "antes de imageCapture 5")
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

fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
    return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
}

fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}

private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
    val buffer = image.planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}