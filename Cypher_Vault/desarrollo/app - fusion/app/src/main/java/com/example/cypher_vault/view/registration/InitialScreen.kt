package com.example.cypher_vault.view.registration

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.cypher_vault.R
import com.example.cypher_vault.controller.authentication.AuthenticationController
import com.example.cypher_vault.view.resources.CustomTitle

import com.example.cypher_vault.controller.messages.*
import com.example.cypher_vault.view.resources.*






var message=""


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
    val passwordState = remember { mutableStateOf(TextFieldValue()) }
    val passwordVisible = remember { mutableStateOf(false) }
    var isContentVisiblename by remember { mutableStateOf(false) }
    var isContentVisiblemail by remember { mutableStateOf(false) }
    var isContentVisiblpasswordState by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {


        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            CustomTitle()
            RegisterText()


            TextField(
                value = emailState.value,
                onValueChange = {
                    emailState.value = it
                },

                textStyle = TextStyle(
                    color = if (getvalidateNameSpacesAndLineBreaks(emailState.value.text)) redColor else firstColor,
                    fontSize = 16.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold
                ),
                placeholder = {
                    Text(
                        "ingrese un correo valido",
                        style = TextStyle(
                            color = Color.Gray,
                            fontSize = 16.sp,
                            fontFamily = fontFamily
                        )
                    )
                },

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
                    focusedIndicatorColor = firstColor,
                    unfocusedIndicatorColor = firstColor,
                ),
                modifier = Modifier
                    .width(290.dp) // Establece un ancho fijo para el TextField
                    .padding(top = 15.dp)
                    .border(
                        BorderStroke(3.dp, firstColor),
                        shape = RoundedCornerShape(4.dp),
                    )
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            // El campo de entrada de texto tiene el foco
                            isContentVisiblemail = true
                        } else {
                            // El campo de entrada de texto perdió el foco
                            isContentVisiblemail = false
                        }
                    }

            )

            if (isContentVisiblemail) {
                if (getfullemailfield(emailState.value.text) != "") {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            (if (getvalidateNameSpacesAndLineBreaks(emailState.value.text)) {
                                R.drawable.icoerror
                            } else if (!getvalidateMail(emailState.value.text)) {

                                R.drawable.iconwarning
                            } else {
                                null
                            })?.let {
                                painterResource(
                                    id = it
                                )
                            }?.let {
                                Image(
                                    painter = it,
                                    contentDescription = "",
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp)) // Espacio entre la imagen y el texto
                            if (getfullemailfield(emailState.value.text) != "null") {
                                Text(
                                    getfullemailfield(emailState.value.text),
                                    fontSize = 13.sp,
                                    fontFamily = fontFamily,
                                    color = thirdColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            TextField(
                value = nameState.value,
                onValueChange = {
                    nameState.value = it
                },
                textStyle = TextStyle(
                    color = if (getvalidateNameNumbers(nameState.value.text) || getvalidateNameSpacesAndLineBreaks(nameState.value.text)) redColor else firstColor,
                    fontSize = 16.sp,
                    fontFamily = fontFamily,
                    fontWeight = FontWeight.Bold
                ),
                placeholder = {
                    Text(
                        "Minimo 3 caracteres",
                        style = TextStyle(
                            color = Color.Gray,
                            fontSize = 16.sp,
                            fontFamily = fontFamily
                        )
                    )
                },
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
                    focusedIndicatorColor = firstColor,
                    unfocusedIndicatorColor = firstColor,
                ),
                modifier = Modifier
                    .width(290.dp) // Establece un ancho fijo para el TextField
                    .padding(top = 15.dp)
                    .border(BorderStroke(3.dp, firstColor), shape = RoundedCornerShape(4.dp),)
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            // El campo de entrada de texto tiene el foco
                            isContentVisiblename = true
                        } else {
                            // El campo de entrada de texto perdió el foco
                            isContentVisiblename = false
                        }
                    }
            )
            Log.d("MiTag", "el contenido es: $isContentVisiblename")
            // Esto se va a poner feo
            if (isContentVisiblename) {
                if (getfullnamefield(nameState.value.text) != "") {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            (if (getvalidateNameNumbers(nameState.value.text) || getvalidateNameSpacesAndLineBreaks(nameState.value.text)) {
                                R.drawable.icoerror
                            } else if (!getvalidateName(nameState.value.text)) {

                                R.drawable.iconwarning
                            } else {
                                null
                            })?.let {
                                painterResource(
                                    id = it
                                )
                            }?.let {
                                Image(
                                    painter = it,
                                    contentDescription = "",
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp)) // Espacio entre la imagen y el texto
                            if (getfullnamefield(nameState.value.text) != "null") {
                                Text(
                                    getfullnamefield(nameState.value.text),
                                    fontSize = 13.sp,
                                    fontFamily = fontFamily,
                                    color = thirdColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .width(290.dp) // Establece un ancho fijo para el TextField
                    .padding(top = 15.dp)
                    .border(
                        BorderStroke(3.dp, firstColor),
                        shape = RoundedCornerShape(4.dp)
                    )
            ) {
                TextField(
                    value = passwordState.value,
                    onValueChange = {

                        passwordState.value = it

                    },
                    textStyle = TextStyle(
                        color = firstColor,
                        fontSize = 16.sp,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold
                    ),
                    label = {
                        Text(
                            "Contraseña",
                            fontSize = 20.sp,
                            fontFamily = fontFamily,
                            color = thirdColor,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    placeholder = {
                        Text(
                            "16 caracteres y 1 espacial",
                            style = TextStyle(
                                color = Color.Gray,
                                fontSize = 16.sp,
                                fontFamily = fontFamily
                            )
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
                    visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(
                            onClick = { passwordVisible.value = !passwordVisible.value },
                            modifier = Modifier.offset(y = 10.dp)
                        ) {
                            Icon(
                                imageVector = if (passwordVisible.value) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (passwordVisible.value) "Ocultar contraseña" else "Mostrar contraseña"
                            )
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        cursorColor = thirdColor,
                        focusedIndicatorColor = firstColor,
                        unfocusedIndicatorColor = firstColor,
                    ),
                    modifier = Modifier.weight(1f)
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                // El campo de entrada de texto tiene el foco
                                isContentVisiblpasswordState = true
                            } else {
                                // El campo de entrada de texto perdió el foco
                                isContentVisiblpasswordState = false
                            }
                        }
                )

            }

            if (isContentVisiblpasswordState) {
                if (getfullnamefield(nameState.value.text) != "") {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            (if (getvalidateNameNumbers(nameState.value.text) || getvalidateNameSpacesAndLineBreaks(nameState.value.text)) {
                                R.drawable.icoerror
                            } else if (!getvalidateName(nameState.value.text)) {

                                R.drawable.iconwarning
                            } else {
                                null
                            })?.let {
                                painterResource(
                                    id = it
                                )
                            }?.let {
                                Image(
                                    painter = it,
                                    contentDescription = "",
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp)) // Espacio entre la imagen y el texto
                            if (getfullnamefield(nameState.value.text) != "null") {
                                Text(
                                    getfullnamefield(nameState.value.text),
                                    fontSize = 13.sp,
                                    fontFamily = fontFamily,
                                    color = thirdColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }


            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    authenticationController.registerUser(emailState.value.text, nameState.value.text, passwordState.value.text)
                },
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(3.dp, firstColor),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = firstColor
                ),
                modifier = Modifier
                    .width(250.dp)
                    .padding(top = 5.dp, bottom = 10.dp)
            ) {
                Text("Registrarse",
                    fontFamily = fontFamily,
                    color = thirdColor,
                    fontWeight = FontWeight.Bold
                )
            }
            OutlinedButton(
                onClick = { authenticationController.navigateToListLogin() },
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
                    fontFamily = fontFamily,
                    color = Color.Gray, // Establece el color del texto a gris
                    fontWeight = FontWeight.Bold
                )
            }

            //------------------------- espacio para el socalo de mensaje-----------------------//

            if(!passwordState.value.text.isEmpty()) {
                Row(
                    modifier = Modifier
                        .width(290.dp)
                        .padding(top = 5.dp)
                ) {
                    Text(
                        text = "16 Como minimo caracteres alfanuméricos",
                        fontFamily = fontFamily,
                        fontSize = 14.sp,
                        color = if (getvalidatePasswordLength(passwordState.value.text)) greenColor else Color.Gray,
                        textAlign = TextAlign.Left
                    )
                }

                Row(
                    modifier = Modifier
                        .width(290.dp) // Establece un ancho fijo para el TextField
                        .padding(top = 5.dp)
                ) {
                    Text(
                        text = "1 carácter especial",
                        fontFamily = fontFamily,
                        fontSize = 14.sp,
                        color = if (getvalidatePasswordCharacters(passwordState.value.text)) greenColor else Color.Gray,
                        textAlign = TextAlign.Left
                    )
                }
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