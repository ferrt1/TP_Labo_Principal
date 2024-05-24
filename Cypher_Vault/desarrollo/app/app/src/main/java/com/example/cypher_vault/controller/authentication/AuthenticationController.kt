package com.example.cypher_vault.controller.authentication

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.cypher_vault.model.authentication.FaceRecognitionModel
import com.example.cypher_vault.controller.data.DatabaseController
import com.example.cypher_vault.controller.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthenticationController(context: Context) {
    private val databaseController = DatabaseController()
    private val model = FaceRecognitionModel(context.assets, "mobilefacenet.tflite")
    private val threshold = 0.6f

    fun authenticate(userId: String, callback: (Boolean, Bitmap?, Bitmap?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val imageLogin = databaseController.getImageLoginForUser(userId)
            val imageRegister = databaseController.getImageRegistersForUser(userId)

            if (imageLogin.isNullOrEmpty() || imageRegister.isNullOrEmpty()) {
                callback(false, null, null)
                return@launch
            }
            val registerImage = imageRegister[0]
            val loginImage = imageLogin[0]

            val registerBitmap = BitmapFactory.decodeByteArray(registerImage.imageData, 0, registerImage.imageData.size)
            val loginBitmap = BitmapFactory.decodeByteArray(loginImage.imageData, 0, loginImage.imageData.size)

            val registerFeatures = model.extractFeatures(registerBitmap)
            val loginFeatures = model.extractFeatures(loginBitmap)

            val result = model.compareFaceFeatures(registerFeatures, loginFeatures)

            if (result < threshold) {
                databaseController.deleteImageLogin(userId)
                callback(true, registerBitmap, loginBitmap)
            } else {
                databaseController.deleteImageLogin(userId)
                callback(false, registerBitmap, loginBitmap)
            }
        }
    }

}