package com.example.cypher_vault.view.login

import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.cypher_vault.R
import com.example.cypher_vault.controller.navigation.NavController
import com.example.cypher_vault.controller.data.DatabaseController
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.example.cypher_vault.view.resources.*
import com.example.cypher_vault.controller.camera.CameraController

private val databaseController = DatabaseController()

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
fun LoginCamera(navController: NavController, userId: String) {
    val context = LocalContext.current
    val assetManager = context.assets // Obtener el AssetManager
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val lifecycleOwner = LocalLifecycleOwner.current
    val isCameraOpen = remember { mutableStateOf(true) }
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
        .build()

    val cameraController = CameraController(userId, databaseController)

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

    val currentOrientation = remember { mutableStateOf("smile") }

    val faceOverlayView = remember { FaceOverlayView(context) }

    val eyesOpens = remember { mutableIntStateOf(3) }

    val eyesOpenedAfterBlink = remember { mutableStateOf(false) }

    imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
            val image = InputImage.fromMediaImage(mediaImage, rotationDegrees)

            val imageView =
                InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            faceOverlayView.imageWidth = imageView.width
            faceOverlayView.imageHeight = imageView.height

            val realTimeOpts = FaceDetectorOptions.Builder()
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
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
                            val hasLeftEye =
                                face.getContour(FaceContour.LEFT_EYE)?.points?.isNotEmpty()
                            val hasRightEye =
                                face.getContour(FaceContour.RIGHT_EYE)?.points?.isNotEmpty()
                            val hasNose =
                                face.getContour(FaceContour.NOSE_BRIDGE)?.points?.isNotEmpty()
                            val hasMouth =
                                face.getContour(FaceContour.UPPER_LIP_TOP)?.points?.isNotEmpty() == true && face.getContour(
                                    FaceContour.LOWER_LIP_BOTTOM
                                )?.points?.isNotEmpty()!!

                            val smilingProb = face.smilingProbability ?: 0f

                            val leftEyeOpenProbability = face.leftEyeOpenProbability ?: 0f
                            val rightEyeOpenProbability = face.leftEyeOpenProbability ?: 0f

                            faceOverlayView.boundingBox = face.boundingBox
                            faceOverlayView.updateMessage()
                            faceOverlayView.updateInside()
                            faceOverlayView.invalidate()

                            Log.d("faceOverlay", "${faceOverlayView.isBoundingBoxInsideTarget()}")

                            if (faceOverlayView.message == null) {
                                if (hasLeftEye == true && hasRightEye == true && hasNose == true && hasMouth) {
                                    when (currentOrientation.value) {
                                        "smile" -> if (smilingProb > 0.7f) {
                                            currentOrientation.value = "blink1"
                                        }

                                        "blink1" -> {
                                            if (leftEyeOpenProbability < 0.5f && rightEyeOpenProbability < 0.5f) {
                                                if (eyesOpens.intValue == 2 && eyesOpenedAfterBlink.value) {
                                                    currentOrientation.value = "blink2"
                                                    eyesOpenedAfterBlink.value = false
                                                } else {
                                                    eyesOpens.intValue = 2
                                                    eyesOpenedAfterBlink.value = false
                                                }
                                            } else if (leftEyeOpenProbability > 0.5f && rightEyeOpenProbability > 0.5f) {
                                                eyesOpenedAfterBlink.value = true
                                            }
                                        }

                                        "blink2" -> {
                                            if (leftEyeOpenProbability < 0.5f && rightEyeOpenProbability < 0.5f) {
                                                if (eyesOpens.intValue == 1 && eyesOpenedAfterBlink.value) {
                                                    currentOrientation.value = "front"
                                                    eyesOpenedAfterBlink.value = false
                                                } else {
                                                    eyesOpens.intValue = 1
                                                    eyesOpenedAfterBlink.value = false
                                                }
                                            } else if (leftEyeOpenProbability > 0.5f && rightEyeOpenProbability > 0.5f) {
                                                eyesOpenedAfterBlink.value = true
                                            }
                                        }

                                        "front" -> if (face.headEulerAngleY in -10.0..10.0) {
                                            cameraController.startTimer(
                                                timer,
                                                timerStarted,
                                                timerFinished,
                                                coroutineScope,
                                                shouldResetTimer = {
                                                    !faceOverlayView.isBoundingBoxInsideTarget()
                                                }
                                            )
                                        }
                                    }
                                    faceOverlayView.updateState(currentOrientation.value, timer.intValue, eyesOpens.intValue)
                                    if (timer.intValue == 0 && !isImageCaptured.value) {
                                        cameraController.captureImage(
                                            context,
                                            imageCapture,
                                            cameraProvider,
                                            isImageCaptured,
                                            coroutineScope,
                                            navController,
                                            true,
                                            //assetManager
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
                }
                .addOnFailureListener { e ->
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }

    LaunchedEffect(cameraProviderFuture) {
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            imageCapture,
            imageAnalysis,
            preview
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        if (isCameraOpen.value) {
            CameraPreview(preview)
            AndroidView({ faceOverlayView })

            LaunchedEffect(currentOrientation.value, timer.intValue, eyesOpens.intValue) {
                faceOverlayView.updateState(currentOrientation.value, timer.intValue, eyesOpens.intValue)
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
