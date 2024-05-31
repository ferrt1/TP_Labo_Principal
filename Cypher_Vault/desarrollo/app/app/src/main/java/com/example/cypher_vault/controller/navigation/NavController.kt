package com.example.cypher_vault.controller.navigation
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.navigation.NavController
import com.example.cypher_vault.controller.messages.getMessageError
import com.example.cypher_vault.controller.messages.registrationValidation
import com.example.cypher_vault.database.User
import com.example.cypher_vault.model.dbmanager.DatabaseManager
import com.example.cypher_vault.model.encrypt.EncryptionService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Base64
import java.util.UUID

class NavController(private val navController: NavController) {

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

    fun navigateToProfile(uid: String){
        navController.navigate("profile/$uid")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun registerUser(
        email: String,
        name: String,
        password: String,
        errorMessage: MutableState<String>,
    ): UUID? {
        val uid = UUID.randomUUID()
        if (registrationValidation(email, name, password)) {

            CoroutineScope(Dispatchers.IO).launch {
                val encryptionService = EncryptionService()
                val salt = encryptionService.generateSalt()
                val user = User(
                    uid = uid.toString(),
                    firstName = name,
                    email = email,
                    entryDate = System.currentTimeMillis(),
                    password = password,
                    authentication = false,
                    encryptionSalt = Base64.getEncoder().encodeToString(salt)
                )
                DatabaseManager.insertUser(user)
            }
            navigateToCamera(uid.toString())
            return uid
        } else {
            errorMessage.value = getMessageError(email, name, password)
            return null
        }
    }
}
