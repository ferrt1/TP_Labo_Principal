package com.example.cypher_vault.controller.authentication

import android.util.Log
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
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AuthenticationController(private val navController: NavController) {

    init{
        getAllUsers()
    }

    private var _uid : String? = null
    private val _users = MutableStateFlow<List<User>>(emptyList())
    private var _userImagesRegister : List<ImagesRegister?>? = null
    val users: StateFlow<List<User>> get() = _users

    fun navigateToRegisterCamera() {
        navController.navigate("cameraR/$_uid")
    }

    fun navigateToConfirmation() {
        navController.navigate("confirmation/$_uid")
    }

    fun navigateToLoginCamera(uidParam : String) {
        navController.navigate("cameraL/$uidParam")
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
            navigateToRegisterCamera()
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
                  faceLandMarks: List<FaceLandmark>, onComplete: (Boolean) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val cambioDeTipo = Converters()
            val faceContoursString = cambioDeTipo.faceContourListToString(faceContours)
            val faceLandMarksString = cambioDeTipo.faceLandmarkListToString(faceLandMarks)
            val imageRegister = ImagesRegister(imageData = imageData, user_id = userId, faceContours =  faceContoursString, faceLandmarks = faceLandMarksString)
            val success = DatabaseManager.insertImageRegister(imageRegister)
            onComplete(success)
        }
    }

    fun getUserImagesRegister(userId: String, onComplete: (List<ImagesRegister?>?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val images = DatabaseManager.getImagesForUser(userId)
            withContext(Dispatchers.Main) {
                onComplete(images)
            }
        }
    }

    suspend fun obtenerContour(userId: String): List<FaceContour> {
        return suspendCancellableCoroutine { continuation ->
            CoroutineScope(Dispatchers.IO).launch {
                Log.d("faceDetection", "obtenerContour antes de getFaceContoursForUser")
                val images = DatabaseManager.getFaceContoursForUser(userId)
                Log.d("faceDetection", "obtenerContour despues de getFaceContoursForUser")
                continuation.resume(images) // Reanuda la ejecución con los resultados obtenidos
            }
        }
    }

    suspend fun obtenerLandMark(userId: String): List<FaceLandmark> {
        return suspendCancellableCoroutine { continuation ->
            CoroutineScope(Dispatchers.IO).launch {
                Log.d("faceDetection", "obtenerLandMark antes de getFaceLandmarksForUser")
                val images = DatabaseManager.getFaceLandmarksForUser(userId)
                Log.d("faceDetection", "obtenerLandMark despues de getFaceLandmarksForUser")
                continuation.resume(images) // Reanuda la ejecución con los resultados obtenidos
            }
        }
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