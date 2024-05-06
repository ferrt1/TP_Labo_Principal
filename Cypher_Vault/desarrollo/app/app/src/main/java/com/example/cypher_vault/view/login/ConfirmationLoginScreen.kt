package com.example.cypher_vault.view.login

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import com.example.cypher_vault.R
import com.example.cypher_vault.controller.authentication.AuthenticationController
import com.example.cypher_vault.database.ImagesLogin
import com.example.cypher_vault.database.ImagesRegister
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarker
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import kotlin.math.pow
import kotlin.math.sqrt


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ConfirmationLoginScreen(authenticationController: AuthenticationController, userId: String) {
    val coroutineScope = rememberCoroutineScope()
    val imageLogin = remember { mutableStateOf<List<ImagesLogin>?>(null) }
    val imageRegister = remember { mutableStateOf<List<ImagesRegister>?>(null) }
    val isRecognized = remember { mutableStateOf<Boolean?>(null) }

    val context = LocalContext.current
    val modelFile: String = copyRawResourceToFile(context, R.raw.face_landmarker, "face_landmarker.task").path

    val imagePrintRegister = remember { mutableStateOf<Bitmap?>(null) }
    val imagePrintLogin = remember { mutableStateOf<Bitmap?>(null) }

    val minFaceDetectionConfidence = 0.5f
    val minFaceTrackingConfidence = 0.5f
    val minFacePresenceConfidence = 0.5f
    val maxNumFaces = 1
    // Crear una instancia de FaceLandmarker con las opciones deseadas.
    val baseOptionsBuilder = BaseOptions.builder().setModelAssetPath(modelFile)
    val baseOptions = baseOptionsBuilder.build()

    val optionsBuilder = FaceLandmarker.FaceLandmarkerOptions.builder()
        .setBaseOptions(baseOptions)
        .setMinFaceDetectionConfidence(minFaceDetectionConfidence)
        .setMinTrackingConfidence(minFaceTrackingConfidence)
        .setMinFacePresenceConfidence(minFacePresenceConfidence)
        .setNumFaces(maxNumFaces)
        .setRunningMode(RunningMode.IMAGE)

    val options = optionsBuilder.build()
    val faceLandmarker = FaceLandmarker.createFromOptions(context, options)

    Log.d("compareFaces", "Entrando")

    coroutineScope.launch {
        Log.d("compareFaces", "Obteniendo imágenes de inicio de sesión y registro")
        imageLogin.value = authenticationController.getImageLoginForUser(userId)
        imageRegister.value = authenticationController.getImageRegistersForUser(userId)

        // Supongamos que las listas de imágenes están ordenadas de la misma manera
        for (i in imageLogin.value!!.indices) {
            val registerImage = imageRegister.value!![i]
            val loginImage = imageLogin.value!![i]

            val registerBitmap = BitmapFactory.decodeByteArray(registerImage.imageData, 0, registerImage.imageData.size)
            val loginBitmap = BitmapFactory.decodeByteArray(loginImage.imageData, 0, loginImage.imageData.size)

            imagePrintRegister.value = registerBitmap
            imagePrintLogin.value = loginBitmap

            val mpImageRegister  = BitmapImageBuilder(registerBitmap).build()
            val mpImageLogin  = BitmapImageBuilder(loginBitmap).build()


            // Obtener los landmarks de las imágenes de registro y login.
            val registerLandmarks = faceLandmarker.detect(mpImageRegister)
            val loginLandmarks = faceLandmarker.detect(mpImageLogin)



            // Comparar los landmarks.
            if (!compareLandmarks(registerLandmarks.faceLandmarks()[0], loginLandmarks.faceLandmarks()[0])) {
                Log.d("ConfirmationLoginScreen", "Las imágenes no coinciden")
                isRecognized.value = false
                break
            }
        }

        if (isRecognized.value == null) {
            Log.d("ConfirmationLoginScreen", "Todas las imágenes coinciden")
            isRecognized.value = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text("Estamos verificando el reconocimiento..!")

        when (isRecognized.value) {
            true -> {
                // Aquí puedes redirigir al usuario a otra pantalla
                Text("¡Reconocimiento exitoso!")
                ImageWithLandmarks(imagePrintRegister) // Muestra la imagen de registro con landmarks
                ImageWithLandmarks(imagePrintLogin) // Muestra la imagen de inicio de sesión con landmarks
                authenticationController.deleteImageLogin(userId)
                //faceLandmarker.close()
            }
            false -> {
                // Aquí puedes mostrar un mensaje de error
                Text("Error en el reconocimiento.")
                ImageWithLandmarks(imagePrintRegister) // Muestra la imagen de registro con landmarks
                ImageWithLandmarks(imagePrintLogin) // Muestra la imagen de inicio de sesión con landmarks
                authenticationController.deleteImageLogin(userId)
                //faceLandmarker.close()
            }

            null ->{
                // Aquí puedes mostrar un mensaje de error
                Text("Esperando")
                //faceLandmarker.close()
            }

        }
    }
}
// Función para comparar dos listas de landmarks.
fun compareLandmarks(landmarks1: List<NormalizedLandmark>, landmarks2: List<NormalizedLandmark>): Boolean {
    // Comprobar que las dos listas tienen el mismo tamaño.

    val threshold = 0.1


    if (landmarks1.size != landmarks2.size) {
        return false
    }

    // Calcular la distancia entre los landmarks correspondientes.
    for (i in landmarks1.indices) {
        val landmark1 = landmarks1[i]
        val landmark2 = landmarks2[i]

        val distance = sqrt(
            (landmark1.x() - landmark2.x()).pow(2) +
                    (landmark1.y() - landmark2.y()).pow(2) +
                    (landmark1.z() - landmark2.z()).pow(2)
        )

        Log.d("Authenticate", "Distancia: ${distance}")

        if (distance > threshold) {
            return false
        }
    }


    // Si todas las distancias son menores que el umbral, considerar que las imágenes coinciden.
    return true
}
fun copyRawResourceToFile(context: Context, resourceId: Int, fileName: String): File {
    val inputStream: InputStream = context.resources.openRawResource(resourceId)
    val file = File(context.filesDir, fileName)

    FileOutputStream(file).use { outputStream ->
        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0) {
            outputStream.write(buffer, 0, length)
        }
    }

    return file
}


@Composable
fun ImageWithLandmarks(bitmapState: MutableState<Bitmap?>) {
    bitmapState.value?.let { bitmap ->
        val imageBitmap = bitmap.asImageBitmap()
        Image(
            bitmap = imageBitmap,
            contentDescription = "Imagen con landmarks",
            modifier = Modifier.fillMaxWidth()
        )
    }
}
