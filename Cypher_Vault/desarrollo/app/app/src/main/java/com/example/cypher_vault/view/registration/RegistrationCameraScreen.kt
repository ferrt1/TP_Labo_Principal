package com.example.cypher_vault.view.registration

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.ImageFormat
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.cypher_vault.controller.authentication.AuthenticationController
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import kotlin.math.abs


class FaceContourView(context: Context) : View(context) {
    private var allContours: Map<String, List<PointF>> = mapOf()
    private val paint = Paint()

    init {
        paint.color = 255
        paint.style = Paint.Style.FILL
        paint.strokeWidth = 5f
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (allContours.isNotEmpty()) {
            val allPoints = allContours.values.flatten()
            val minX = allPoints.minOfOrNull { it.x }
            val maxX = allPoints.maxOfOrNull { it.x }
            val minY = allPoints.minOfOrNull { it.y }
            val maxY = allPoints.maxOfOrNull { it.y }

            if (minX != null && maxX != null && minY != null && maxY != null) {
                // Dibuja el rectángulo delimitador alrededor de la cara
                paint.style = Paint.Style.STROKE
                canvas.drawRect(minX, minY, maxX, maxY, paint)

                // Dibuja los puntos de contorno
                paint.style = Paint.Style.FILL
                for ((_, points) in allContours) {
                    for (point in points) {
                        canvas.drawPoint(point.x, point.y, paint)
                    }
                }
            }
        }
        drawDefaultFaceContour(canvas)
    }




    private fun drawDefaultFaceContour(canvas: Canvas) {
        val paint = Paint()
        paint.color = 255
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 10f

        // Aquí es donde defines el contorno facial predeterminado.
        // Deberás ajustar estos valores según tus necesidades.
        val defaultFaceContour = listOf(
            // PointF(0f, 0f),  // Punto de inicio

            PointF(350f, 800f),  // Punto de la ceja derecha
            PointF(350f, 950f),  // Punto del ojo derecho

            PointF(750f, 800f),  // Punto de la ceja izquierda
            PointF(750f, 950f),  // Punto del ojo izquierdo


            PointF(550f, 1100f),  // Punto de la nariz


            PointF(550f, 1400f)   // Punto de boca
        )

        for (point in defaultFaceContour) {
            canvas.drawPoint(point.x, point.y, paint)
        }
    }

    fun updateFaceContours(newContours: Map<String, List<PointF>>) {
        allContours = newContours
        invalidate()
    }
}

