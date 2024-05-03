package com.example.cypher_vault.view.registration

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.view.View
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
    val timerState = remember { mutableIntStateOf(10) }
    val timerStarted = remember { mutableStateOf(false) } // Nueva variable para controlar el inicio del temporizador

    val imageState = remember { mutableStateOf<ByteArray?>(null) }

    val timerFinished = remember { mutableStateOf(false) }
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
                    var faceDetected = false
                    for (face in faces) {
                        val hasLeftEye = face.getContour(FaceContour.LEFT_EYE)?.points?.isNotEmpty()
                        val hasRightEye = face.getContour(FaceContour.RIGHT_EYE)?.points?.isNotEmpty()
                        val hasNose = face.getContour(FaceContour.NOSE_BRIDGE)?.points?.isNotEmpty()
                        val hasMouth = face.getContour(FaceContour.UPPER_LIP_TOP)?.points?.isNotEmpty() == true && face.getContour(FaceContour.LOWER_LIP_BOTTOM)?.points?.isNotEmpty()!!

                        if (hasLeftEye == true && hasRightEye == true && hasNose == true && hasMouth) {
                            faceDetected = true
                            if (!timerStarted.value) {
                                timerStarted.value = true
                                timerState.value = 3
                                coroutineScope.launch {
                                    while (timerState.value > 0) {
                                        delay(1000)
                                        timerState.value--
                                    }
                                    timerFinished.value = true
                                }
                            }
                        }
                    }

                    if (!faceDetected) {
                        // Si no se detecta un rostro completo, reinicia el temporizador
                        timerState.value = 0
                        timerFinished.value = false
                        timerStarted.value = false
                    }
                }
                .addOnFailureListener { e ->
                    // Manejar cualquier error aquí
                }
                .addOnCompleteListener {

                    if (timerFinished.value) {
                        // Aquí es donde capturas la imagen y la guardas
                        val buffer = mediaImage.planes[0].buffer
                        val bytes = ByteArray(buffer.remaining())
                        buffer.get(bytes)
                        imageState.value = bytes
                        val saveImageDeferred = authenticationController.saveImage(bytes, userId)
                        coroutineScope.launch {
                            saveImageDeferred.await()
                            isCameraOpen.value = false
                            cameraProvider.unbindAll()
                            authenticationController.navigateToConfirmation(userId)
                        }
                    }
                    // Cerrar la imagen cuando hayas terminado
                    imageProxy.close()
                }
        }
    }

    LaunchedEffect(cameraProviderFuture) {
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, imageAnalysis, preview)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (isCameraOpen.value) {
            CameraPreview(preview)
            val textColor = Color(0xFFFFFFFF)
            val textStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 36.sp, color = thirdColor, fontFamily = fontFamily)
            if (timerState.value > 0) {
                Text(
                    text = "${timerState.intValue}",
                    color = textColor,
                    style = textStyle,
                )
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
