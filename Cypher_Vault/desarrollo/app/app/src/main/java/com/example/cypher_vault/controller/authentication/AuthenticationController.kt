package com.example.cypher_vault.controller.authentication
import android.graphics.Bitmap
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

    suspend fun comparePins(userId: String, inputPin: Int): Boolean {
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
        showDialog: MutableState<Boolean>,
        errorMessage: MutableState<String>,
        pin: Int
    ): UUID? {
        val uid = UUID.randomUUID()
        if (validateFields(name, email)){
            showDialog.value = true
            errorMessage.value = "Por favor, rellena todos los campos correctamente."
        }

        else if (!validateNameLettersOnly(name)) {
            showDialog.value = true
            errorMessage.value = "El nombre debe contener caracteres alfabéticos únicamente"
        }

        else if (!validateMail(email)) {
            showDialog.value = true
            errorMessage.value = "El email debe ser válido"
        }
        else if (validateNameSpacesAndLineBreaks(name)){
            showDialog.value = true
            errorMessage.value = "El nombre no puede contener espacios en blanco"
        }
        else if (validateNameNumbers(name)){
            showDialog.value = true
            errorMessage.value = "El nombre no puede tener números"
        }
        else if (!validateName(name)){
            showDialog.value = true
            errorMessage.value = "El nombre debe tener más de 3 carácteres y menos de 50"
        }
        else {
            CoroutineScope(Dispatchers.IO).launch {
                val user = User(uid = uid.toString(), firstName = name, email = email, entryDate = System.currentTimeMillis(), pin = pin)
                DatabaseManager.insertUser(user)
            }
            navigateToCamera(uid.toString())
            return uid
        }

        return null
    }

    private fun getAllUsers() {
        CoroutineScope(Dispatchers.IO).launch {
            _users.value = DatabaseManager.getAllUsers()
        }
    }
    private fun validateNameLettersOnly(name: String): Boolean {
        return name.all { it.isLetter() }
    }

    private fun validateMail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validateNameNumbers(name: String): Boolean {
        return name.any { it.isDigit() }
    }

    private fun validateNameSpacesAndLineBreaks(name: String): Boolean {
        return name.contains(" ") || name.contains("\n") || name.contains("\r\n")
    }

    private fun validateName(name: String): Boolean{
        return name.length in 3..50
    }

    private fun validateFields(email: String, name: String): Boolean{
        return name.isEmpty() || email.isEmpty()
    }

}
