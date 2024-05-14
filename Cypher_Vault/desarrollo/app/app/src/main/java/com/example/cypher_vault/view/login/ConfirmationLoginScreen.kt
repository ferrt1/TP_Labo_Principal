package com.example.cypher_vault.view.login

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cypher_vault.R
import com.example.cypher_vault.controller.authentication.AuthenticationController
import com.example.cypher_vault.controller.data.DatabaseController
import com.example.cypher_vault.database.ImagesLogin
import com.example.cypher_vault.database.ImagesRegister
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.converter.gson.GsonConverterFactory
import com.example.cypher_vault.view.resources.*

data class RecognitionResult(val result: Boolean)
interface FaceRecognitionAPI {
    @Multipart
    @POST("/compare_faces")
    fun compareFaces(
        @Part image1: MultipartBody.Part,
        @Part image2: MultipartBody.Part
    ): Call<RecognitionResult>
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ConfirmationLoginScreen(authenticationController: AuthenticationController, userId: String) {
    val databaseController = DatabaseController()
    val coroutineScope = rememberCoroutineScope()
    val imageLogin = remember { mutableStateOf<List<ImagesLogin>?>(null) }
    val imageRegister = remember { mutableStateOf<List<ImagesRegister>?>(null) }
    val recognitionResult = remember { mutableStateOf<RecognitionResult?>(null) }
    val context = LocalContext.current
    val imagePrintRegister = remember { mutableStateOf<Bitmap?>(null) }
    val imagePrintLogin = remember { mutableStateOf<Bitmap?>(null) }

    val retrofit = Retrofit.Builder()
        .baseUrl("https://cypherapi-0zaf.onrender.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api = retrofit.create(FaceRecognitionAPI::class.java)

    coroutineScope.launch {
        imageLogin.value = databaseController.getImageLoginForUser(userId)
        imageRegister.value = databaseController.getImageRegistersForUser(userId)
        for (i in imageLogin.value!!.indices) {
            val registerImage = imageRegister.value!![i]
            val loginImage = imageLogin.value!![i]

            val registerBitmap = BitmapFactory.decodeByteArray(
                registerImage.imageData,
                0,
                registerImage.imageData.size
            )
            val loginBitmap =
                BitmapFactory.decodeByteArray(loginImage.imageData, 0, loginImage.imageData.size)

            imagePrintRegister.value = registerBitmap
            imagePrintLogin.value = loginBitmap

            val file1 = convertBitmapToFile("image1", registerBitmap, context)
            val file2 = convertBitmapToFile("image2", loginBitmap, context)

            val requestFile1 = file1.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val requestFile2 = file2.asRequestBody("image/jpeg".toMediaTypeOrNull())

            val body1 = createFormData("image1", "image1.jpg", requestFile1)
            val body2 = createFormData("image2", "image2.jpg", requestFile2)

            coroutineScope.launch(Dispatchers.IO) {
                val call = api.compareFaces(body1, body2)
                val response = call.execute()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        recognitionResult.value = response.body()
                    } else if (response.code() == 400 || response.code() == 500) {
                        databaseController.deleteImageLogin(userId)
                        recognitionResult.value = RecognitionResult(result = false)
                    }
                }
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

        when (recognitionResult.value) {
            null -> {
                Text(
                    "Esperando...",
                    fontSize = 20.sp,
                    fontFamily = fontFamily,
                    color = thirdColor,
                    fontWeight = FontWeight.Bold,
                    )
            }
            else -> {
                if (recognitionResult.value!!.result) {
                    Text(
                        "Reconocimiento exitoso!",
                        fontSize = 20.sp,
                        fontFamily = fontFamily,
                        color = thirdColor,
                        fontWeight = FontWeight.Bold,
                    )
                    databaseController.deleteImageLogin(userId)
                    OutlinedButton(
                        onClick = { authenticationController.navigateToGallery(userId) },
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
                            "Ver galeria",
                            fontFamily = fontFamily,
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Text(
                        "Error en el reconocimiento...",
                        fontSize = 20.sp,
                        fontFamily = fontFamily,
                        color = thirdColor,
                        fontWeight = FontWeight.Bold,
                    )
                    databaseController.deleteImageLogin(userId)
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

fun convertBitmapToFile(fileName: String, bitmap: Bitmap, context: Context): File {
    val width = bitmap.width * 0.5
    val height = bitmap.height * 0.5
    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, width.toInt(), height.toInt(), false)

    val file = File(context.cacheDir, fileName)
    val fileOutputStream = FileOutputStream(file)

    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)

    return file
}
