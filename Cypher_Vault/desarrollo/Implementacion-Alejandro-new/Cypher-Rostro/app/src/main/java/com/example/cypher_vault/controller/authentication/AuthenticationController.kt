package com.example.cypher_vault.controller.authentication

import androidx.compose.runtime.MutableState
import androidx.navigation.NavController
import com.example.cypher_vault.database.Converters
import com.example.cypher_vault.database.ImagesRegister
import com.example.cypher_vault.database.User
import com.example.cypher_vault.model.dbmanager.DatabaseManager
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceLandmark
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AuthenticationController(private val navController: NavController) {

    init{
        getAllUsers()
    }

    private var _uid : String? = null
    private val _users = MutableStateFlow<List<User>>(emptyList())
    private val _userImagesRegister = MutableStateFlow<List<ImagesRegister>>(emptyList())
    val users: StateFlow<List<User>> get() = _users
    val userImagesRegister: MutableStateFlow<List<ImagesRegister>> get() = _userImagesRegister

    fun navigateToCamera() {
        navController.navigate("camera/$_uid")
    }

    fun navigateToConfirmation() {
        navController.navigate("confirmation/$_uid")
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
        _uid = uid.toString()
        if (!validateFields(email, name) && validateMail(email) && validateName(name)) {

            CoroutineScope(Dispatchers.IO).launch {
                val user = User(uid = uid.toString(), firstName = name, email = email, entryDate = System.currentTimeMillis(), pin = null)
                DatabaseManager.insertUser(user)

            }
            navigateToCamera()
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

    fun saveImage(imageData: ByteArray, userId: String, faceContours: List<FaceContour>,
                  faceLandMarks: List<FaceLandmark>) {
        CoroutineScope(Dispatchers.IO).launch {
            val cambioDeTipo = Converters()
            val faceContoursString = cambioDeTipo.faceContourListToString(faceContours)
            val faceLandMarksString = cambioDeTipo.faceLandmarkListToString(faceLandMarks)
            val imageRegister = ImagesRegister(imageData = imageData, user_id = userId, faceContours =  faceContoursString, faceLandmarks = faceLandMarksString)
            DatabaseManager.insertImageRegister(imageRegister)
        }
    }

    fun getUserImagesRegister(userId: String): MutableStateFlow<List<ImagesRegister>> {
        CoroutineScope(Dispatchers.IO).launch {
            _userImagesRegister.value = DatabaseManager.getImagesForUser(userId)
        }
        return _userImagesRegister
    }

    fun getAllUsers() {
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