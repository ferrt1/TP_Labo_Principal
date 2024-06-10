package com.example.cypher_vault.controller.camera

import android.content.Context
import android.content.res.AssetManager
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
import com.example.cypher_vault.model.mtcnn.MTCNN
import com.example.cypher_vault.view.resources.FaceOverlayView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import androidx.exifinterface.media.ExifInterface
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class CameraController(
    private val userId: String,
    private val databaseController: DatabaseController,
    private val onStartAnalyzing: () -> Unit
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
        isRegister: Boolean,
    ) {
        Log.d("Imagen", "entra aca")
        try {
            onStartAnalyzing()
            val tempFile = File.createTempFile("tempImage", ".jpg", context.cacheDir)
            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(tempFile).build()
            imageCapture.takePicture(
                outputFileOptions,
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        try {
                            val imageBytes = tempFile.readBytes()
                            var imgBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                            try {
                                // Rotar la imagen si es necesario
                                val rotationDegrees = getRotationDegrees(tempFile)
                                imgBitmap = rotateBitmap(imgBitmap, rotationDegrees)
                            } catch (e: Exception) {
                                Log.e("Imagen", "Error al rotar la imagen", e)
                            }

                            try {
                                // Crear InputImage para ML Kit
                                val inputImage = InputImage.fromBitmap(imgBitmap, 0)

                                // Configurar el detector de rostros
                                val realTimeOpts = FaceDetectorOptions.Builder()
                                    .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                                    .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                                    .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                                    .enableTracking()
                                    .build()

                                val detector = FaceDetection.getClient(realTimeOpts)
                                detector.process(inputImage)
                                    .addOnSuccessListener { faces ->
                                        try {
                                            if (faces.isNotEmpty()) {
                                                // Suponemos que solo hay una cara y tomamos la primera
                                                val face = faces[0]
                                                val boundingBox = face.boundingBox

                                                try {
                                                    // Reducir el tamaño del bounding box para que sea solo la cara
                                                    val reductionAmountX = 230
                                                    val reductionAmountY = 230
                                                    val adjustedBoundingBox = Rect(
                                                        boundingBox.left + reductionAmountX,
                                                        boundingBox.top + reductionAmountY,
                                                        boundingBox.right - reductionAmountX,
                                                        boundingBox.bottom - reductionAmountY
                                                    )

                                                    // Recortar la imagen para obtener solo la cara
                                                    val faceBitmap = Bitmap.createBitmap(
                                                        imgBitmap,
                                                        adjustedBoundingBox.left,
                                                        adjustedBoundingBox.top,
                                                        adjustedBoundingBox.width(),
                                                        adjustedBoundingBox.height()
                                                    )

                                                    // Calcular las nuevas dimensiones manteniendo el aspecto original
                                                    val aspectRatio = faceBitmap.width.toFloat() / faceBitmap.height.toFloat()
                                                    val targetWidth: Int
                                                    val targetHeight: Int

                                                    if (aspectRatio > 1) {
                                                        targetWidth = 112
                                                        targetHeight = (112 / aspectRatio).toInt()
                                                    } else {
                                                        targetHeight = 112
                                                        targetWidth = (112 * aspectRatio).toInt()
                                                    }

                                                    // Redimensionar la imagen manteniendo el aspecto original
                                                    val resizedBitmap = Bitmap.createScaledBitmap(faceBitmap, targetWidth, targetHeight, false)

                                                    // Crear una nueva imagen de 112x112 píxeles y dibujar la imagen redimensionada en el centro
                                                    val finalBitmap = Bitmap.createBitmap(112, 112, Bitmap.Config.ARGB_8888)
                                                    val canvas = Canvas(finalBitmap)
                                                    canvas.drawColor(Color.BLACK) // Rellenar el fondo con negro

                                                    val left = (finalBitmap.width - resizedBitmap.width) / 2f
                                                    val top = (finalBitmap.height - resizedBitmap.height) / 2f
                                                    canvas.drawBitmap(resizedBitmap, left, top, null)

                                                    // Guardar la imagen final
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
                                                            navController.navigateToConfirmationLogin(userId, true)
                                                        } else {
                                                            navController.navigateToConfirmation(userId, true, "bien")
                                                        }
                                                    }
                                                } catch (e: Exception) {
                                                    Log.e("Imagen", "Error al recortar y redimensionar la imagen", e)
                                                    if (!isRegister)
                                                        navController.navigateToConfirmation(userId, false, "Error al procesar la imagen")
                                                }
                                            } else {
                                                Log.e("Imagen", "No se detectaron caras")
                                                if (!isRegister)
                                                    navController.navigateToConfirmation(userId, false, "No se detectaron caras")
                                            }
                                        } catch (e: Exception) {
                                            Log.e("Imagen", "Error al procesar las caras detectadas", e)
                                            if (!isRegister)
                                                navController.navigateToConfirmation(userId, false, "Error al procesar la imagen")
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("Imagen", "Error al procesar la imagen", e)
                                        if (!isRegister)
                                            navController.navigateToConfirmation(userId, false, "Error al procesar la imagen")
                                    }
                            } catch (e: Exception) {
                                Log.e("Imagen", "Error al crear InputImage o configurar el detector de rostros", e)
                                if (!isRegister)
                                    navController.navigateToConfirmation(userId, false, "Error al procesar la imagen")
                            }
                        } catch (e: Exception) {
                            Log.e("Imagen", "Error al procesar la imagen", e)
                            if (!isRegister)
                                navController.navigateToConfirmation(userId, false, "Error al procesar la imagen")
                        }
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.e("Imagen", "Error al capturar la imagen", exception)
                        if (!isRegister)
                            navController.navigateToConfirmation(userId, false, "Error al capturar la imagen")
                    }
                })
        } catch (e: Exception) {
            Log.e("Imagen", "Error al iniciar la captura de imagen", e)
            if (!isRegister)
                navController.navigateToConfirmation(userId, false, "Error al iniciar la captura de imagen")
        }
    }




    fun rotateBitmap(bitmap: Bitmap, rotationDegrees: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(rotationDegrees.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun getRotationDegrees(tempFile: File): Int {
        val exif = ExifInterface(tempFile.absolutePath)
        return when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
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


}



