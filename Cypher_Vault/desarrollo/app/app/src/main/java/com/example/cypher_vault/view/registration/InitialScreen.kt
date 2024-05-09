package com.example.cypher_vault.view.registration

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.cypher_vault.R
import com.example.cypher_vault.controller.authentication.AuthenticationController
import com.example.cypher_vault.view.resources.CustomTitle


val firstColor = Color(0xFF02a6c3)
val secondColor = Color(0xFF01243a)
val thirdColor = Color(0xFF005767)
val fontFamily = FontFamily(
    Font(R.font.expandedconsolabold, FontWeight.Normal)
)

@Composable
fun RegisterText(){
    val textStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 25.sp, color = thirdColor, fontFamily = fontFamily)
    Text(
        "Registro",
        style = textStyle,
        modifier = Modifier
            .padding(top = 70.dp, bottom = 10.dp)
            .offset(x = -(80.dp)),
    )

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InitialScreen(authenticationController: AuthenticationController) {
    val context = LocalContext.current
    val activity = context.findAncestorActivity()

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.CAMERA), 200)
    }

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 200)
    }


    val emailState = remember { mutableStateOf(TextFieldValue()) }
    val nameState = remember { mutableStateOf(TextFieldValue()) }
    val showDialog = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf("") }



    Box(
        modifier = Modifier.fillMaxSize(),
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            CustomTitle()
            RegisterText()


            TextField(
                value = emailState.value,
                onValueChange = { emailState.value = it },
                textStyle = TextStyle(
                    color = firstColor,
                    fontSize = 16.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold
                ),
                label = {
                    Text(
                        "Correo electrónico",
                        fontSize = 20.sp,
                        fontFamily = fontFamily,
                        color = thirdColor,
                        fontWeight = FontWeight.Bold,
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(4.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    cursorColor = thirdColor,
                    focusedIndicatorColor = com.example.cypher_vault.view.login.firstColor,
                    unfocusedIndicatorColor = com.example.cypher_vault.view.login.firstColor,
                ),
                modifier = Modifier
                    .width(290.dp) // Establece un ancho fijo para el TextField
                    .padding(top = 15.dp)
                    .border(BorderStroke(3.dp, com.example.cypher_vault.view.login.firstColor), shape =  RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.height(10.dp))

            TextField(
                value = nameState.value,
                onValueChange = { nameState.value = it },
                textStyle = TextStyle(
                    color = firstColor,
                    fontSize = 16.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold
                ),
                label = {
                    Text(
                        "Nombre",
                        fontSize = 20.sp,
                        fontFamily = fontFamily,
                        color = thirdColor,
                        fontWeight = FontWeight.Bold
                    )
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    cursorColor = thirdColor,
                    focusedIndicatorColor = com.example.cypher_vault.view.login.firstColor,
                    unfocusedIndicatorColor = com.example.cypher_vault.view.login.firstColor,
                ),
                modifier = Modifier
                    .width(290.dp) // Establece un ancho fijo para el TextField
                    .padding(top = 15.dp)
                    .border(BorderStroke(3.dp, com.example.cypher_vault.view.login.firstColor), shape =  RoundedCornerShape(4.dp),)
            )


            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    authenticationController.registerUser(emailState.value.text, nameState.value.text, showDialog, errorMessage)
                },
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(3.dp, com.example.cypher_vault.view.login.firstColor),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = com.example.cypher_vault.view.login.firstColor
                ),
                modifier = Modifier.width(250.dp).padding(top = 5.dp ,bottom = 10.dp)
            ) {
                Text("Registrarse",
                    fontFamily = com.example.cypher_vault.view.login.fontFamily,
                    color = com.example.cypher_vault.view.login.thirdColor,
                    fontWeight = FontWeight.Bold
                )
            }


            OutlinedButton(
                onClick = { authenticationController.navigateToListLogin() },
                shape = RoundedCornerShape(15.dp), // Esto hará que los bordes sean completamente redondos
                border = BorderStroke(3.dp, Color.Gray), // Establece el color del borde a gris
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Gray // Establece el color del contenido (texto) a gris
                ),
                modifier = Modifier
                    .width(200.dp)
                    .padding(top = 30.dp)
            ) {
                Text(
                    "Iniciar sesión",
                    fontFamily = com.example.cypher_vault.view.login.fontFamily,
                    color = Color.Gray, // Establece el color del texto a gris
                    fontWeight = FontWeight.Bold
                )
            }

        }
    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Error") },
            text = { Text(errorMessage.value) },
            confirmButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text("Aceptar")
                }
            }
        )
    }
}

fun Context.findAncestorActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }
        context = context.baseContext
    }
    return null
}