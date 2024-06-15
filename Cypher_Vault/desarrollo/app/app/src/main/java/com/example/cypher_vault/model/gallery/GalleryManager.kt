package com.example.cypher_vault.model.gallery
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.cypher_vault.controller.data.DatabaseController
import com.example.cypher_vault.controller.navigation.NavController
import com.example.cypher_vault.controller.session.SessionController
import com.example.cypher_vault.database.Images
import com.example.cypher_vault.model.dbmanager.DatabaseManager
import com.example.cypher_vault.model.encrypt.EncryptionService
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Base64
import java.util.Date
import java.util.Locale


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


data class QrResult(val result: Boolean)
interface ImageQRAPI {
    @Multipart
    @POST("/compare_faces")
    fun uploadImage(
        @Part image: MultipartBody.Part,
    ): Call<QrResult>
}


val retrofit = Retrofit.Builder()
    .baseUrl("http://<your-server-ip>:5000/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val api = retrofit.create(ImageQRAPI::class.java)

data class ImageResponse(val imageUrl: String)
class GalleryManager {

    val databaseController = DatabaseController()


    @RequiresApi(Build.VERSION_CODES.O)
    fun generateQRCodeForSelectedImages(imageUrls: List<String>): Bitmap {
        val writer = QRCodeWriter()
        val combinedUrls = imageUrls.joinToString(separator = "\n")
        try {
            val bitMatrix = writer.encode(combinedUrls, BarcodeFormat.QR_CODE, 512, 512)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
            return bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
        }
        throw RuntimeException("Error generating QR code")
    }

    fun getImageById(imageId: Long): Images? {
        return images.value.find { it.id == imageId }
    }


    fun capitalizarPrimeraLetra(palabra: String): String {
        if (palabra.isEmpty()) {
            return palabra
        }
        return palabra.substring(0, 1).uppercase(Locale.getDefault())
    }

    fun procesarString(texto: String): String {
        if (texto.length <= 1) {
            return texto
        }

        val minusculas = texto.substring(1).lowercase(Locale.getDefault())
        return if (minusculas.length <= 16) {
            minusculas
        } else {
            minusculas.substring(0, 14) + ".."
        }
    }

    fun formatIncomeDate(income: Long?): String {
        return if (income != null) {
            val date = Date(income)
            val formatter = SimpleDateFormat("HH:mm - dd MMM yyyy", Locale.getDefault())
            formatter.format(date)
        } else {
            "Fecha no disponible"
        }
    }

    fun reduceImageSize(imageBitmap: ImageBitmap, maxMegapixels: Float): ImageBitmap {
        val reducedBitmap = reduceImage(imageBitmap, maxMegapixels)
        return reducedBitmap.asImageBitmap()
    }

    suspend fun updateProfileImage(userId: String, newImage: ByteArray) {
        withContext(Dispatchers.IO) {
            DatabaseManager.updateUserImage(userId, newImage)
        }
    }

    private fun reduceImage(bitmap: ImageBitmap, maxMegapixels: Float): Bitmap {
        val megapixels = (bitmap.width * bitmap.height) / 1000000f

        if (megapixels <= maxMegapixels) {
            return bitmap.asAndroidBitmap()
        }

        val scale = Math.sqrt((maxMegapixels / megapixels).toDouble())

        val matrix = Matrix()
        matrix.postScale(scale.toFloat(), scale.toFloat())

        return Bitmap.createBitmap(bitmap.asAndroidBitmap(), 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun saveBitmapToFile(context: Context, bitmap: Bitmap, fileName: String) {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        val uri: Uri? = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            val outputStream: OutputStream? = resolver.openOutputStream(it)
            outputStream.use {
                if (it != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun saveByteArrayToFile(context: Context, byteArray: ByteArray, fileName: String) {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        val uri: Uri? = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            val outputStream: OutputStream? = resolver.openOutputStream(it)
            outputStream.use {
                it?.write(byteArray)
            }
        }
    }

    fun changePassword(userId: String, text: String) {
        runBlocking {
            databaseController.updatePassword(userId, text)
        }
    }

    fun deleteAccount(userId: String, callback: (Boolean) -> Unit) {
        databaseController.deleteImageGalleryAndUser(userId, callback)
    }

    fun closeSession(context: Context, navController: NavController) {
        SessionController.logout(context,navController)
    }

    val images = mutableStateOf<List<Images>>(listOf())

    fun clearImages() {
        images.value = listOf()
    }

    fun getImagesSize(): Int {
        return images.value.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun loadImagesForUser(userId: String) {
        Log.d("EncryptionServiceGallery", "entrando a loadImages")
        images.value = withContext(Dispatchers.IO) {
            val encryptedImages = DatabaseManager.getImagesForUser(userId)
            val user = DatabaseManager.getUserById(userId)
            if (user?.encryptionSalt != null) {
                val salt = Base64.getDecoder().decode(user.encryptionSalt)
                val password = user.password
                    ?: throw IllegalArgumentException("Password missing for user $userId")
                val encryptionService = EncryptionService()
                val decryptedImages = mutableListOf<Images>()
                encryptedImages.chunked(10).forEach { batch ->
                    batch.map { image ->
                        val decryptedImageData =
                            encryptionService.decrypt(password, image.imageData, salt)
                        decryptedImages.add(image.copy(imageData = decryptedImageData))
                    }
                    // Liberar memoria después de procesar cada lote
                    System.gc()
                }
                decryptedImages
            } else {
                Log.e("EncryptionServiceGallery", "User not found or salt missing for user $userId")
                emptyList()
            }
        }
    }

    fun getGalleryImages(): List<Images> {
        return images.value
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveImage(imageData: ByteArray, userId: String): Deferred<Int> {
        return CoroutineScope(Dispatchers.IO).async {
            try {
                val user = DatabaseManager.getUserById(userId)
                if (user?.encryptionSalt != null) {
                    val salt = Base64.getDecoder().decode(user.encryptionSalt)
                    val password = user.password ?: throw IllegalArgumentException("Password missing for user $userId")
                    val encryptionService = EncryptionService()
                    val encryptedImageData = encryptionService.encrypt(password, imageData, salt)
                    val image = Images(imageData = encryptedImageData, user_id = userId)
                    DatabaseManager.insertImage(image)

                    loadImagesForUser(userId)
                    Log.d("EncryptionServiceGallery", "Image saved successfully for user $userId")
                } else {
                    Log.e("EncryptionServiceGallery", "User not found or salt missing for user $userId")
                }
            } catch (e: Exception) {
                Log.e("EncryptionServiceGallery", "Error saving image for user $userId", e)
            }
        }
    }

    fun saveSecondAuth(userId: String, b: Boolean): Deferred<Unit> {
        return CoroutineScope(Dispatchers.IO).async {
            databaseController.saveSecondAuth(userId, b)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun deleteImgs(userId: String, selectedImageIds: MutableState<List<Long>>) {
        DatabaseManager.deleteImgs(selectedImageIds)
        loadImagesForUser(userId)
    }

}