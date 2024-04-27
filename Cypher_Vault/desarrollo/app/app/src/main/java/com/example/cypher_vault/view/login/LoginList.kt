package com.example.cypher_vault.view.login

import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import com.example.cypher_vault.R
import com.example.cypher_vault.controller.authentication.AuthenticationController
import com.example.cypher_vault.view.registration.CameraPreview


val firstColor = Color(0xFF02a6c3)
val secondColor = Color(0xFF01243a)
val thirdColor = Color(0xFF005767)

@Composable
fun CustomTitle() {
    val textStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 54.sp)

    Row(modifier = Modifier.wrapContentSize().offset(16.dp).padding( top = 40.dp), )
        {
        Column() {
            Row (
                horizontalArrangement = Arrangement.Absolute.Left
            ){
                Text(
                    text = "C",
                    color = firstColor,
                    style = textStyle,
                )
                Text(
                    text = "ypher",
                    color = secondColor,
                    style = textStyle,
                )
            }
            Row {
                Text(
                    text = "V",
                    color = firstColor,
                    style = textStyle,
                )
                Text(
                    text = "ault",
                    color = secondColor,
                    style = textStyle,
                )
            }
        }
        Box(
            modifier = Modifier
                .offset { IntOffset(-(32).dp.roundToPx(), 14.dp.roundToPx()) }
                .size(94.dp)
                .align(Alignment.Bottom)
                .border(2.dp, firstColor, RoundedCornerShape(50))
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(82.dp)
                    .align(Alignment.Center)
            )
        }
    }

}


@Composable
fun NavigationLogin(authenticationController: AuthenticationController) {
    val users by authenticationController.users.collectAsState()

    var selectedPersona by remember { mutableStateOf<String?>(null) }

    val textStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 25.sp, color = thirdColor)

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    )
    {
        CustomTitle()
        Text(
            "Inicio de sesión",
            style = textStyle,
            modifier = Modifier.padding( start = 65.dp, top = 70.dp ).align(Alignment.Start),
        )
    }


    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        items(users) { user ->
            Button(
                onClick = {
                    selectedPersona = user.firstName
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = user.firstName ?: "", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        OutlinedButton(
            onClick = { authenticationController.navigateToRegister() },
            border = BorderStroke(0.dp, Color.Transparent),
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Registrarse", style = MaterialTheme.typography.bodySmall)
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