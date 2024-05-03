package com.example.cypher_vault.view.registration

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.Log
import android.view.View
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.cypher_vault.controller.authentication.AuthenticationController
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class FaceContourView(context: Context) : View(context) {
    var allContours: Map<String, List<PointF>> = mapOf()

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val paint = Paint()
        paint.color = Color.RED
        paint.style = Paint.Style.FILL
        paint.strokeWidth = 10f

        for ((_, points) in allContours) {
            for (point in points) {
                val scaledPoint = PointF(point.x * scaleX, point.y * scaleY)
                canvas.drawPoint(scaledPoint.x, scaledPoint.y, paint)
            }
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
    val allContours = remember { mutableStateOf<Map<String, List<PointF>>>(mapOf()) }

    val imageState = remember { mutableStateOf<ByteArray?>(null) }

    imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            val realTimeOpts = FaceDetectorOptions.Builder()
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                .build()

            val detector = FaceDetection.getClient(realTimeOpts)
            detector.process(image)
                .addOnSuccessListener { faces ->
                    for (face in faces) {
                        val newContours = mutableMapOf<String, List<PointF>>()
                        newContours["FACE"] = face.getContour(FaceContour.FACE)?.points ?: listOf()
                        newContours["LEFT_EYE"] = face.getContour(FaceContour.LEFT_EYE)?.points ?: listOf()
                        newContours["RIGHT_EYE"] = face.getContour(FaceContour.RIGHT_EYE)?.points ?: listOf()
                        newContours["UPPER_LIP_TOP"] = face.getContour(FaceContour.UPPER_LIP_TOP)?.points ?: listOf()
                        newContours["UPPER_LIP_BOTTOM"] = face.getContour(FaceContour.UPPER_LIP_BOTTOM)?.points ?: listOf()
                        newContours["LOWER_LIP_TOP"] = face.getContour(FaceContour.LOWER_LIP_TOP)?.points ?: listOf()
                        newContours["LOWER_LIP_BOTTOM"] = face.getContour(FaceContour.LOWER_LIP_BOTTOM)?.points ?: listOf()
                        newContours["NOSE_BRIDGE"] = face.getContour(FaceContour.NOSE_BRIDGE)?.points ?: listOf()
                        newContours["NOSE_BOTTOM"] = face.getContour(FaceContour.NOSE_BOTTOM)?.points ?: listOf()

                        allContours.value = newContours

                        if (face.allContours.isNotEmpty()) {

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
                }
                }
                .addOnFailureListener { e ->
                    // Manejar cualquier error aquí
                }
                .addOnCompleteListener {
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
        contentAlignment = Alignment.BottomCenter
    ) {
        if (isCameraOpen.value) {
            CameraPreview(preview)
            FaceContourOverlay(allContours)
            //Text("Pon tu cara en la cámara", modifier = Modifier.align(Alignment.Center))
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