package com.example.cypher_vault.controller.gallery


import android.content.Context
import android.graphics.Bitmap
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.util.Base64


class GalleryController() {

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

}