package com.example.cypher_vault.model

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf

class CameraModel {
    // Crea un estado mutable para almacenar el bitmap
    val imageBitmap = mutableStateOf<Bitmap?>(null)
}