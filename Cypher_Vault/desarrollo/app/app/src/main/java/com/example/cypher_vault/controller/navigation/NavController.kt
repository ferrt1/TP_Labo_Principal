package com.example.cypher_vault.controller.navigation
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.NavController
import com.example.cypher_vault.R
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
import java.io.ByteArrayOutputStream
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

    fun navigateToConfirmation(uid: String, registerSuccessful: Boolean, messageError: String) {
        navController.navigate("confirmation/$uid/$registerSuccessful/$messageError")
    }

    fun navigateToListLogin(){
        getAllUsers()
        navController.navigate("list")
    }

    fun navigateToConfirmationLogin(uid: String, fromCamera : Boolean) {
        navController.navigate("authenticate/$uid/$fromCamera")
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


    fun navigateToLockScreen(blockedUser: String) {
        navController.navigate("lockscreen/$blockedUser")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun registerUser(
        email: String,
        name: String,
        password: String,
        errorMessage: MutableState<String>,
        context: Context
    ): UUID? {
        val uid = UUID.randomUUID()
        if (registrationValidation(email, name, password)) {

            CoroutineScope(Dispatchers.IO).launch {
                val encryptionService = EncryptionService()
                val salt = encryptionService.generateSalt()

                // Convertir la imagen predeterminada a un ByteArray
                val defaultImageBitmap = getBitmapFromDrawable(context, R.drawable.logo)
                val profileImageByteArray = convertBitmapToByteArray(defaultImageBitmap)

                val user = User(
                    uid = uid.toString(),
                    firstName = name,
                    email = email,
                    entryDate = System.currentTimeMillis(),
                    password = password,
                    authentication = false,
                    encryptionSalt = Base64.getEncoder().encodeToString(salt),
                    profile_picture = profileImageByteArray
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

    private fun getBitmapFromDrawable(context: Context, drawableId: Int): Bitmap {
        val drawable = ResourcesCompat.getDrawable(context.resources, drawableId, null)!!
        return (drawable as BitmapDrawable).bitmap
    }

    private fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

}
