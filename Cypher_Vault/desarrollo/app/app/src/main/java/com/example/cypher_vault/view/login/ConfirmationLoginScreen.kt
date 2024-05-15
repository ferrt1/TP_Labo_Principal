package com.example.cypher_vault.view.login

import com.example.cypher_vault.view.resources.*
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cypher_vault.controller.authentication.AuthenticationController
import com.example.cypher_vault.controller.data.DatabaseController
import com.example.cypher_vault.database.ImagesLogin
import com.example.cypher_vault.database.ImagesRegister
import kotlinx.coroutines.launch
import org.tensorflow.lite.Interpreter

import java.nio.MappedByteBuffer
import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.util.Log
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import kotlin.math.pow
import kotlin.math.sqrt

// Cargar el modelo de TensorFlow Lite
fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
    val fileDescriptor: AssetFileDescriptor = assetManager.openFd(modelPath)
    val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
    val fileChannel: FileChannel = inputStream.channel
    val startOffset: Long = fileDescriptor.startOffset
    val declaredLength: Long = fileDescriptor.declaredLength

    Log.d("Imagenes", "entro aca loadModelFile $fileChannel")
    return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
}

// Usar el modelo para extraer características del rostro
fun extractFaceFeatures(model: Interpreter, faceImage: Bitmap): FloatArray {
    val faceFeatures = Array(1) { FloatArray(192) } // El modelo produce 192 características
    val faceImageBuffer = convertBitmapToBuffer(faceImage) // Necesitarás implementar esta función
    model.run(faceImageBuffer, faceFeatures)
    return faceFeatures[0] // Devuelve el primer (y único) elemento del array
}

fun convertBitmapToBuffer(bitmap: Bitmap): ByteBuffer {
    Log.d("Imagenes", "entro aca convertBitmapToBuffer")

    val width = bitmap.width
    val height = bitmap.height
    val channels = 3 // Asume que estás trabajando con imágenes en color
    val pixelSize = 4 // Asume que tu modelo espera valores de píxeles en formato float
    val bufferSize = width * height * channels * pixelSize
    val imgData = ByteBuffer.allocateDirect(bufferSize)
    imgData.order(ByteOrder.nativeOrder())
    val intValues = IntArray(width * height)
    bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
    imgData.rewind()
    for (i in 0 until width) {
        for (j in 0 until height) {
            val pixelValue = intValues[j * width + i]
            imgData.putFloat(((pixelValue shr 16 and 0xFF) - 127.5f) / 127.5f)
            imgData.putFloat(((pixelValue shr 8 and 0xFF) - 127.5f) / 127.5f)
            imgData.putFloat(((pixelValue and 0xFF) - 127.5f) / 127.5f)
        }
    }
    return imgData
}


// Calcular la distancia euclidiana entre dos vectores de características
fun compareFaceFeatures(features1: FloatArray, features2: FloatArray): Float {
    Log.d("Imagenes", "entro aca compareFace")
    var sum = 0.0f
    for (i in features1.indices) {
        sum += (features1[i] - features2[i]).pow(2)
    }

    val result = sqrt(sum)
    Log.d("Imagenes", "el resultado es: $result" )

    return result
}


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ConfirmationLoginScreen(authenticationController: AuthenticationController, userId: String) {
    val databaseController = DatabaseController()
    val coroutineScope = rememberCoroutineScope()
    val imageLogin = remember { mutableStateOf<List<ImagesLogin>?>(null) }
    val imageRegister = remember { mutableStateOf<List<ImagesRegister>?>(null) }
    val context = LocalContext.current
    val imagePrintRegister = remember { mutableStateOf<Bitmap?>(null) }
    val imagePrintLogin = remember { mutableStateOf<Bitmap?>(null) }
    val threshold = 0.6f // Define un umbral de similitud
    val result = remember { mutableStateOf<Float?>(null) } // Almacena el resultado del reconocimiento facial

    val model = Interpreter(loadModelFile(context.assets, "mobilefacenet.tflite"))

    coroutineScope.launch {
        imageLogin.value = databaseController.getImageLoginForUser(userId)
        imageRegister.value = databaseController.getImageRegistersForUser(userId)
        for (i in imageLogin.value!!.indices) {
            val registerImage = imageRegister.value!![0]
            val loginImage = imageLogin.value!![0]

            val registerBitmap = BitmapFactory.decodeByteArray(
                registerImage.imageData,
                0,
                registerImage.imageData.size
            )
            val loginBitmap =
                BitmapFactory.decodeByteArray(loginImage.imageData, 0, loginImage.imageData.size)

            imagePrintRegister.value = registerBitmap
            imagePrintLogin.value = loginBitmap

            // Extraer características del rostro usando el modelo de TensorFlow Lite
            val registerFeatures = extractFaceFeatures(model, registerBitmap)
            val loginFeatures = extractFaceFeatures(model, loginBitmap)

            // Comparar las características para determinar si las caras coinciden
            result.value = compareFaceFeatures(registerFeatures, loginFeatures)

            // Actualizar el resultado del reconocimiento
            if (result.value!! < threshold) { // Las caras son similares, por lo que el inicio de sesión es exitoso
                databaseController.deleteImageLogin(userId)
            } else {
                databaseController.deleteImageLogin(userId)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ImageWithLandmarks(imagePrintRegister)
        ImageWithLandmarks(imagePrintLogin)

        when {
            imageLogin.value == null || imageRegister.value == null -> {
                Text(
                    "Cargando imágenes...",
                    fontSize = 20.sp,
                    fontFamily = fontFamily,
                    color = thirdColor,
                    fontWeight = FontWeight.Bold,
                )
            }
            result.value == null -> {
                Text(
                    "Esperando...",
                    fontSize = 20.sp,
                    fontFamily = fontFamily,
                    color = thirdColor,
                    fontWeight = FontWeight.Bold,
                )
            }
            else -> {
                if (result.value!! < threshold) { // Las caras son similares, por lo que el inicio de sesión es exitoso
                    Text(
                        "¡Bienvenido de nuevo!",
                        fontSize = 20.sp,
                        fontFamily = fontFamily,
                        color = thirdColor,
                        fontWeight = FontWeight.Bold,
                    )
                    OutlinedButton(
                        onClick = { authenticationController.navigateToGallery(userId) }, // Navega a la pantalla de inicio
                        shape = RoundedCornerShape(15.dp),
                        border = BorderStroke(3.dp, Color.Gray),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.Gray
                        ),
                        modifier = Modifier
                            .width(200.dp)
                            .padding(top = 30.dp)
                    ) {
                        Text(
                            "Ir a la galeria",
                            fontFamily = fontFamily,
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }else {
                    Text(
                        "Error en el reconocimiento...",
                        fontSize = 20.sp,
                        fontFamily = fontFamily,
                        color = thirdColor,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
        OutlinedButton(
            onClick = { authenticationController.navigateToListLogin() },
            shape = RoundedCornerShape(15.dp),
            border = BorderStroke(3.dp, Color.Gray),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.Gray
            ),
            modifier = Modifier
                .width(200.dp)
                .padding(top = 30.dp)
        ) {
            Text(
                "Iniciar sesión",
                fontFamily = fontFamily,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Composable
fun ImageWithLandmarks(bitmapState: MutableState<Bitmap?>) {
    bitmapState.value?.let { bitmap ->
        val imageBitmap = bitmap.asImageBitmap()
        Image(
            bitmap = imageBitmap,
            contentDescription = "Imagen",
            modifier = Modifier.fillMaxWidth()
        )
    }
}
