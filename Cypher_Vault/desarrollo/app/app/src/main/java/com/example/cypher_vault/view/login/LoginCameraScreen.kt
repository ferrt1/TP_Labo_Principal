package com.example.cypher_vault.view.login

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.cypher_vault.controller.authentication.AuthenticationController
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
fun LoginCamera(authenticationController: AuthenticationController, userId: String) {
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val isCameraOpen = remember { mutableStateOf(true) }
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
        .build()

    val cameraProvider = cameraProviderFuture.get()

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

    val imageAnalysis = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()

    val coroutineScope = rememberCoroutineScope()

    val imageCapture = ImageCapture.Builder()
        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
        .build()

    val timer = remember { mutableIntStateOf(3) }
    val timerStarted = remember { mutableStateOf(false) }
    val timerFinished = remember { mutableStateOf(false) }

    val isImageCaptured = remember { mutableStateOf(false) }

    val currentOrientation = remember { mutableStateOf("front") }

    imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            val realTimeOpts = FaceDetectorOptions.Builder()
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .enableTracking()
                .build()

            val detector = FaceDetection.getClient(realTimeOpts)
            detector.process(image)
                .addOnSuccessListener { faces ->
                    if (faces.isEmpty()) {
                        // Si no hay rostros en la imagen, detén el temporizador y restablécelo a 3
                        timer.intValue = 3
                        timerStarted.value = false
                    } else {
                        for (face in faces) {
                            val hasLeftEye = face.getContour(FaceContour.LEFT_EYE)?.points?.isNotEmpty()
                            val hasRightEye =
                                face.getContour(FaceContour.RIGHT_EYE)?.points?.isNotEmpty()
                            val hasNose = face.getContour(FaceContour.NOSE_BRIDGE)?.points?.isNotEmpty()
                            val hasMouth =
                                face.getContour(FaceContour.UPPER_LIP_TOP)?.points?.isNotEmpty() == true && face.getContour(
                                    FaceContour.LOWER_LIP_BOTTOM
                                )?.points?.isNotEmpty()!!

                            if (hasLeftEye == true && hasRightEye == true && hasNose == true && hasMouth) {
                                when (currentOrientation.value) {
                                    "front" -> if (face.headEulerAngleY in -10.0..10.0) startTimer(
                                        timer,
                                        timerStarted,
                                        timerFinished,
                                        coroutineScope
                                    )
                                }

                                if (timer.intValue == 0 && !isImageCaptured.value) {
                                    captureImage(
                                        context,
                                        imageCapture,
                                        cameraProvider,
                                        isImageCaptured,
                                        coroutineScope,
                                        authenticationController,
                                        userId
                                    )
                                    timer.intValue = 3 // Reinicia el temporizador
                                    timerStarted.value = false // Reinicia el estado del temporizador
                                    isImageCaptured.value = false
                                    currentOrientation.value = when (currentOrientation.value) {
                                        "front" -> "done"
                                        else -> "done"
                                    }
                                }
                            }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    // Manejar cualquier error aquí
                }
                .addOnCompleteListener {
                    if (currentOrientation.value == "done") {
                        // Muestra un Toast con el mensaje "¡Listo!"
                        Toast.makeText(context, "¡Listo!", Toast.LENGTH_SHORT).show()
                        // Agrega un retraso antes de navegar a la pantalla de confirmación
                    }
                    imageProxy.close()
                }

        }
    }

    LaunchedEffect(cameraProviderFuture) {
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector,imageCapture, imageAnalysis, preview)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        if (isCameraOpen.value) {
            CameraPreview(preview)

            val textStyle =
                TextStyle(fontWeight = FontWeight.ExtraBold, color = thirdColor, fontFamily = fontFamily)

            when (currentOrientation.value) {
                "front" -> if (timer.intValue > 0) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${timer.intValue}",
                            color = Color.White,
                            fontSize = 36.sp,
                            style = textStyle.copy(shadow = Shadow(color = firstColor, offset = Offset(-3f,3f), blurRadius = 0f)),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            "Mire hacia la cámara.",
                            color = Color.White,
                            fontSize = 36.sp,
                            style = textStyle.copy(shadow = Shadow(color = firstColor, offset = Offset(-3f,3f), blurRadius = 0f)),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }

}

fun captureImage(context: Context,
                 imageCapture: ImageCapture,
                 cameraProvider: ProcessCameraProvider,
                 state: MutableState<Boolean>,
                 coroutineScope: CoroutineScope,
                 authenticationController: AuthenticationController,
                 userId: String) {
    Log.d("Imagen", "entra aca")
    val tempFile = File.createTempFile("tempImage", ".jpg", context.cacheDir)
    val outputFileOptions = ImageCapture.OutputFileOptions.Builder(tempFile).build()
    imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(context), object : ImageCapture.OnImageSavedCallback {
        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            val imageBytes = tempFile.readBytes()

            // Convierte los bytes de la imagen en un Bitmap
            val imgBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

            val newWidth = imgBitmap.width / 4
            val newHeight = imgBitmap.height / 4

            // Crea un nuevo Bitmap con la mitad del tamaño original
            val resizedBitmap = Bitmap.createScaledBitmap(imgBitmap, newWidth, newHeight, false)

            // Convierte el Bitmap redimensionado de nuevo a un array de bytes
            val stream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val resizedImageBytes = stream.toByteArray()

            val saveImageDeferred = authenticationController.saveImageLogin(resizedImageBytes, userId)
            coroutineScope.launch {
                saveImageDeferred.await()
                state.value = true
                state.value = false
                tempFile.delete() // Borra el archivo temporal después de guardar la imagen en la base de datos
                cameraProvider.unbindAll()
                authenticationController.navigateToConfirmationLogin(userId)
            }
        }


        override fun onError(exception: ImageCaptureException) {
            Log.d("Imagen", "entro aca y tiro error$exception")
        }
    })
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

fun startTimer(timer: MutableIntState, timerStarted: MutableState<Boolean>, timerFinished: MutableState<Boolean>, coroutineScope: CoroutineScope) {
    if (!timerStarted.value) {
        timerStarted.value = true
        timer.intValue = 3
        coroutineScope.launch {
            while (timer.intValue > 0) {
                delay(1000)
                timer.intValue--
            }
            timerFinished.value = true
        }
    }
}