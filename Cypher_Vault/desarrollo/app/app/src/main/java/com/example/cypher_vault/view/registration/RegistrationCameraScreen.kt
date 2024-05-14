package com.example.cypher_vault.view.registration

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.util.Log

import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.cypher_vault.controller.authentication.AuthenticationController
import com.example.cypher_vault.controller.camera.CameraController
import com.example.cypher_vault.controller.data.DatabaseController
import com.example.cypher_vault.view.resources.FaceOverlayView
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import com.example.cypher_vault.view.resources.*


private val databaseController = DatabaseController()

@OptIn(ExperimentalGetImage::class)
@Composable
fun RegistrationCameraScreen(authenticationController: AuthenticationController, userId: String) {

    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val isCameraOpen = remember { mutableStateOf(true) }
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
        .build()

    val cameraProvider = cameraProviderFuture.get()
    val preview = Preview.Builder().build()
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

    val faceOverlayView = remember { FaceOverlayView(context) }

    val cameraController = CameraController(context, authenticationController, userId, databaseController)


    imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
            val image = InputImage.fromMediaImage(mediaImage, rotationDegrees)

            val imageView = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            // Actualiza imageWidth e imageHeight.
            faceOverlayView.imageWidth = imageView.width
            faceOverlayView.imageHeight = imageView.height

            val realTimeOpts = FaceDetectorOptions.Builder()
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .enableTracking()
                .build()

            val detector = FaceDetection.getClient(realTimeOpts)
            detector.process(image)
                .addOnSuccessListener { faces ->
                    if (faces.isEmpty()) {
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


                        faceOverlayView.boundingBox = face.boundingBox
                        faceOverlayView.invalidate()


                        if (hasLeftEye == true && hasRightEye == true && hasNose == true && hasMouth && faceOverlayView.isBoundingBoxInsideTarget()) {
                            when (currentOrientation.value) {
                                "front" -> if (face.headEulerAngleY in -10.0..10.0) cameraController.startTimer(
                                    timer,
                                    timerStarted,
                                    timerFinished,
                                    coroutineScope
                                )
                            }
                            if (timer.intValue == 0 && !isImageCaptured.value) {
                                cameraController.captureImage(
                                    context,
                                    imageCapture,
                                    cameraProvider,
                                    isImageCaptured,
                                    coroutineScope,
                                    authenticationController,
                                    faceOverlayView
                                )
                                timer.intValue = 3
                                timerStarted.value = false
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
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }

        }
    }

    LaunchedEffect(cameraProviderFuture) {
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, imageCapture , imageAnalysis, preview)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        if (isCameraOpen.value) {
            CameraPreview(preview)
            AndroidView( {faceOverlayView} )

            when (currentOrientation.value) {
                "front" -> if (timer.value > 0) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${timer.intValue}",
                            color = Color.Black,
                            fontSize = 36.sp,
                            style = textStyle.copy(shadow = Shadow(color = firstColor, offset = Offset(-3f,3f), blurRadius = 0f)),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            "Mire hacia la cámara.",
                            color = Color.Black,
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


