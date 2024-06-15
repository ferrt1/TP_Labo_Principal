package com.example.cypher_vault.controller.gallery


import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import com.example.cypher_vault.controller.income.UserAccessController
import com.example.cypher_vault.controller.navigation.NavController
import com.example.cypher_vault.database.Images
import com.example.cypher_vault.database.UserIncome
import com.example.cypher_vault.model.dbmanager.DatabaseManager
import com.example.cypher_vault.model.encrypt.EncryptionService
import com.example.cypher_vault.model.gallery.GalleryManager
import com.example.cypher_vault.model.income.UserAccessManager
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.internal.platform.android.AndroidLogHandler.setLevel
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Base64


interface RetrofitService {
    @Multipart
    @POST("/upload")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): Response<ResponseBody>
}

class GalleryController() {
    private val BASE_URL = "https://cypherapi.vercel.app"
    private val logging = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val retrofitService = retrofit.create(RetrofitService::class.java)

    private val userAccessManager = UserAccessManager()
    private val userAccessController = UserAccessController(userAccessManager)
    private val galleryManager = GalleryManager()


    suspend fun performUserIncomeInsertion(userId: String) {
        userAccessController.insertUserIncome(userId).await()
    }

    suspend fun loadAllIncomes(userId: String): MutableState<List<UserIncome>> {
        userAccessController.loadAllIncomes(userId)
        return userAccessController.userIncomes
    }

    suspend fun loadFiveIncomes(userId: String): MutableState<List<UserIncome>> {
        userAccessController.loadLastFiveIncomes(userId)
        return userAccessController.userIncomes
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveImage(imageData: ByteArray, userId: String) {
        galleryManager.saveImage(imageData, userId)

    }

    suspend fun sendImageToServer(imageData: ByteArray): String? {
        return withContext(Dispatchers.IO) {
            try {
                val requestFile = RequestBody.create("image/png".toMediaTypeOrNull(), imageData)
                val body = MultipartBody.Part.createFormData("file", "image.png", requestFile)
                val response: Response<ResponseBody> = retrofitService.uploadImage(body)

                if (response.isSuccessful) {
                    response.body()?.string() // Assuming the server returns the URL as plain text
                } else {
                    Log.e("GalleryController", "Error uploading image: ${response.code()}")
                    null
                }
            } catch (e: Exception) {
                Log.e("GalleryController", "Exception uploading image", e)
                null
            }
        }
    }
    fun generateQRCode(text: String): Bitmap {
        val size = 512 // Tamanho do QR Code
        val hints = mapOf(
            EncodeHintType.CHARACTER_SET to "UTF-8",
            EncodeHintType.MARGIN to 1
        )
        val bitMatrix = MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, size, size, hints)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    }

    fun convertBitmapToFile(fileName: String, bitmap: Bitmap, context: Context): File {
        val file = File(context.cacheDir, fileName)
        file.createNewFile()

        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        val bitmapData = bos.toByteArray()

        val fos = FileOutputStream(file)
        fos.write(bitmapData)
        fos.flush()
        fos.close()

        return file
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun loadImagesForUser(userId: String) {
        galleryManager.loadImagesForUser(userId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun updateProfileImage(userId: String, newImage: ByteArray) {
        galleryManager.updateProfileImage(userId, newImage)
    }

    fun capitalizarPrimeraLetra(palabra: String): String{
        return galleryManager.capitalizarPrimeraLetra(palabra)
    }

    fun procesarString(palabra: String): String{
        return galleryManager.procesarString(palabra)
    }

    fun formatIncomeDate(income: Long?): String{
        return galleryManager.formatIncomeDate(income)
    }

    fun reduceImageSize(bitmap: ImageBitmap, maxMegapixels: Float): ImageBitmap {
        return galleryManager.reduceImageSize(bitmap, maxMegapixels)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun saveByteArrayToFile(context: Context, byteArray: ByteArray, fileName: String){
        return galleryManager.saveByteArrayToFile(context,byteArray,fileName)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun saveBitmapToFile(context: Context, bitmap: Bitmap, fileName: String){
        return galleryManager.saveBitmapToFile(context,bitmap,fileName)
    }

    fun changePassword(userId: String, text: String) {
        galleryManager.changePassword(userId, text)
    }

    fun closeSession(context: Context, navController: NavController): Unit {
        galleryManager.closeSession(context,navController)
        return Unit
    }

    fun deleteAccount(userId: String, onComplete: (Boolean) -> Unit) {
        galleryManager.deleteAccount(userId, onComplete)
    }

    fun getGalleryImages(): List<Images> {
        return galleryManager.getGalleryImages()
    }

    fun saveSecondAuth(userId: String, b: Boolean) {
        galleryManager.saveSecondAuth(userId, b)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun deleteImg(userId: String, selectedImageIds: MutableState<List<Long>>) {
        galleryManager.deleteImgs(userId, selectedImageIds)
    }

    fun clearImages(){
        galleryManager.clearImages()
    }

    fun getImagesSize(): Int {
        return galleryManager.getImagesSize()
    }

}