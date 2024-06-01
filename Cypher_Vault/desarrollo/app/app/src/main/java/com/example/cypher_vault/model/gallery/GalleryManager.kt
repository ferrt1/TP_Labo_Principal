package com.example.cypher_vault.model.gallery
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Base64
import java.util.Date
import java.util.Locale

class GalleryManager {

    val databaseController = DatabaseController()

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

    fun getGalleryImages(): List<Images> {
        return images.value
    }
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun loadImagesForUser(userId: String) {
        Log.d("EncryptionServiceGallery", "entrando a loadImages")
        images.value = withContext(Dispatchers.IO) {
            val encryptedImages = DatabaseManager.getImagesForUser(userId)
            val user = DatabaseManager.getUserById(userId)
            if (user?.encryptionSalt != null) {
                val salt = Base64.getDecoder().decode(user.encryptionSalt)
                val password = user.password ?: throw IllegalArgumentException("Password missing for user $userId")
                val encryptionService = EncryptionService()
                encryptedImages.map { image ->
                    val decryptedImageData = encryptionService.decrypt(password, image.imageData, salt)
                    image.copy(imageData = decryptedImageData)
                }
            } else {
                Log.e("EncryptionServiceGallery", "User not found or salt missing for user $userId")
                emptyList()
            }
        }
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

    fun deletaImgs(selectedImageIds: MutableState<List<Long>>) {
        databaseController.deletaImgs(selectedImageIds)
    }

}