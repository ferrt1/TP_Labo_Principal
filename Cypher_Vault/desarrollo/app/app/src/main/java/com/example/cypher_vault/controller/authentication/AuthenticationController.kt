package com.example.cypher_vault.controller.authentication

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.cypher_vault.model.authentication.FaceRecognitionModel
import com.example.cypher_vault.controller.data.DatabaseController
import com.example.cypher_vault.controller.navigation.NavController
import com.example.cypher_vault.model.authentication.MobileFaceNet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthenticationController(context: Context) {
    private val databaseController = DatabaseController()
    private val model = FaceRecognitionModel(context.assets, "MobileFaceNetv2.tflite")
    private val assetManager = context.assets // AssetManager
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

            val mobileFaceNet = MobileFaceNet(assetManager)
            val result = mobileFaceNet.compare(registerBitmap, loginBitmap)

            val isAuthenticated = result >= MobileFaceNet.THRESHOLD
            databaseController.deleteImageLogin(userId)
            callback(isAuthenticated, registerBitmap, loginBitmap)
        }
    }


}