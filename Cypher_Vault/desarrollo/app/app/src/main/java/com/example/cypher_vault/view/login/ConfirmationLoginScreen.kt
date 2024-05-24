package com.example.cypher_vault.view.login

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cypher_vault.controller.authentication.AuthenticationController
import com.example.cypher_vault.controller.navigation.NavController

@Composable
fun ConfirmationLoginScreen(navController: NavController, userId: String) {
    val context = LocalContext.current
    val authenticationController = AuthenticationController(context)

    val imagePrintRegister = remember { mutableStateOf<Bitmap?>(null) }
    val imagePrintLogin = remember { mutableStateOf<Bitmap?>(null) }
    val result = remember { mutableStateOf<Boolean?>(null) }

    // Utiliza LaunchedEffect para asegurar que la autenticación se ejecute una vez
    LaunchedEffect(userId) {
        authenticationController.authenticate(userId) { isSuccess, registerBitmap, loginBitmap ->
            result.value = isSuccess
            imagePrintRegister.value = registerBitmap
            imagePrintLogin.value = loginBitmap
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ImageWithLandmarks(imagePrintRegister)
        ImageWithLandmarks(imagePrintLogin)

        when (result.value) {
            null -> {
                Text(
                    "Esperando...",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            true -> {
                Text(
                    "¡Bienvenido de nuevo!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
                OutlinedButton(
                    onClick = { navController.navigateToGallery(userId) },
                    shape = RoundedCornerShape(15.dp),
                    border = BorderStroke(3.dp, Color.Gray),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Gray
                    ),
                    modifier = Modifier
                        .width(200.dp)
                        .padding(top = 30.dp)
                ) {
                    Text(
                        "Ir a la galería",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            false -> {
                Text(
                    "Error en el reconocimiento...",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        OutlinedButton(
            onClick = { navController.navigateToListLogin() },
            shape = RoundedCornerShape(15.dp),
            border = BorderStroke(3.dp, Color.Gray),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.Gray
            ),
            modifier = Modifier
                .width(200.dp)
                .padding(top = 30.dp)
        ) {
            Text(
                "Iniciar sesión",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ImageWithLandmarks(bitmapState: MutableState<Bitmap?>) {
    bitmapState.value?.let { bitmap ->
        val imageBitmap = bitmap.asImageBitmap()
        Image(
            bitmap = imageBitmap,
            contentDescription = "Imagen",
            modifier = Modifier.fillMaxWidth()
        )
    }
}
