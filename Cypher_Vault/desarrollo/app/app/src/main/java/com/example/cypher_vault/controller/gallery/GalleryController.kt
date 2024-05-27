package com.example.cypher_vault.controller.gallery


import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import com.example.cypher_vault.controller.data.DatabaseController
import com.example.cypher_vault.controller.income.UserAccessController
import com.example.cypher_vault.database.Images
import com.example.cypher_vault.database.UserIncome
import com.example.cypher_vault.database.UserPremium
import com.example.cypher_vault.database.UserPremiumDao
import com.example.cypher_vault.model.dbmanager.DatabaseManager
import com.example.cypher_vault.model.gallery.GalleryManager
import com.example.cypher_vault.model.income.UserAccessManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


class GalleryController() {

    val userAccessManager = UserAccessManager()
    val userAccessController = UserAccessController(userAccessManager)
    val galleryManager = GalleryManager()

    suspend fun performUserIncomeInsertion(userId: String) {
        userAccessController.insertUserIncome(userId).await()
    }

    suspend fun loadAllIncomes(userId: String): MutableState<List<UserIncome>> {
        userAccessController.loadAllIncomes(userId)
        return userAccessController.userIncomes
    }

    fun saveImage(imageData: ByteArray, userId: String): Deferred<Unit> {
        return CoroutineScope(Dispatchers.IO).async {
            val image = Images(imageData = imageData, user_id = userId)
            DatabaseManager.insertImage(image)
        }
    }

    val images = mutableStateOf<List<Images>>(listOf())

    suspend fun loadImagesForUser(userId: String) {
        images.value = withContext(Dispatchers.IO) {
            DatabaseManager.getImagesForUser(userId)
        }
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


}