package com.example.cypher_vault.controller.data

import com.example.cypher_vault.database.ImagesLogin
import com.example.cypher_vault.database.ImagesRegister
import com.example.cypher_vault.database.User
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
    init{
        getAllUsers()
    }

    private fun getAllUsers() {
        CoroutineScope(Dispatchers.IO).launch {
            _users.value = DatabaseManager.getAllUsers()
        }
    }

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> get() = _users

    private suspend fun getUserById(userId: String): User? {
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
}