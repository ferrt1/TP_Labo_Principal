package com.example.cypher_vault.controller.authentication

import androidx.compose.runtime.MutableState
import androidx.navigation.NavController
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

class AuthenticationController(private val navController: NavController) {

    init{
        getAllUsers()
    }
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> get() = _users

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

    fun navigateToRegister(){
        navController.navigate("register")
    }

    fun registerUser(
        email: String,
        name: String,
        showDialog: MutableState<Boolean>,
        errorMessage: MutableState<String>
    ): UUID? {
        val uid = UUID.randomUUID()
        if (!validateFields(email, name) && validateMail(email) && validateName(name)) {

            CoroutineScope(Dispatchers.IO).launch {
                val user = User(uid = uid.toString(), firstName = name, email = email, entryDate = System.currentTimeMillis(), pin = null)
                DatabaseManager.insertUser(user)
            }
            navigateToCamera(uid.toString())
            return uid
        }
        else if(validateFields(name, email)){
            showDialog.value = true
            errorMessage.value = "Por favor, rellena todos los campos correctamente."
        }
        else if (!validateMail(email)) {
            showDialog.value = true
            errorMessage.value = "El email debe ser válido"
        }
        else if (!validateName(name)){
            showDialog.value = true
            errorMessage.value = "El nombre debe tener más de 3 carácteres y menos de 50"
        }
        return null
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

    fun getLastImageRegister(userId: String): ImagesRegister? {
        var imageRegister: ImagesRegister? = null
        CoroutineScope(Dispatchers.IO).launch {
            imageRegister = withContext(Dispatchers.IO) {
                DatabaseManager.getLastImageRegisterForUser(userId)
            }
        }
        return imageRegister
    }

    private fun getAllUsers() {
        CoroutineScope(Dispatchers.IO).launch {
            _users.value = DatabaseManager.getAllUsers()
        }
    }

    private fun validateMail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validateName(name: String): Boolean{
        return name.length in 3..50;
    }

    private fun validateFields(email: String, name: String): Boolean{
        return name.isEmpty() || email.isEmpty()
    }

}
