package com.example.cypher_vault.view.login

import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.cypher_vault.R
import com.example.cypher_vault.controller.authentication.AuthenticationController
import com.example.cypher_vault.view.registration.CameraPreview
import com.example.cypher_vault.view.resources.CustomTitle


val firstColor = Color(0xFF02a6c3)
val secondColor = Color(0xFF01243a)
val thirdColor = Color(0xFF005767)
val fontFamily = FontFamily(
    Font(R.font.consola, FontWeight.Normal)
)

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun LoginText(){
    val textStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 25.sp, color = thirdColor, fontFamily = fontFamily)
    Text(
        "Inicio de sesión",
        style = textStyle,
        modifier = Modifier
                        .padding(top = 70.dp)
                        .offset(x = -(60.dp)),
        )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationLogin(authenticationController: AuthenticationController) {
    val users by authenticationController.users.collectAsState()
    val buttonTextStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp, color = thirdColor, fontFamily = fontFamily)
    var selectedPersona by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    )
    {
        CustomTitle()
        LoginText()

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Buscá tu usuario", style = TextStyle(color = thirdColor)) },
            modifier = Modifier.padding(top = 20.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                cursorColor = secondColor,
                focusedBorderColor = firstColor,
                unfocusedBorderColor = firstColor,
                disabledBorderColor = firstColor,
            ),
            shape = RoundedCornerShape(4.dp),
            trailingIcon = { Icon(Icons.Default.Send, contentDescription = "Icono de envío", tint = thirdColor) },
        )


        LazyColumn(
            modifier = Modifier.padding(top = 20.dp).heightIn(max = 200.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(users.filter { it.firstName?.contains(searchQuery, ignoreCase = true) == true }.take(5)) { user ->
            Button(
                    onClick = {
                        selectedPersona = user.firstName
                    },
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, firstColor),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = firstColor),
                    modifier = Modifier.width(290.dp).padding(top = 15.dp)
                ) {
                    Column (
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ){
                        Text(
                            text = user.firstName?.take(21)?.let { if (it.length <= 21) it else "$it..." } ?: "",
                            fontSize = 20.sp // Tamaño de fuente más grande para el nombre
                        )
                        Text(
                            text = user.email?.take(21)?.let { if (it.length <= 21) "$it..." else it } ?: "",
                            fontSize = 14.sp // Tamaño de fuente más pequeño para el correo electrónico
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            OutlinedButton(
                onClick = { authenticationController.navigateToRegister() },
                border = BorderStroke(2.dp, Color.White),
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Registrarse", style = buttonTextStyle)
            }
        }
    }
    selectedPersona?.let { user ->
        LoginCamera(
            authenticationController = authenticationController,
            user = user
        )
    }
}


@Composable
fun LoginCamera(authenticationController: AuthenticationController, user: String) {
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraProvider = cameraProviderFuture.get()
    val preview = Preview.Builder().build()
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
        .build()

    LaunchedEffect(cameraProviderFuture) {
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        CameraPreview(preview)
        CloseCameraButton(cameraProvider, authenticationController)
    }
}

@Composable
fun CloseCameraButton(cameraProvider: ProcessCameraProvider, authenticationController: AuthenticationController) {
    Button(onClick = {
        // Cierra la cámara
        cameraProvider.unbindAll()
        authenticationController.navigateToListLogin()
    }, modifier = Modifier.padding(bottom = 50.dp)) {
        Text(text = "Cerrar cámara")
    }

}