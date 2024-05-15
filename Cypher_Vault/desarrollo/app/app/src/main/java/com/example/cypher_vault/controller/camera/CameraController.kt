package com.example.cypher_vault.controller.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Rect
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.core.content.ContextCompat
import com.example.cypher_vault.controller.authentication.AuthenticationController
import com.example.cypher_vault.controller.data.DatabaseController
import com.example.cypher_vault.view.resources.FaceOverlayView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File

class CameraController(
    private val context: Context,
    private val authenticationController: AuthenticationController,
    private val userId: String,
    private val databaseController: DatabaseController
) {
//    fun startImageAnalysis() {
//        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
//            // ... implementación ...
//        }
//    }

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

    fun captureImageRegister(context: Context,
                             imageCapture: ImageCapture,
                             cameraProvider: ProcessCameraProvider,
                             state: MutableState<Boolean>,
                             coroutineScope: CoroutineScope,
                             authenticationController: AuthenticationController,
                             faceOverlayView: FaceOverlayView
    ) {
        Log.d("Imagen", "entra aca")
        val tempFile = File.createTempFile("tempImage", ".jpg", context.cacheDir)
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(tempFile).build()
        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(context), object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val imageBytes = tempFile.readBytes()

                // Convierte los bytes de la imagen en un Bitmap
                val imgBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                val rotatedBitmap = rotateBitmap(imgBitmap, -90f)

                // Calcula las coordenadas del targetBox en las coordenadas de la imagen
                val targetBoxInImageCoordinates = Rect(
                    faceOverlayView.targetBox!!.left * rotatedBitmap.width / faceOverlayView.width,
                    faceOverlayView.targetBox!!.top * rotatedBitmap.height / faceOverlayView.height,
                    faceOverlayView.targetBox!!.right * rotatedBitmap.width / faceOverlayView.width,
                    faceOverlayView.targetBox!!.bottom * rotatedBitmap.height / faceOverlayView.height
                )

                // Recorta el Bitmap para que tenga el mismo tamaño que el targetBox
                val croppedBitmap = Bitmap.createBitmap(rotatedBitmap, targetBoxInImageCoordinates.left,
                    targetBoxInImageCoordinates.top, targetBoxInImageCoordinates.width(), targetBoxInImageCoordinates.height())

                val newWidth = croppedBitmap.width / 3
                val newHeight = croppedBitmap.height / 3

                // Crea un nuevo Bitmap con la mitad del tamaño original
                val resizedBitmap = Bitmap.createScaledBitmap(croppedBitmap, 112, 112, false)


                // Convierte el Bitmap recortado de nuevo a un array de bytes
                val stream = ByteArrayOutputStream()
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                val resizedImageBytes = stream.toByteArray()

                val saveImageDeferred = databaseController.saveImage(resizedImageBytes, userId)
                coroutineScope.launch {
                    saveImageDeferred.await()
                    state.value = true
                    state.value = false
                    tempFile.delete() // Borra el archivo temporal después de guardar la imagen en la base de datos
                    cameraProvider.unbindAll()
                    authenticationController.navigateToConfirmation(userId)
                }
            }


            override fun onError(exception: ImageCaptureException) {
                Log.d("Imagen", "entro aca y tiro error$exception")
            }
        })
    }

    fun captureImageLogin(context: Context,
                          imageCapture: ImageCapture,
                          cameraProvider: ProcessCameraProvider,
                          state: MutableState<Boolean>,
                          coroutineScope: CoroutineScope,
                          authenticationController: AuthenticationController,
                          faceOverlayView: FaceOverlayView
    ) {
        Log.d("Imagen", "entra aca")
        val tempFile = File.createTempFile("tempImage", ".jpg", context.cacheDir)
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(tempFile).build()
        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(context), object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val imageBytes = tempFile.readBytes()

                // Convierte los bytes de la imagen en un Bitmap
                val imgBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                val rotatedBitmap = rotateBitmap(imgBitmap, -90f)

                // Calcula las coordenadas del targetBox en las coordenadas de la imagen
                val targetBoxInImageCoordinates = Rect(
                    faceOverlayView.targetBox!!.left * rotatedBitmap.width / faceOverlayView.width,
                    faceOverlayView.targetBox!!.top * rotatedBitmap.height / faceOverlayView.height,
                    faceOverlayView.targetBox!!.right * rotatedBitmap.width / faceOverlayView.width,
                    faceOverlayView.targetBox!!.bottom * rotatedBitmap.height / faceOverlayView.height
                )

                // Recorta el Bitmap para que tenga el mismo tamaño que el targetBox
                val croppedBitmap = Bitmap.createBitmap(rotatedBitmap, targetBoxInImageCoordinates.left,
                    targetBoxInImageCoordinates.top, targetBoxInImageCoordinates.width(), targetBoxInImageCoordinates.height())


                // Crea un nuevo Bitmap con la mitad del tamaño original
                val resizedBitmap = Bitmap.createScaledBitmap(croppedBitmap, 112, 112, false)


                // Convierte el Bitmap recortado de nuevo a un array de bytes
                val stream = ByteArrayOutputStream()
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                val resizedImageBytes = stream.toByteArray()

                val saveImageDeferred = databaseController.saveImageLogin(resizedImageBytes, userId)
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

    fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }



}
