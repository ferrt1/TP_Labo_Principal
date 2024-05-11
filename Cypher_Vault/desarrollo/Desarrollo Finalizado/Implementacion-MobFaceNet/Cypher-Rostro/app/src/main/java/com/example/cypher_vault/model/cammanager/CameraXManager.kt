package com.example.cypher_vault.view.registration


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.cypher_vault.controller.authentication.AuthenticationController
import com.example.cypher_vault.model.facermanager.FaceDetectionActivity
import java.io.ByteArrayOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import android.util.Size
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.compose.foundation.Image
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import com.example.cypher_vault.R


@Composable
fun CameraRegisterPreviewScreen(authenticationController: AuthenticationController, userId: String) {
    val lensFacing = CameraSelector.LENS_FACING_FRONT // Cambio aquí para la cámara frontal
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    // Obtener la configuración local del dispositivo
    val configuration = LocalConfiguration.current
    // Obtener la rotación del dispositivo
    val rotation = configuration.orientation
    val screenSize = if (rotation == 0) Size(720, 1280) else Size(1280, 720)
    val resolutionSelector = ResolutionSelector.Builder().setResolutionStrategy(
        ResolutionStrategy(
            screenSize,
            ResolutionStrategy.FALLBACK_RULE_NONE
        )
    ).build()
    val preview = Preview.Builder()
        .setResolutionSelector(resolutionSelector)
        .build()
    val previewView = remember {
        PreviewView(context)
    }
    val cameraxSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
    val imageCapture = remember {
        ImageCapture.Builder().build()
    }

    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraxSelector, preview, imageCapture)
        preview.setSurfaceProvider(previewView.surfaceProvider)
        Log.d(
            "faceDetection",
            "Camera preview bound to lifecycle with lens facing: $lensFacing"
        ) // Agrego un log aquí
    }
    Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxSize()) {
        AndroidView({ previewView }, modifier = Modifier.fillMaxSize())
        Button(
            onClick = { captureImageForRegister(imageCapture, context, authenticationController, userId) },
            modifier = Modifier.padding(bottom = 50.dp)
        ) {
            Text(text = "Tomar Foto")
        }
        // Añadir imagen superpuesta desde recursos
        val painter = painterResource(id = R.drawable.silueta) // Reemplaza R.drawable.overlay_image con el ID de tu imagen en la carpeta res
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .padding(1.dp) // Ajusta el padding según sea necesario
        )
    }
}

private fun captureImageForRegister(
    imageCapture: ImageCapture,
    context: Context,
    authenticationController: AuthenticationController,
    userId: String
) {
    Log.e("faceDetection", "imageCapture Register")
    // Captura la imagen
    imageCapture.takePicture(ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                // Convertir la imagen a un bitmap
                Log.d("faceDetection", "imageCapture 1")
                val bitmap = imageProxyToBitmap(image)
                // Convertir el bitmap a bytes
                val bytes = bitmapToByteArray(bitmap)
                Log.d("faceDetection", "imageCapture 2")
                // Ejecutar la detección de rostros
                val faceDetector = FaceDetectionActivity()
                Log.d("faceDetection", "imageCapture 3")
                faceDetector.detectFacesForRegister(authenticationController, bitmap, userId)
                Log.e("faceDetection", "salio del DetectFaces")
                image.close()
            }
            override fun onError(error: ImageCaptureException) {
                Log.d("faceDetection", "error: ImageCaptureException : $error")
            }
        })
}


@Composable
fun CameraLoginPreviewScreen(authenticationController: AuthenticationController, userId: String) {
    val lensFacing = CameraSelector.LENS_FACING_FRONT // Cambio aquí para la cámara frontal
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    // Obtener la configuración local del dispositivo
    val configuration = LocalConfiguration.current
    // Obtener la rotación del dispositivo
    val rotation = configuration.orientation
    val screenSize = if (rotation == 0) Size(720, 1280) else Size(1280, 720)
    val resolutionSelector = ResolutionSelector.Builder().setResolutionStrategy(
        ResolutionStrategy(
            screenSize,
            ResolutionStrategy.FALLBACK_RULE_NONE
        )
    ).build()
    val preview = Preview.Builder()
        .setResolutionSelector(resolutionSelector)
        .build()
    val previewView = remember {
        PreviewView(context)
    }
    val cameraxSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
    val imageCapture = remember {
        ImageCapture.Builder().build()
    }

    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraxSelector, preview, imageCapture)
        preview.setSurfaceProvider(previewView.surfaceProvider)
        Log.d(
            "faceDetection",
            "Camera preview bound to lifecycle with lens facing: $lensFacing"
        ) // Agrego un log aquí
    }
    Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxSize()) {
        AndroidView({ previewView }, modifier = Modifier.fillMaxSize())
        Button(
            onClick = { captureImageForLogin(imageCapture, context, authenticationController, userId) },
            modifier = Modifier.padding(bottom = 50.dp)
        ) {
            Text(text = "Tomar Foto")
        }
        // Añadir imagen superpuesta desde recursos
        val painter = painterResource(id = R.drawable.silueta) // Reemplaza R.drawable.overlay_image con el ID de tu imagen en la carpeta res
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .padding(1.dp) // Ajusta el padding según sea necesario
        )
    }
}

fun captureImageForLogin(imageCapture: ImageCapture, context: Context, authenticationController: AuthenticationController, userId: String) {
    Log.e("faceDetection", "imageCapture Login")
    // Captura la imagen
    imageCapture.takePicture(ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                // Convertir la imagen a un bitmap
                Log.d("faceDetection", "imageCapture 1")
                val bitmap = imageProxyToBitmap(image)
                // Convertir el bitmap a bytes
                val bytes = bitmapToByteArray(bitmap)
                Log.d("faceDetection", "imageCapture 2")
                // Ejecutar la detección de rostros
                val faceDetector = FaceDetectionActivity()
                Log.d("faceDetection", "imageCapture 3")
                faceDetector.detectFacesForLogin(authenticationController, bitmap, userId, context)
                Log.e("faceDetection", "salio del DetectFaces")
                image.close()
            }
            override fun onError(error: ImageCaptureException) {
                Log.d("faceDetection", "error: ImageCaptureException : $error")
            }
        })
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }


private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
    val buffer = image.planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, getBitmapOptions())
}

private fun getBitmapOptions(): BitmapFactory.Options {
    val options = BitmapFactory.Options()
    options.inSampleSize =
        4 // Reduce el tamaño de la imagen a la mitad. Puedes ajustar este valor según tus necesidades.
    return options
}

fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
    return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
}

fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}

private fun imageProxyToBitmapWOBF(image: ImageProxy): Bitmap {
    val buffer = image.planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}

//private fun captureImageJpg(
//    imageCapture: ImageCapture,
//    context: Context,
//    authenticationController: AuthenticationController,
//    userId: String
//) {
//    val name = "CameraxImage.jpeg"
//    val contentValues = ContentValues().apply {
//        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
//        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
//            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
//        }
//    }
//    val outputOptions = ImageCapture.OutputFileOptions
//        .Builder(
//            context.contentResolver,
//            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//            contentValues
//        )
//        .build()
//    imageCapture.takePicture(
//        outputOptions,
//        ContextCompat.getMainExecutor(context),
//        object : ImageCapture.OnImageSavedCallback {
//            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
//                Log.d("faceDetection", "Image captured successfully") // Agrego un log aquí
//                authenticationController.navigateToConfirmation()
//            }
//
//            override fun onError(exception: ImageCaptureException) {
//                Log.e("faceDetection", "Error capturing image: $exception") // Agrego un log aquí
//            }
//
//        })
//}


