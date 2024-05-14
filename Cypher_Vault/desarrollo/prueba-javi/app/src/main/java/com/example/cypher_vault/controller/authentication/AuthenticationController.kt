package com.example.cypher_vault.controller.authentication
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.navigation.NavController
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
import java.util.UUID

import com.example.cypher_vault.controller.MessageController.*


class AuthenticationController(private val navController: NavController) {

    init{
        getAllUsers()
    }

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> get() = _users

    private suspend fun getUserById(userId: String): User? {
        return withContext(Dispatchers.IO) {
            DatabaseManager.getUserById(userId)
        }
    }

    suspend fun comparePins(userId: String, inputPin: String): Boolean {
        val user = getUserById(userId)
        return user?.pin == inputPin
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


    private fun navigateToCamera(uid: String) {
        navController.navigate("camera/$uid")
    }

    fun navigateToConfirmation(uid: String) {
        navController.navigate("confirmation/$uid")
    }

    fun navigateToListLogin(){
        getAllUsers()
        navController.navigate("list")
    }

    fun navigateToConfirmationLogin(uid: String) {
        navController.navigate("authenticate/$uid")
    }

    fun navigateToCameraLogin(uid: String) {
        navController.navigate("cameralogin/$uid")
    }


    fun navigateToRegister(){
        navController.navigate("register")
    }

    fun navigateToGallery(uid: String){
        navController.navigate("gallery/$uid")
    }



    fun registerUser(
        email: String,
        name: String,
        pin: String,
    ): UUID?
    {
        val uid = UUID.randomUUID()
        if (registrationValidation(email, name, pin)) {
            Log.d("MiTag", "entro en la parte de datos $email,$name,$pin ")
            CoroutineScope(Dispatchers.IO).launch {
                val user = User(
                    uid = uid.toString(),
                    firstName = name,
                    email = email,
                    entryDate = System.currentTimeMillis(),
                    pin = pin
                )
                Log.d("MiTag", "entro en la parte de datos $email,$name,$pin ")
                DatabaseManager.insertUser(user)
            }
            navigateToCamera(uid.toString())
            return uid
        } else {
           // errorMessage.value = getMessageError(email, name, pin).toString()
            return null
        }
    }


    private fun getAllUsers() {
        CoroutineScope(Dispatchers.IO).launch {
            _users.value = DatabaseManager.getAllUsers()
        }
    }
    //-------------------------------------------------------------------------------------------

}
