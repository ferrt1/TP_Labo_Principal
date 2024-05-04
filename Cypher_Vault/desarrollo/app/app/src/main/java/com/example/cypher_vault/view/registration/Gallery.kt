package com.example.cypher_vault.view.registration

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import com.example.cypher_vault.controller.authentication.AuthenticationController
import com.example.cypher_vault.database.ImagesRegister

@Composable
fun Gallery(authenticationController: AuthenticationController, userId: String) {

    val imageRegisters = remember { mutableStateOf<List<ImagesRegister>?>(null) }
    LaunchedEffect(key1 = userId) {
        imageRegisters.value = authenticationController.getImageRegistersForUser(userId)
        Log.d("Imagen", imageRegisters.toString() + "user:" + userId);
    }

    Box(modifier = Modifier.fillMaxSize()) {
        imageRegisters.value?.forEach { imageRegister ->
            val bitmap = BitmapFactory.decodeByteArray(
                imageRegister.imageData,
                0,
                imageRegister.imageData.size
            )
            val imageBitmap = bitmap.asImageBitmap()
            Image(
                painter = BitmapPainter(imageBitmap),
                contentDescription = "a",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}