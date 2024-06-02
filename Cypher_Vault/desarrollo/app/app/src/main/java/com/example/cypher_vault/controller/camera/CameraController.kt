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
        try {
            val tempFile = File.createTempFile("tempImage", ".jpg", context.cacheDir)
            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(tempFile).build()
            imageCapture.takePicture(
                outputFileOptions,
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        try {
                            val imageBytes = tempFile.readBytes()
                            val imgBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                            val rotatedBitmap = rotateBitmap(imgBitmap, -90f)
                            val grayscaleBitmap = convertToGrayscale(rotatedBitmap)
                            val reductionAmountX = 220
                            val reductionAmountY = 180

                            val boundingBoxInImageCoordinates = Rect(
                                faceOverlayView.boundingBox!!.left * imgBitmap.width / faceOverlayView.imageWidth + reductionAmountX,
                                faceOverlayView.boundingBox!!.top * imgBitmap.height / faceOverlayView.imageHeight + reductionAmountY,
                                faceOverlayView.boundingBox!!.right * imgBitmap.width / faceOverlayView.imageWidth - reductionAmountX,
                                faceOverlayView.boundingBox!!.bottom * imgBitmap.height / faceOverlayView.imageHeight - reductionAmountY
                            )

                            val croppedBitmap = Bitmap.createBitmap(
                                grayscaleBitmap,
                                boundingBoxInImageCoordinates.left,
                                boundingBoxInImageCoordinates.top,
                                boundingBoxInImageCoordinates.width(),
                                boundingBoxInImageCoordinates.height()
                            )

                            val aspectRatio = boundingBoxInImageCoordinates.width().toFloat() / boundingBoxInImageCoordinates.height().toFloat()
                            val targetWidth: Int
                            val targetHeight: Int

                            if (boundingBoxInImageCoordinates.width() > boundingBoxInImageCoordinates.height()) {
                                targetWidth = 112
                                targetHeight = Math.round(targetWidth / aspectRatio)
                            } else {
                                targetHeight = 112
                                targetWidth = Math.round(targetHeight * aspectRatio)
                            }

                            val resizedBitmap = Bitmap.createScaledBitmap(croppedBitmap, targetWidth, targetHeight, false)
                            val finalBitmap = Bitmap.createBitmap(112, 112, Bitmap.Config.ARGB_8888)
                            val canvas = Canvas(finalBitmap)
                            canvas.drawColor(Color.BLACK)

                            val left = (finalBitmap.width - resizedBitmap.width) / 2f
                            val top = (finalBitmap.height - resizedBitmap.height) / 2f
                            canvas.drawBitmap(resizedBitmap, left, top, null)

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
                                    navController.navigateToConfirmation(userId, true)
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("Imagen", "Error al procesar la imagen", e)
                            if (!isRegister)
                                navController.navigateToConfirmation(userId, false)
                        }
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.e("Imagen", "Error al capturar la imagen", exception)
                        if (!isRegister)
                            navController.navigateToConfirmation(userId, false)
                    }
                })
        } catch (e: Exception) {
            Log.e("Imagen", "Error al iniciar la captura de imagen", e)
            if (!isRegister)
                navController.navigateToConfirmation(userId, false)
        }
    }


    fun convertToGrayscale(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height
        val grayscaleBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(grayscaleBitmap)
        val paint = android.graphics.Paint()
        val colorMatrix = android.graphics.ColorMatrix()
        colorMatrix.setSaturation(0f)
        val colorMatrixFilter = android.graphics.ColorMatrixColorFilter(colorMatrix)
        paint.colorFilter = colorMatrixFilter
        canvas.drawBitmap(source, 0f, 0f, paint)

        return grayscaleBitmap
    }


    fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }
}



