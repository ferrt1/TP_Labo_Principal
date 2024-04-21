package com.example.cypher_vault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.lifecycle.ProcessCameraProvider
import com.example.cypher_vault.model.CameraModel
import com.example.cypher_vault.view.LoginView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
            LoginView(cameraProviderFuture, this)
        }
    }
}