@Composable
fun FaceContourOverlay(allContours: MutableState<Map<String, List<PointF>>>) {
    val context = LocalContext.current
    val faceContourView = remember { FaceContourView(context) }

    LaunchedEffect(allContours.value) {
        faceContourView.updateFaceContours(allContours.value)
    }

    AndroidView(
        factory = { faceContourView },
        modifier = Modifier.fillMaxSize()
    )
}


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

    val imageState = remember { mutableStateOf<ByteArray?>(null) }

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
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .enableTracking()
                .build()

            val detector = FaceDetection.getClient(realTimeOpts)
            detector.process(image)
                .addOnSuccessListener { faces ->
                    if (faces.isEmpty()) {
                        // Si no hay rostros en la imagen, detén el temporizador y restablécelo a 3
                        timer.value = 3
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

                                "left" -> if (face.headEulerAngleY > 30) startTimer(
                                    timer,
                                    timerStarted,
                                    timerFinished,
                                    coroutineScope
                                )

                                "right" -> if (face.headEulerAngleY < -30) startTimer(
                                    timer,
                                    timerStarted,
                                    timerFinished,
                                    coroutineScope
                                )

                                "below" -> if (face.headEulerAngleX < -20) startTimer(
                                    timer,
                                    timerStarted,
                                    timerFinished,
                                    coroutineScope
                                )

                                "above" -> if (face.headEulerAngleX > 20) startTimer(
                                    timer,
                                    timerStarted,
                                    timerFinished,
                                    coroutineScope
                                )
                            }

                            if (timer.value == 0 && !isImageCaptured.value) {
                                captureImage(
                                    mediaImage,
                                    isImageCaptured,
                                    coroutineScope,
                                    authenticationController,
                                    userId
                                )
                                timer.value = 3 // Reinicia el temporizador
                                timerStarted.value = false // Reinicia el estado del temporizador
                                isImageCaptured.value =
                                    false // Reinicia el estado de la captura de la imagen

                                // Cambia a la siguiente orientación
                                currentOrientation.value = when (currentOrientation.value) {
                                    "front" -> "left"
                                    "left" -> "right"
                                    "right" -> "below"
                                    "below" -> "above"
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
                        cameraProvider.unbindAll()
                        // Muestra un Toast con el mensaje "¡Listo!"
                        Toast.makeText(context, "¡Listo!", Toast.LENGTH_SHORT).show()
                        // Agrega un retraso antes de navegar a la pantalla de confirmación
                        coroutineScope.launch {
                            delay(2000) // Espera 2 segundos
                            authenticationController.navigateToConfirmation(userId)
                        }
                    }
                    imageProxy.close()
                }

        }
    }

    LaunchedEffect(cameraProviderFuture) {
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, imageAnalysis, preview)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        if (isCameraOpen.value) {
            CameraPreview(preview)
            val textColor = Color(0xFFFFFFFF)
            val textStyle =
                TextStyle(fontWeight = FontWeight.Bold, color = thirdColor, fontFamily = fontFamily)

            when (currentOrientation.value) {
                "front" -> if (timer.value > 0) {
                    Column {
                        Text(
                            text = "${timer.intValue}",
                            color = textColor,
                            style = textStyle,
                            fontSize = 36.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            "Por favor, Mire hacia la cámara.",
                            color = textColor,
                            style = textStyle,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                "left" -> if (timer.value > 0) {
                    Column {
                        Text(
                            text = "${timer.intValue}",
                            color = textColor,
                            style = textStyle,
                            fontSize = 36.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            "Por favor, gire la cabeza hacia la izquierda.",
                            color = textColor,
                            style = textStyle,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                "right" -> if (timer.value > 0) {
                    Column {
                        Text(
                            text = "${timer.intValue}",
                            color = textColor,
                            style = textStyle,
                            fontSize = 36.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            "Por favor, gire la cabeza hacia la derecha.",
                            color = textColor,
                            style = textStyle,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                "below" -> if (timer.value > 0) {
                    Column {
                        Text(
                            text = "${timer.intValue}",
                            color = textColor,
                            style = textStyle,
                            fontSize = 36.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            "Por favor, gire la cabeza hacia abajo.",
                            color = textColor,
                            style = textStyle,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                "above" -> if (timer.value > 0) {
                    Column {
                        Text(
                            text = "${timer.intValue}",
                            color = textColor,
                            style = textStyle,
                            fontSize = 36.sp,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            "Por favor, gire la cabeza hacia arriba.",
                            color = textColor,
                            style = textStyle,
                            textAlign = TextAlign.Center
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

fun startTimer(timer: MutableIntState, timerStarted: MutableState<Boolean>, timerFinished: MutableState<Boolean>, coroutineScope: CoroutineScope) {
    if (!timerStarted.value) {
        timerStarted.value = true
        timer.value = 3
        coroutineScope.launch {
            while (timer.value > 0) {
                delay(1000)
                timer.value--
            }
            timerFinished.value = true
        }
    }
}

fun captureImage(mediaImage: Image, state: MutableState<Boolean>, coroutineScope: CoroutineScope, authenticationController: AuthenticationController, userId: String) {
    val jpegBytes = convertYUV420888ToJpeg(mediaImage)
    val saveImageDeferred = authenticationController.saveImage(jpegBytes, userId)
    coroutineScope.launch {
        saveImageDeferred.await()
        state.value = true
        state.value = false
    }
}

fun convertYUV420888ToJpeg(image: Image): ByteArray {
    val yBuffer = image.planes[0].buffer
    val uBuffer = image.planes[1].buffer
    val vBuffer = image.planes[2].buffer

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)

    //U and V are swapped
    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 100, out)

    return out.toByteArray()
}



fun isUserFaceAligned(face: Face): Boolean {
    // Aquí es donde defines el contorno facial predeterminado.
    // Deberás ajustar estos valores según tus necesidades.
    val defaultFaceContour = listOf(
        //PointF(0f, 0f),  // Punto de inicio


        PointF(350f, 800f),  // Punto de la ceja derecha
        PointF(350f, 950f),  // Punto del ojo derecho

        PointF(750f, 800f),  // Punto de la ceja izquierda
        PointF(750f, 950f),  // Punto del ojo izquierdo


        PointF(550f, 1100f),  // Punto de la nariz


        PointF(550f, 1400f)   // Punto de boca
    )

    // Aquí es donde verificas si los puntos de contorno detectados para los ojos, la nariz y la boca del usuario
    // están dentro de las áreas correspondientes en el contorno facial predeterminado.
    // Deberás implementar la lógica de esta verificación según tus necesidades.
    for (point in defaultFaceContour) {
        if (!isPointInsideArea(face.getContour(FaceContour.FACE)?.points ?: listOf(), point)) {
            return false
        }
    }

    return true
}

fun isPointInsideArea(faceContourPoints: List<PointF>, point: PointF): Boolean {
    var j = faceContourPoints.size - 1
    var inside = false
    for (i in faceContourPoints.indices) {
        if (faceContourPoints[i].y > point.y != faceContourPoints[j].y > point.y &&
            point.x < (faceContourPoints[j].x - faceContourPoints[i].x) * (point.y - faceContourPoints[i].y) / (faceContourPoints[j].y - faceContourPoints[i].y) + faceContourPoints[i].x) {
            inside = !inside
        }
        j = i
    }
    return inside
}