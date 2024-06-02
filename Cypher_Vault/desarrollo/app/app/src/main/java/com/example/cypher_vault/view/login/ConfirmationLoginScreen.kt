package com.example.cypher_vault.view.login

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Nightlight
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material.icons.outlined.WbTwilight
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cypher_vault.R
import com.example.cypher_vault.controller.authentication.AuthenticationController
import com.example.cypher_vault.controller.authentication.SecondAuthController
import com.example.cypher_vault.controller.data.DatabaseController
import com.example.cypher_vault.controller.income.UserAccessController
import com.example.cypher_vault.controller.navigation.NavController
import com.example.cypher_vault.controller.service.ServiceController
import com.example.cypher_vault.model.authentication.SecondAuthManager
import com.example.cypher_vault.model.income.UserAccessManager
import com.example.cypher_vault.model.service.ServiceManager
import com.example.cypher_vault.view.gallery.firstColor
import com.example.cypher_vault.view.gallery.textStyleTittle2
import com.example.cypher_vault.view.gallery.thirdColor

//Colores de la ui, tipo de letra, etc.///////////////////////
val firstColor = Color(0xFF02a6c3)
val secondColor = Color(0xFF01243a)
val thirdColor = Color(0xFF005767)
val premiumBackgroundColor = Color(0xFF131313)
val premiumButtonColor = Color(0xFF64C4F1)
val premiumButtonTextColor = Color(0xFF000000)
val mainBackgroundColor = Color(0xFFdcdcdc)
val wingWhite = Color(0xFFdcdcdc)
val fontFamily = FontFamily(
    Font(R.font.expandedconsolabold, FontWeight.Normal)
)
val textStyle = TextStyle(fontSize = 25.sp, color = thirdColor, fontFamily = fontFamily)
val textStyleTittle = TextStyle(
    fontWeight = FontWeight.ExtraBold,
    fontSize = 25.sp,
    fontFamily = com.example.cypher_vault.view.resources.fontFamily,
    letterSpacing = 2.sp
)
val textStyleTittle2 = TextStyle(
    fontWeight = FontWeight.ExtraBold,
    fontSize = 15.sp,
    fontFamily = com.example.cypher_vault.view.resources.fontFamily,
    letterSpacing = 1.sp
)

