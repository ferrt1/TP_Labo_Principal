package com.example.cypher_vault.controller.data

import com.example.cypher_vault.database.ImagesLogin
import com.example.cypher_vault.database.ImagesRegister
import com.example.cypher_vault.database.User
import com.example.cypher_vault.database.UserIncome
import com.example.cypher_vault.model.dbmanager.DatabaseManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    suspend fun getLastIncome(): List<UserIncome?> {
        return withContext(Dispatchers.IO) {
            DatabaseManager.getLastIncome()
        }
    }

    suspend fun hasIncomeOnDay(specificIncome: Long): Boolean {
        return withContext(Dispatchers.IO) {
            DatabaseManager.hasIncomeOnDay(specificIncome) > 0
        }
    }

    suspend fun getAllIncomes(): List<UserIncome?> {
        return withContext(Dispatchers.IO) {
            DatabaseManager.getAllIncomes()
        }
    }

    suspend fun getLastTenIncomes(): List<UserIncome?> {
        return withContext(Dispatchers.IO) {
            DatabaseManager.getLastTenIncomes()
        }
    }
}