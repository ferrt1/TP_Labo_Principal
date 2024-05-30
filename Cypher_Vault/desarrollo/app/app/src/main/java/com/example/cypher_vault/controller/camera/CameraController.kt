package com.example.cypher_vault.controller.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Rect
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.core.content.ContextCompat
import com.example.cypher_vault.controller.navigation.NavController
import com.example.cypher_vault.controller.data.DatabaseController
import com.example.cypher_vault.view.resources.FaceOverlayView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File

class CameraController(
    private val context: Context,
    private val navController: NavController,
    private val userId: String,
    private val databaseController: DatabaseController
) {


    fun startTimer(
        timer: MutableIntState,
        timerStarted: MutableState<Boolean>,
        timerFinished: MutableState<Boolean>,
        coroutineScope: CoroutineScope,
        shouldResetTimer: () -> Boolean
    ) {
        if (!timerStarted.value || shouldResetTimer()) {
            timerStarted.value = true
            timer.intValue = 3
            coroutineScope.launch {
                while (timer.intValue > 0) {
                    delay(1000)
                    if (shouldResetTimer()) {
                        timer.intValue = 3
                    } else {
                        timer.intValue--
                    }
                }
                timerFinished.value = true
            }
        }
    }

    fun captureImage(
        context: Context,
        imageCapture: ImageCapture,
        cameraProvider: ProcessCameraProvider,
        state: MutableState<Boolean>,
        coroutineScope: CoroutineScope,
        navController: NavController,
        faceOverlayView: FaceOverlayView,
        isRegister: Boolean
    ) {
        Log.d("Imagen", "entra aca")
        val tempFile = File.createTempFile("tempImage", ".jpg", context.cacheDir)
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(tempFile).build()
        imageCapture.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val imageBytes = tempFile.readBytes()

                    // Convierte los bytes de la imagen en un Bitmap
                    val imgBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                    val rotatedBitmap = rotateBitmap(imgBitmap, -90f)

                    val boundingBoxInImageCoordinates = Rect(
                        faceOverlayView.boundingBox!!.left * imgBitmap.width / faceOverlayView.imageWidth,
                        faceOverlayView.boundingBox!!.top * imgBitmap.height / faceOverlayView.imageHeight,
                        faceOverlayView.boundingBox!!.right * imgBitmap.width / faceOverlayView.imageWidth,
                        faceOverlayView.boundingBox!!.bottom * imgBitmap.height / faceOverlayView.imageHeight
                    )

                    // Recorta el Bitmap para que tenga el mismo tamaño que el boundingBox
                    val croppedBitmap = Bitmap.createBitmap(
                        rotatedBitmap,
                        boundingBoxInImageCoordinates.left,
                        boundingBoxInImageCoordinates.top,
                        boundingBoxInImageCoordinates.width(),
                        boundingBoxInImageCoordinates.height()
                    )

                    val aspectRatio = boundingBoxInImageCoordinates.width()
                        .toFloat() / boundingBoxInImageCoordinates.height().toFloat()

                    val targetWidth: Int
                    val targetHeight: Int

                    if (boundingBoxInImageCoordinates.width() > boundingBoxInImageCoordinates.height()) {
                        // El bounding box es más ancho que alto
                        targetWidth = 112
                        targetHeight = Math.round(targetWidth / aspectRatio)
                    } else {
                        // El bounding box es más alto que ancho
                        targetHeight = 112
                        targetWidth = Math.round(targetHeight * aspectRatio)
                    }

                    val resizedBitmap =
                        Bitmap.createScaledBitmap(croppedBitmap, targetWidth, targetHeight, false)

                    // Crea un Bitmap final de 112x112 con un color de fondo
                    val finalBitmap = Bitmap.createBitmap(112, 112, Bitmap.Config.ARGB_8888)

                    // Rellena el Bitmap final con el color de fondo
                    val canvas = Canvas(finalBitmap)
                    canvas.drawColor(Color.BLACK)

                    // Calcula la posición donde se debe dibujar el Bitmap redimensionado en el Bitmap final
                    val left = (finalBitmap.width - resizedBitmap.width) / 2f
                    val top = (finalBitmap.height - resizedBitmap.height) / 2f

                    // Dibuja el Bitmap redimensionado en el Bitmap final
                    canvas.drawBitmap(resizedBitmap, left, top, null)

                    // Convierte el Bitmap final de nuevo a un array de bytes
                    val stream = ByteArrayOutputStream()
                    finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    val finalImageBytes = stream.toByteArray()

                    val saveImageDeferred = if (isRegister) {
                        databaseController.saveImageLogin(finalImageBytes, userId)
                    } else {
                        databaseController.saveImage(finalImageBytes, userId)
                    }

                    coroutineScope.launch {
                        saveImageDeferred.await()
                        state.value = true
                        state.value = false
                        tempFile.delete()
                        cameraProvider.unbindAll()
                        if (isRegister) {
                            navController.navigateToConfirmationLogin(userId)
                        } else {
                            navController.navigateToConfirmation(userId)
                        }
                    }
                }


                override fun onError(exception: ImageCaptureException) {
                    Log.d("Imagen", "entro aca y tiro error$exception")
                }
            })
    }


    fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }
}