@Composable
fun ConfirmationLoginScreen(navController: NavController, userId: String) {
    val context = LocalContext.current
    val authenticationController = AuthenticationController(context)
    var dbc = DatabaseController()
    var secondAuthManager = SecondAuthManager()
    var secondAuthController = SecondAuthController(secondAuthManager)
    var showImage by remember { mutableStateOf(false) }

    //Prototipado de la pantalla para logueo con camara
    val imagePrintRegister = remember { mutableStateOf<Bitmap?>(null) }
    val imagePrintLogin = remember { mutableStateOf<Bitmap?>(null) }
    val result = remember { mutableStateOf<Boolean?>(null) }

    //Se agrega ingreso de usuario
    val userAccessManager = UserAccessManager()
    val userAccessController = UserAccessController(userAccessManager)

    //Variables de la 2da Authentificacion
    var isSecondAuth: Boolean? by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { // Key can be anything to trigger on recomposition
        val usuarioTemp = dbc.getUserById(userId)
        if (usuarioTemp != null) {
            isSecondAuth = usuarioTemp.authentication
        }
    }

    //Variables de conexion a la red de internet
    val serviceManager = remember { ServiceManager(context) }
    val serviceController = remember { ServiceController(serviceManager) }
    var isInternetAvailable by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isInternetAvailable = serviceController.isInternetAvailable()
    }

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
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //Elementos de la 2da Authentificacion//////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////
        if (imagePrintRegister.value == null && imagePrintLogin.value == null) {
            if (isSecondAuth == true) {
                /// SIN IMAGENES Y CON SEGUNDA ACTIVADA ////////////////////////
                if (isInternetAvailable) {
                    ElevatedCardMailConfirmation(
                        context,
                        navController,
                        secondAuthController,
                        userAccessController,
                        userId,
                        isInternetAvailable
                    )
                } else {
                    ElevatedCardDayPart(
                        context,
                        navController,
                        secondAuthController,
                        userAccessController,
                        userId,
                        isInternetAvailable
                    )
                }
            } else {
                /// SIN IMAGENES Y SIN SEGUNDA ACTIVADA ////////////////////////
                // Ingreso de usuario
                LaunchedEffect(userId) {
                    userAccessController.insertUserIncome(userId)
                }
                Image(
                    painter = painterResource(id = R.drawable.successful),
                    contentDescription = "Bienvenido!",
                    modifier = Modifier.size(128.dp)
                )
                Text(
                    "¡Bienvenido de nuevo!",
                    fontSize = 20.sp,
                    fontFamily = fontFamily,
                    color = thirdColor,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(top = 16.dp)
                )
                OutlinedButton(
                    onClick = { navController.navigateToGallery(userId) },
                    shape = RoundedCornerShape(15.dp),
                    border = BorderStroke(3.dp, firstColor),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = thirdColor
                    ),
                    modifier = Modifier
                        .width(200.dp)
                        .padding(top = 30.dp)
                ) {
                    Text(
                        "Ir a la galería",
                        fontFamily = fontFamily,
                        color = thirdColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            /// CON IMAGENES Y CON SEGUNDA ACTIVADA ////////////////////////
            if (isSecondAuth == true) {
                if (isInternetAvailable) {
                    ElevatedCardMailConfirmation(
                        context,
                        navController,
                        secondAuthController,
                        userAccessController,
                        userId,
                        isInternetAvailable
                    )

                } else {
                    ElevatedCardDayPart(
                        context,
                        navController,
                        secondAuthController,
                        userAccessController,
                        userId,
                        isInternetAvailable
                    )
                }
            } else {
                /// CON IMAGENES Y CON SEGUNDA DESACTIVADA ////////////////////////

                when (result.value) {
                    null -> {
                        Text(
                            "Esperando...",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    true -> {
                        // Ingreso de usuario
                        LaunchedEffect(userId) {
                            userAccessController.insertUserIncome(userId)
                        }
                        Image(
                            painter = painterResource(id = R.drawable.successful),
                            contentDescription = "Bienvenido!",
                            modifier = Modifier.size(128.dp)
                        )
                        Text(
                            "¡Bienvenido de nuevo!",
                            fontSize = 20.sp,
                            fontFamily = fontFamily,
                            color = thirdColor,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        OutlinedButton(
                            onClick = { navController.navigateToGallery(userId) },
                            shape = RoundedCornerShape(15.dp),
                            border = BorderStroke(3.dp, firstColor),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = thirdColor
                            ),
                            modifier = Modifier
                                .width(200.dp)
                                .padding(top = 30.dp)
                        ) {
                            Text(
                                "Ir a la galería",
                                fontFamily = fontFamily,
                                color = thirdColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    false -> {
                        Image(
                            painter = painterResource(id = R.drawable.failure),
                            contentDescription = "No son la misma persona",
                            modifier = Modifier.size(128.dp)
                        )
                        Text(
                            "Acceso denegado.",
                            fontSize = 20.sp,
                            fontFamily = fontFamily,
                            color = Color.Red,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        OutlinedButton(
                            onClick = { navController.navigateToListLogin() },
                            shape = RoundedCornerShape(15.dp),
                            border = BorderStroke(3.dp, firstColor),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = thirdColor
                            ),
                            modifier = Modifier
                                .width(200.dp)
                                .padding(top = 30.dp)
                        ) {
                            Text(
                                "Iniciar sesión",
                                fontFamily = fontFamily,
                                color = thirdColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                if (result.value == true) {
                    OutlinedButton(
                        onClick = { showImage = !showImage },
                        shape = RoundedCornerShape(15.dp),
                        border = BorderStroke(3.dp, firstColor),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = thirdColor
                        ),
                        modifier = Modifier
                            .width(200.dp)
                            .padding(top = 16.dp)
                    ) {
                        Text(
                            if (showImage) "Ocultar imágenes" else "Ver imágenes",
                            fontFamily = fontFamily,
                            color = thirdColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (showImage) {
                        ImageWithLandmarks(imagePrintRegister)
                        ImageWithLandmarks(imagePrintLogin)
                    }
                }
            }

        }
        //Boton vuelta al logueo
        Spacer(modifier = Modifier.weight(1f)) // Push the button to the bottom
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
                .padding(vertical = 50.dp)
        ) {
            Text(
                "Volver",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/// TARJETA DE LA 2DA AUTHENTICACION PARTE DEL MAIL ////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
@Composable
fun ElevatedCardMailConfirmation(
    context: Context,
    navController: NavController,
    secondAuthController: SecondAuthController,
    userAccessController: UserAccessController,
    userId: String,
    internetAvailable: Boolean
) {
    var primerValorCodigo = remember { mutableStateOf(TextFieldValue()) }
    var segundoValorCodigo = remember { mutableStateOf(TextFieldValue()) }
    var tercerValorCodigo = remember { mutableStateOf(TextFieldValue()) }
    var cuartoValorCodigo = remember { mutableStateOf(TextFieldValue()) }
    var quintoValorCodigo = remember { mutableStateOf(TextFieldValue()) }

    val textFieldValues = listOf(primerValorCodigo, segundoValorCodigo, tercerValorCodigo, cuartoValorCodigo, quintoValorCodigo)

    val focusManager = LocalFocusManager.current
    val focusRequesters = List(5) { remember { FocusRequester() } }

    fun handleTextChange(index: Int, value: String) {
        if (value.length > 1) {
            textFieldValues[index].value = TextFieldValue(value[0].toString())
            if (index < textFieldValues.size - 1) {
                handleTextChange(index + 1, value.substring(1))
                focusRequesters[index + 1].requestFocus()
            }
        } else {
            textFieldValues[index].value = TextFieldValue(value)
            if (value.isNotEmpty() && index < textFieldValues.size - 1) {
                focusRequesters[index + 1].requestFocus()
            }
        }
    }

    val emailManager = remember { ServiceManager(context) }
    var mailCode by remember { mutableStateOf("") }
    var sendingMail by remember { mutableStateOf(false) }

    if (mailCode.isEmpty() && !sendingMail) {
        sendingMail = true
        LaunchedEffect(Unit) {
            if (sendingMail) {
                //emailManager.generateAndSendCode(context, userId)
                mailCode = emailManager.getMailCode()
            }
        }
    }
    Log.d("MailConfirmation", "salida: $mailCode")

    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .padding(16.dp)
            .background(mainBackgroundColor)
            .fillMaxHeight(),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .background(wingWhite),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            /// TITULO //////////////////////////////////////////////////////////////////
            Spacer(modifier = Modifier.height(15.dp))
            Row(
                horizontalArrangement = Arrangement.Absolute.Center
            ) {
                Icon(
                    Icons.Filled.Lock,
                    contentDescription = "Localized description",
                    Modifier.size(150.dp),
                    tint = secondColor
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
            Row(
                horizontalArrangement = Arrangement.Absolute.Center
            ) {
                Text(
                    text = "Segunda",
                    fontSize = 20.sp,
                    color = secondColor,
                    style = textStyleTittle2,
                    onTextLayout = { /* No se necesita hacer nada aquí */ }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.Absolute.Center
            ) {
                Text(
                    text = "Autentificacion",
                    fontSize = 20.sp,
                    color = secondColor,
                    style = textStyleTittle2,
                    onTextLayout = { /* No se necesita hacer nada aquí */ }
                )
            }
            /// Informacion de RED///////////////////////////////
            Row {
                AssistChip(
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(3.dp, firstColor),
                    onClick = { Log.d("Assist chip", "WbTwilight world") },
                    label = {
                        if (internetAvailable) {
                            Text(
                                text = "con conexion",
                                color = firstColor,
                                style = textStyleTittle2,
                                onTextLayout = { /* No se necesita hacer nada aquí */ })
                        } else {
                            Text(
                                text = "sin conexion",
                                color = firstColor,
                                style = textStyleTittle2,
                                onTextLayout = { /* No se necesita hacer nada aquí */ })
                        }
                    },
                    leadingIcon = {
                        if (internetAvailable) {
                            Icon(
                                Icons.Filled.Wifi,
                                contentDescription = "Localized description",
                                Modifier.size(AssistChipDefaults.IconSize),
                                tint = firstColor
                            )

                        } else {
                            Icon(
                                Icons.Filled.WifiOff,
                                contentDescription = "Localized description",
                                Modifier.size(AssistChipDefaults.IconSize),
                                tint = firstColor
                            )
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            //// Texto de la 2da Authentificacion por mail ////////////////////////////
            Row {
                Text(
                    text = "Ingresa el codigo que acabamos de enviar a tu correo",
                    color = firstColor,
                    style = textStyleTittle2,
                    onTextLayout = { /* No se necesita hacer nada aquí */ },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            //// Formularios ingreso de codigo ////////////////////////////
            Row(
                horizontalArrangement = Arrangement.Absolute.Center
            ) {
                /// VALOR DIGITO 1 ///////////////////////////////////////////////
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    TextField(
                        value = primerValorCodigo.value,
                        onValueChange = {
                            handleTextChange(0, it.text)
                            //primerValorCodigo.value = it
                        },
                        textStyle = TextStyle(
                            color = firstColor,
                            fontSize = 16.sp,
                            fontFamily = com.example.cypher_vault.view.resources.fontFamily,
                            fontWeight = FontWeight.Bold
                        ),
                        placeholder = {
                            Text(
                                "",
                                style = TextStyle(
                                    color = Color.Gray,
                                    fontSize = 16.sp,
                                    fontFamily = com.example.cypher_vault.view.resources.fontFamily
                                )
                            )
                        },
                        label = {
                            Text(
                                "",
                                fontSize = 20.sp,
                                fontFamily = com.example.cypher_vault.view.resources.fontFamily,
                                color = com.example.cypher_vault.view.resources.thirdColor,
                                fontWeight = FontWeight.Bold,
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(4.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            cursorColor = com.example.cypher_vault.view.resources.thirdColor,
                            focusedIndicatorColor = com.example.cypher_vault.view.resources.firstColor,
                            unfocusedIndicatorColor = com.example.cypher_vault.view.resources.firstColor,
                        ),
                        modifier = Modifier
                            .width(45.dp) // Establece un ancho fijo para el TextField
                            .padding(top = 15.dp)
                            .border(
                                BorderStroke(
                                    3.dp,
                                    com.example.cypher_vault.view.resources.firstColor
                                ),
                                shape = RoundedCornerShape(4.dp),
                            )
                            .focusRequester(focusRequesters[0])
                           
                    )
                }
                /// VALOR DIGITO 2 ///////////////////////////////////////////////
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    TextField(
                        value = segundoValorCodigo.value,
                        onValueChange = {
                            handleTextChange(1, it.text)
                                        //segundoValorCodigo.value = it
                        },
                        textStyle = TextStyle(
                            color = firstColor,
                            fontSize = 16.sp,
                            fontFamily = com.example.cypher_vault.view.resources.fontFamily,
                            fontWeight = FontWeight.Bold
                        ),
                        placeholder = {
                            Text(
                                "",
                                style = TextStyle(
                                    color = Color.Gray,
                                    fontSize = 16.sp,
                                    fontFamily = com.example.cypher_vault.view.resources.fontFamily
                                )
                            )
                        },
                        label = {
                            Text(
                                "",
                                fontSize = 20.sp,
                                fontFamily = com.example.cypher_vault.view.resources.fontFamily,
                                color = com.example.cypher_vault.view.resources.thirdColor,
                                fontWeight = FontWeight.Bold,
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(4.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            cursorColor = com.example.cypher_vault.view.resources.thirdColor,
                            focusedIndicatorColor = com.example.cypher_vault.view.resources.firstColor,
                            unfocusedIndicatorColor = com.example.cypher_vault.view.resources.firstColor,
                        ),
                        modifier = Modifier
                            .width(45.dp) // Establece un ancho fijo para el TextField
                            .padding(top = 15.dp)
                            .border(
                                BorderStroke(
                                    3.dp,
                                    com.example.cypher_vault.view.resources.firstColor
                                ),
                                shape = RoundedCornerShape(4.dp),
                            )
                            .focusRequester(focusRequesters[1])

                    )
                }
                /// VALOR DIGITO 3 ///////////////////////////////////////////////
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    TextField(
                        value = tercerValorCodigo.value,
                        onValueChange = {
                            handleTextChange(2, it.text)
                            //tercerValorCodigo.value = it
                        },
                        textStyle = TextStyle(
                            color = firstColor,
                            fontSize = 16.sp,
                            fontFamily = com.example.cypher_vault.view.resources.fontFamily,
                            fontWeight = FontWeight.Bold
                        ),
                        placeholder = {
                            Text(
                                "",
                                style = TextStyle(
                                    color = Color.Gray,
                                    fontSize = 16.sp,
                                    fontFamily = com.example.cypher_vault.view.resources.fontFamily
                                )
                            )
                        },
                        label = {
                            Text(
                                "",
                                fontSize = 20.sp,
                                fontFamily = com.example.cypher_vault.view.resources.fontFamily,
                                color = com.example.cypher_vault.view.resources.thirdColor,
                                fontWeight = FontWeight.Bold,
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(4.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            cursorColor = com.example.cypher_vault.view.resources.thirdColor,
                            focusedIndicatorColor = com.example.cypher_vault.view.resources.firstColor,
                            unfocusedIndicatorColor = com.example.cypher_vault.view.resources.firstColor,
                        ),
                        modifier = Modifier
                            .width(45.dp) // Establece un ancho fijo para el TextField
                            .padding(top = 15.dp)
                            .border(
                                BorderStroke(
                                    3.dp,
                                    com.example.cypher_vault.view.resources.firstColor
                                ),
                                shape = RoundedCornerShape(4.dp),
                            )
                            .focusRequester(focusRequesters[2])

                    )
                }
                /// VALOR DIGITO 4 ///////////////////////////////////////////////
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    TextField(
                        value = cuartoValorCodigo.value,
                        onValueChange = {
                            handleTextChange(3, it.text)
                            //cuartoValorCodigo.value = it
                        },
                        textStyle = TextStyle(
                            color = firstColor,
                            fontSize = 16.sp,
                            fontFamily = com.example.cypher_vault.view.resources.fontFamily,
                            fontWeight = FontWeight.Bold
                        ),
                        placeholder = {
                            Text(
                                "",
                                style = TextStyle(
                                    color = Color.Gray,
                                    fontSize = 16.sp,
                                    fontFamily = com.example.cypher_vault.view.resources.fontFamily
                                )
                            )
                        },
                        label = {
                            Text(
                                "",
                                fontSize = 20.sp,
                                fontFamily = com.example.cypher_vault.view.resources.fontFamily,
                                color = com.example.cypher_vault.view.resources.thirdColor,
                                fontWeight = FontWeight.Bold,
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(4.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            cursorColor = com.example.cypher_vault.view.resources.thirdColor,
                            focusedIndicatorColor = com.example.cypher_vault.view.resources.firstColor,
                            unfocusedIndicatorColor = com.example.cypher_vault.view.resources.firstColor,
                        ),
                        modifier = Modifier
                            .width(45.dp) // Establece un ancho fijo para el TextField
                            .padding(top = 15.dp)
                            .border(
                                BorderStroke(
                                    3.dp,
                                    com.example.cypher_vault.view.resources.firstColor
                                ),
                                shape = RoundedCornerShape(4.dp),
                            )
                            .focusRequester(focusRequesters[3])

                    )
                }
                /// VALOR DIGITO 5 ///////////////////////////////////////////////
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    TextField(
                        value = quintoValorCodigo.value,
                        onValueChange = {
                            handleTextChange(4, it.text)
                            //quintoValorCodigo.value = it
                        },
                        textStyle = TextStyle(
                            color = firstColor,
                            fontSize = 16.sp,
                            fontFamily = com.example.cypher_vault.view.resources.fontFamily,
                            fontWeight = FontWeight.Bold
                        ),
                        placeholder = {
                            Text(
                                "",
                                style = TextStyle(
                                    color = Color.Gray,
                                    fontSize = 16.sp,
                                    fontFamily = com.example.cypher_vault.view.resources.fontFamily
                                )
                            )
                        },
                        label = {
                            Text(
                                "",
                                fontSize = 20.sp,
                                fontFamily = com.example.cypher_vault.view.resources.fontFamily,
                                color = com.example.cypher_vault.view.resources.thirdColor,
                                fontWeight = FontWeight.Bold,
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(4.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            cursorColor = com.example.cypher_vault.view.resources.thirdColor,
                            focusedIndicatorColor = com.example.cypher_vault.view.resources.firstColor,
                            unfocusedIndicatorColor = com.example.cypher_vault.view.resources.firstColor,
                        ),
                        modifier = Modifier
                            .width(45.dp) // Establece un ancho fijo para el TextField
                            .padding(top = 15.dp)
                            .border(
                                BorderStroke(
                                    3.dp,
                                    com.example.cypher_vault.view.resources.firstColor
                                ),
                                shape = RoundedCornerShape(4.dp),
                            )
                            .focusRequester(focusRequesters[4])

                    )
                }
            }
            //// Boton del formulario //////////////////////////////////////////////////////////////////////////
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.Absolute.Center
            ) {
                OutlinedButton(
                    onClick = {
                        if (comprobarCodigo(mailCode,primerValorCodigo.value.text, segundoValorCodigo.value.text, tercerValorCodigo.value.text, cuartoValorCodigo.value.text, quintoValorCodigo.value.text)) {
                            /////Ingreso de usuario
                            userAccessController.insertUserIncome(userId)
                            ///////////////////////
                            navController.navigateToGallery(userId)
                        } else {
                            Toast.makeText(context, "Error en la autenticacion", Toast.LENGTH_SHORT)
                                .show()
                            mailCode = ""
                            navController.navigateToListLogin()
                        }
                    },
                    shape = RoundedCornerShape(15.dp),
                    border = BorderStroke(3.dp, Color.Gray),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = thirdColor,
                        contentColor = wingWhite
                    ),
                    modifier = Modifier
                        .width(200.dp)
                        .padding(vertical = 30.dp)
                ) {
                    Text(
                        "Comprobar",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Row {
                Text(
                    text = "El codigo que se envia es : $mailCode",
                    color = firstColor,
                    style = textStyleTittle2,
                    onTextLayout = { /* No se necesita hacer nada aquí */ },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

fun comprobarCodigo(mailCode: String, value1: String, value2: String, value3: String, value4: String, value5: String): Boolean {
    Log.d("Comprobando codigo", "COMPROBACION : $mailCode = $value1$value2$value3$value4$value5")
    return mailCode == value1 + value2 + value3 + value4 + value5
}


/// TARJETA DE LA 2DA AUTHENTICACION PARTE DEL DIA ////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
@Composable
fun ElevatedCardDayPart(
    context: Context,
    navController: NavController,
    secondAuthController: SecondAuthController,
    userAccessController: UserAccessController,
    userId: String,
    isInternetAvailable: Boolean
) {
    var dayPart by remember { mutableStateOf(SecondAuthManager.DayPart.MORNING) }
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .padding(16.dp)
            .background(mainBackgroundColor)
            .fillMaxHeight(),
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .background(wingWhite),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            /// TITULO //////////////////////////////////////////////////////////////////
            Spacer(modifier = Modifier.height(15.dp))
            Row(
                horizontalArrangement = Arrangement.Absolute.Center
            ) {
                Icon(
                    Icons.Filled.Lock,
                    contentDescription = "Localized description",
                    Modifier.size(150.dp),
                    tint = secondColor
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
            Row(
                horizontalArrangement = Arrangement.Absolute.Center
            ) {
                Text(
                    text = "Segunda",
                    fontSize = 20.sp,
                    color = secondColor,
                    style = textStyleTittle2,
                    onTextLayout = { /* No se necesita hacer nada aquí */ }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.Absolute.Center
            ) {
                Text(
                    text = "Autentificacion",
                    fontSize = 20.sp,
                    color = secondColor,
                    style = textStyleTittle2,
                    onTextLayout = { /* No se necesita hacer nada aquí */ }
                )
            }
            /// Informacion de RED///////////////////////////////
            Row {
                AssistChip(
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(3.dp, firstColor),
                    onClick = { Log.d("Assist chip", "WbTwilight world") },
                    label = {
                        if (isInternetAvailable) {
                            Text(
                                text = "con conexion",
                                color = firstColor,
                                style = textStyleTittle2,
                                onTextLayout = { /* No se necesita hacer nada aquí */ })
                        } else {
                            Text(
                                text = "sin conexion",
                                color = firstColor,
                                style = textStyleTittle2,
                                onTextLayout = { /* No se necesita hacer nada aquí */ })
                        }
                    },
                    leadingIcon = {
                        if (isInternetAvailable) {
                            Icon(
                                Icons.Filled.Wifi,
                                contentDescription = "Localized description",
                                Modifier.size(AssistChipDefaults.IconSize),
                                tint = firstColor
                            )

                        } else {
                            Icon(
                                Icons.Filled.WifiOff,
                                contentDescription = "Localized description",
                                Modifier.size(AssistChipDefaults.IconSize),
                                tint = firstColor
                            )
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            /// TEXTO DE LA CARD //////////////////////////////////////////////////////////////////
            Row {
                Text(
                    text = "¿En que momento del dia te conectaste la ultima vez?",
                    color = firstColor,
                    style = textStyleTittle2,
                    onTextLayout = { /* No se necesita hacer nada aquí */ },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                horizontalArrangement = Arrangement.Absolute.Center
            ) {
                AssistChip(
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(3.dp, firstColor),
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color.Transparent,
                        labelColor = firstColor,
                    ),
                    modifier = Modifier
                        .width(250.dp)
                        .padding(top = 15.dp),
                    onClick = { dayPart = SecondAuthManager.DayPart.MORNING },
                    label = { Text("Mañana(7:00AM-15:00PM)") },
                    leadingIcon = {
                        if (dayPart == SecondAuthManager.DayPart.MORNING) {
                            Icon(
                                Icons.Outlined.Check,
                                contentDescription = "Localized description",
                                Modifier.size(AssistChipDefaults.IconSize),
                                tint = firstColor
                            )
                        } else {
                            Icon(
                                Icons.Outlined.WbTwilight,
                                contentDescription = "Localized description",
                                Modifier.size(AssistChipDefaults.IconSize),
                                tint = firstColor
                            )
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.Absolute.Center
            ) {
                AssistChip(
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(3.dp, firstColor),
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color.Transparent,
                        labelColor = firstColor,
                    ),
                    modifier = Modifier
                        .width(250.dp)
                        .padding(top = 15.dp),
                    onClick = { dayPart = SecondAuthManager.DayPart.AFTERNOON },
                    label = { Text("Tarde(15:00PM-23:00PM)") },
                    leadingIcon = {
                        if (dayPart == SecondAuthManager.DayPart.AFTERNOON) {
                            Icon(
                                Icons.Outlined.Check,
                                contentDescription = "Localized description",
                                Modifier.size(AssistChipDefaults.IconSize),
                                tint = firstColor
                            )
                        } else {
                            Icon(
                                Icons.Outlined.WbSunny,
                                contentDescription = "Localized description",
                                Modifier.size(AssistChipDefaults.IconSize),
                                tint = firstColor
                            )
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.Absolute.Center
            ) {
                AssistChip(
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(3.dp, firstColor),
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = Color.Transparent,
                        labelColor = firstColor,
                    ),
                    modifier = Modifier
                        .width(250.dp)
                        .padding(top = 15.dp),
                    onClick = { dayPart = SecondAuthManager.DayPart.EVENING },
                    label = { Text("Noche(23:00PM-7:00AM)") },
                    leadingIcon = {
                        if (dayPart == SecondAuthManager.DayPart.EVENING) {
                            Icon(
                                Icons.Outlined.Check,
                                contentDescription = "Localized description",
                                Modifier.size(AssistChipDefaults.IconSize),
                                tint = firstColor
                            )
                        } else {
                            Icon(
                                Icons.Outlined.Nightlight,
                                contentDescription = "Localized description",
                                Modifier.size(AssistChipDefaults.IconSize),
                                tint = firstColor
                            )
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                OutlinedButton(
                    onClick = {
                        if (secondAuthController.authenticateWOConection(userId, dayPart)) {
                            /////Ingreso de usuario
                            userAccessController.insertUserIncome(userId)
                            ///////////////////////
                            navController.navigateToGallery(userId)
                        } else {
                            Toast.makeText(context, "Error en la autenticacion", Toast.LENGTH_SHORT)
                                .show()
                            navController.navigateToListLogin()
                        }
                    },
                    shape = RoundedCornerShape(15.dp),
                    border = BorderStroke(3.dp, Color.Gray),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = thirdColor,
                        contentColor = wingWhite
                    ),
                    modifier = Modifier
                        .width(200.dp)
                        .padding(vertical = 30.dp)
                ) {
                    Text(
                        "Comprobar",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

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