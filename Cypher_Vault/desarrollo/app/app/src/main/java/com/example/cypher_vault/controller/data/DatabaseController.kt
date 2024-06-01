package com.example.cypher_vault.controller.data

import android.util.Log
import androidx.compose.runtime.MutableState
import com.example.cypher_vault.database.ImagesLogin
import com.example.cypher_vault.database.ImagesRegister
import com.example.cypher_vault.database.User
import com.example.cypher_vault.database.UserIncome
import com.example.cypher_vault.database.UserPremium
import com.example.cypher_vault.model.dbmanager.DatabaseManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DatabaseController(){

    suspend fun getUserById(userId: String): User? {
        return withContext(Dispatchers.IO) {
            DatabaseManager.getUserById(userId)
        }
    }

    suspend fun comparePasswords(userId: String, password: String): Boolean {
        val user = getUserById(userId)
        return user?.password == password
    }


    fun saveImage(imageData: ByteArray, userId: String): Deferred<Unit> {
        return CoroutineScope(Dispatchers.IO).async {
            val imageRegister = ImagesRegister(imageData = imageData, user_id = userId)
            DatabaseManager.insertImageRegister(imageRegister)
        }
    }

    suspend fun getImageRegistersForUser(userId: String): List<ImagesRegister> {
        return withContext(Dispatchers.IO) {
            DatabaseManager.getImageRegistersForImage(userId)
        }
    }

    fun saveImageLogin(imageData: ByteArray, userId: String): Deferred<Unit> {
        return CoroutineScope(Dispatchers.IO).async {
            val imageLogin = ImagesLogin(imageData = imageData, user_id = userId)
            DatabaseManager.insertImageLogin(imageLogin)
        }
    }

    fun deleteImageLogin(userId: String): Deferred<Unit> {
        return CoroutineScope(Dispatchers.IO).async {
            DatabaseManager.deleteLoginImagesForUser(userId)
        }
    }

    suspend fun getImageLoginForUser(userId: String): List<ImagesLogin> {
        return withContext(Dispatchers.IO) {
            DatabaseManager.getImageLoginForImage(userId)
        }
    }

    fun getLastImageLogin(userId: String): ImagesLogin? {
        var imageLogin: ImagesLogin? = null
        CoroutineScope(Dispatchers.IO).launch {
            imageLogin = withContext(Dispatchers.IO) {
                DatabaseManager.getLastImageLoginForUser(userId)
            }
        }
        return imageLogin
    }

    suspend fun insertIncome(userIncome: UserIncome) {
        withContext(Dispatchers.IO) {
            DatabaseManager.insertIncome(userIncome)
        }
    }

    suspend fun getLastIncome(userId: String): List<UserIncome?> {
        return withContext(Dispatchers.IO) {
            DatabaseManager.getLastIncome(userId)
        }
    }

    suspend fun hasIncomeOnDay(userId: String, specificIncome: Long): Boolean {
        return withContext(Dispatchers.IO) {
            DatabaseManager.hasIncomeOnDay(userId,specificIncome) > 0
        }
    }

    suspend fun getAllIncomes(userId: String): List<UserIncome?> {
        return withContext(Dispatchers.IO) {
            DatabaseManager.getAllIncomes(userId)
        }
    }

    suspend fun getLastFiveIncomes(userId: String): List<UserIncome?> {
        return withContext(Dispatchers.IO) {
            DatabaseManager.getLastFiveIncomes(userId)
        }
    }

    suspend fun insertPremiumActiveAccount(userPremium: UserPremium) {
        withContext(Dispatchers.IO) {
            DatabaseManager.insertPremiumActiveAccount(userPremium)
        }
    }

    suspend fun getPremiumActiveAccount(userId: String): UserPremium? {
        return withContext(Dispatchers.IO) {
            DatabaseManager.getPremiumActiveAccount(userId)
        }
    }

    suspend fun updatePassword(uid: String, newPassword: String){
        withContext(Dispatchers.IO){
            DatabaseManager.updatePassword(uid,newPassword)
        }
    }
    fun deleteImageGalleryAndUser(uid: String, callback: (Boolean) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                DatabaseManager.deleteImgUser(uid)
                DatabaseManager.deleteUser(uid)
                withContext(Dispatchers.Main) {
                    callback(true)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback(false)
                }
            }
        }
    }

    suspend fun saveSecondAuth(userId: String, b: Boolean) {
        withContext(Dispatchers.IO){
            DatabaseManager.saveSecondAuth(userId,b)
        }
    }

    fun deletaImgs(selectedImageIds: MutableState<List<Long>>){
        CoroutineScope(Dispatchers.IO).launch {
            DatabaseManager.deleteImgs(selectedImageIds)
        }
    }
}