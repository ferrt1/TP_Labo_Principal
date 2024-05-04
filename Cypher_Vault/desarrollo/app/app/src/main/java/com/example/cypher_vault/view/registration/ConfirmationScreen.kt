package com.example.cypher_vault.view.registration

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.Image
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.dp
import com.example.cypher_vault.controller.authentication.AuthenticationController
import com.example.cypher_vault.database.ImagesRegister
import com.example.cypher_vault.model.dbmanager.DatabaseManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ConfirmationScreen(authenticationController: AuthenticationController, userId: String) {
    val coroutineScope = rememberCoroutineScope()
    val imageRegisters = remember { mutableStateOf<List<ImagesRegister>?>(null) }
    coroutineScope.launch {
        imageRegisters.value = authenticationController.getImageRegistersForUser(userId)
        Log.d("Imagen", imageRegisters.toString() + "user:" + userId);
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text("¡Ya estás registrado! ")
        imageRegisters.value?.forEach { imageRegister ->
            val bitmap = BitmapFactory.decodeByteArray(
                imageRegister.imageData,
                0,
                imageRegister.imageData.size
            )
            bitmap?.let {
                val rotatedBitmap = Bitmap.createBitmap(
                    it,
                    0,
                    0,
                    it.width,
                    it.height,
                    Matrix().apply { postRotate(270f) },
                    true
                )
                val imageBitmap = rotatedBitmap.asImageBitmap()
                Image(
                    painter = BitmapPainter(imageBitmap),
                    contentDescription = "Imagen guardada",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Button(onClick = {
            authenticationController.navigateToListLogin()
        }) {
            Text("Iniciar sesion")
        }
    }



}