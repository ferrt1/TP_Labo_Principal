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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cypher_vault.R
import com.example.cypher_vault.controller.authentication.AuthenticationController
import com.example.cypher_vault.controller.authentication.SecondAuthController
import com.example.cypher_vault.controller.data.DatabaseController
import com.example.cypher_vault.controller.income.UserAccessController
import com.example.cypher_vault.controller.lockaccount.BlockUserController
import com.example.cypher_vault.controller.navigation.NavController
import com.example.cypher_vault.controller.service.ServiceController
import com.example.cypher_vault.database.BlockedUsers
import com.example.cypher_vault.model.authentication.SecondAuthManager
import com.example.cypher_vault.model.income.UserAccessManager
import com.example.cypher_vault.model.lockaccount.BlockUserManager
import com.example.cypher_vault.model.service.ServiceManager
import com.example.cypher_vault.view.gallery.firstColor
import com.example.cypher_vault.view.gallery.textStyleTittle2
import com.example.cypher_vault.view.gallery.thirdColor
import kotlinx.coroutines.launch

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
fun ConfirmationLoginScreen(navController: NavController, userId: String, fromCamera : Boolean) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val authenticationController = AuthenticationController(context)
    var dbc = DatabaseController()
    var secondAuthManager = SecondAuthManager()
    var secondAuthController = SecondAuthController(secondAuthManager)
    var blockUserManager = BlockUserManager()
    var blockUserController = BlockUserController(blockUserManager)

    //Variables de paneles principales
    var showImage by remember { mutableStateOf(false) }
    var showConfirmationLoguin by remember { mutableStateOf(false) }
    var showDenyAccess by remember { mutableStateOf(false) }
    var fromCamera by remember { mutableStateOf(fromCamera) }

    //Prototipado de la pantalla para logueo con camara
    val imagePrintRegister = remember { mutableStateOf<Bitmap?>(null) }
    val imagePrintLogin = remember { mutableStateOf<Bitmap?>(null) }
    val result = remember { mutableStateOf<Boolean?>(null) }
    val distance = remember { mutableStateOf<Float?>(null) }
    var showData by remember { mutableStateOf(false) }
    LaunchedEffect(userId) {
        authenticationController.authenticate(userId) { isSuccess, registerBitmap, loginBitmap, dist ->
            result.value = isSuccess
            imagePrintRegister.value = registerBitmap
            imagePrintLogin.value = loginBitmap
            distance.value = dist
        }
    }

    //Se agrega ingreso de usuario
    val userAccessManager = UserAccessManager()
    val userAccessController = UserAccessController(userAccessManager)

    //Variables de la 2da Authentificacion
    var isSecondAuth by remember { mutableStateOf<Boolean?>(null) }
    LaunchedEffect(userId) {
        launch {
            isSecondAuth = dbc.getUserById(userId)?.authentication
        }
    }
    var isAuthenticaed by remember { mutableStateOf(false) }

    //Variables de conexion a la red de internet
    val serviceManager = remember { ServiceManager(context) }
    val serviceController = remember { ServiceController(serviceManager) }
    var isInternetAvailable by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isInternetAvailable = serviceController.isInternetAvailable()
    }

    //Variables para el bloqueo de usuario
    var intentosTotales by remember { mutableStateOf<Int?>(null) }
    var blockedUser by remember { mutableStateOf<BlockedUsers?>(null) }

    LaunchedEffect(userId) {
        scope.launch {
            blockedUser = blockUserController.getBlockedUser(userId)
            intentosTotales = blockedUser?.attempts
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    ////// DATOS DE PRUEBA /////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
    @Composable
    fun datosParaPruebas(
        distance: MutableState<Float?>,
        imagePrintRegister: MutableState<Bitmap?>,
        imagePrintLogin: MutableState<Bitmap?>
    ): Boolean {
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
        if (showData && distance.value != null) {
            Text(
                "Distancia calculada: ${distance.value}",
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold,
                color = thirdColor,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
        if (showImage) {
            ImageWithLandmarks(imagePrintRegister)
            ImageWithLandmarks(imagePrintLogin)
        }
        return showData
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    //// Pantalla de confirmacion de logueo //////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Composable
    fun pantallaDeConfirmacion(
        userId: String,
        userAccessController: UserAccessController,
        navController: NavController
    ) {
        var insertarIngreso by remember { mutableStateOf(false) }
        Image(
            painter = painterResource(id = R.drawable.successful),
            contentDescription = "Bienvenido!",
            modifier = Modifier.size(128.dp)
        )
        Text(
            "¡Bienvenido!",
            fontSize = 20.sp,
            fontFamily = fontFamily,
            color = thirdColor,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(top = 16.dp)
        )
        OutlinedButton(
            onClick = {
                insertarIngreso = true
                navController.navigateToGallery(userId)
            },
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
        OutlinedButton(
            onClick = { showData = !showData },
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
                if (showData) "Ocultar datos" else "Ver datos",
                fontFamily = fontFamily,
                color = thirdColor,
                fontWeight = FontWeight.Bold
            )
        }
        if (insertarIngreso) {
            LaunchedEffect(userId) {
                userAccessController.insertUserIncome(userId)
                insertarIngreso = false // Close dialog or reset state after execution
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    ///// Pantalla de acceso denegado ////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////
    @Composable
    fun pantallaAccesoDenegado(
        distance: MutableState<Float?>,
        navController: NavController
    ) {
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
            onClick = { showData = !showData },
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
                if (showData) "Ocultar datos" else "Ver datos",
                fontFamily = fontFamily,
                color = thirdColor,
                fontWeight = FontWeight.Bold
            )
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //// SE INSERTA ESO ACA POR EL SCOPE DE VARIABLES //////////////////////////////////////////////////////////////
    /// TARJETA DE LA 2DA AUTHENTICACION PARTE DEL MAIL ////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Composable
    fun ElevatedCardMailConfirmation(
        context: Context,
        navController: NavController,
        secondAuthController: SecondAuthController,
        userAccessController: UserAccessController,
        userId: String,
        internetAvailable: Boolean,
        mailCode: String
    ) {
        var valorCodigo = remember { mutableStateOf(TextFieldValue()) }

        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            ),
            modifier = Modifier
                .padding(15.dp)
                .background(mainBackgroundColor)
                .fillMaxHeight(),
        ) {
            Column(
                modifier = Modifier
                    .padding(15.dp)
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
                        textAlign = TextAlign.Center,
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
                            value = valorCodigo.value,
                            onValueChange = {nuevoValor ->
                                valorCodigo.value = nuevoValor
                            },
                            textStyle = TextStyle(
                                color = firstColor,
                                fontSize = 30.sp,
                                fontFamily = com.example.cypher_vault.view.resources.fontFamily,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
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
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                                .width(200.dp) // Establece un ancho fijo para el TextField
                                .border(
                                    BorderStroke(
                                        3.dp,
                                        com.example.cypher_vault.view.resources.firstColor
                                    ),
                                    shape = RoundedCornerShape(4.dp),
                                )
                        )
                    }
                }
                //// Botones del formulario //////////////////////////////////////////////////////////////////////////
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.Absolute.Center
                ) {
                    /// BOTON LIMPIAR
                    Column {
                        Row(
                            horizontalArrangement = Arrangement.Absolute.Center
                        ) {
                            OutlinedButton(
                                onClick = {
                                    valorCodigo.value = TextFieldValue("")
                                },
                                shape = RoundedCornerShape(15.dp),
                                border = BorderStroke(3.dp, Color.Gray),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = thirdColor,
                                    contentColor = wingWhite
                                ),
                                modifier = Modifier
                                    .width(200.dp)
                            ) {
                                Text(
                                    "Limpiar campos",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Row(
                            horizontalArrangement = Arrangement.Absolute.Center
                        ) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Intento $intentosTotales de 3",
                                color = firstColor,
                                style = textStyleTittle2,
                                onTextLayout = { /* No se necesita hacer nada aquí */ },
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.Absolute.Center
                        ) {
                            OutlinedButton(
                                onClick = {

                                    if (comprobarCodigo(
                                            mailCode,
                                            valorCodigo.value.text
                                        )
                                    ) {
                                        intentosTotales = 0
                                        Log.d("lockAccount", "////////IntentosTotales: $intentosTotales")
                                        scope.launch {
                                            blockedUser?.let { blockUserController.setAttempts(it, intentosTotales!!) }
                                            Log.d("lockAccount", "///////updateAttempts completed")
                                            isAuthenticaed = true
                                            showConfirmationLoguin = true
                                        }
                                    } else {
                                        intentosTotales = intentosTotales!! + 1
                                        Log.d("lockAccount", "////////IntentosTotales: $intentosTotales")
                                        scope.launch {
                                            blockedUser?.let { blockUserController.setAttempts(it, intentosTotales!!) }
                                            Log.d("lockAccount", "///////updateAttempts completed")
                                            Toast.makeText(
                                                context,
                                                "Error en la autenticacion",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            navController.navigateToListLogin()
                                        }
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
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///// SE INSERTA ESO ACA POR EL SCOPE DE VARIABLES //////////////////////////////////////////////////////////////
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
                .padding(15.dp)
                .background(mainBackgroundColor)
                .fillMaxHeight(),
        ) {
            Column(
                modifier = Modifier
                    .padding(15.dp)
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
                        textAlign = TextAlign.Center,
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
                        label = {
                            Text("Mañana(7:00AM-15:00PM)",textAlign = TextAlign.Center,)
                                },
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
                        label = {
                            Text("Tarde(15:00PM-23:00PM)",textAlign = TextAlign.Center,)
                                },
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
                            .width(250.dp),
                        onClick = { dayPart = SecondAuthManager.DayPart.EVENING },
                        label = {
                            Text("Noche(23:00PM-7:00AM)",textAlign = TextAlign.Center,)
                                },
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
                /////// TEXTO DE INTENTOS ////////
                Row(
                    horizontalArrangement = Arrangement.Absolute.Center
                ) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Intento 1 de 1",
                        color = firstColor,
                        style = textStyleTittle2,
                        onTextLayout = { /* No se necesita hacer nada aquí */ },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                ///// Boton de comprobacion //////////////////////////////////////////////////////////////////
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    OutlinedButton(
                        onClick = {
                            if (secondAuthController.authenticateWOConection(userId, dayPart)) {
                                isAuthenticaed = true
                                showConfirmationLoguin = true
                            } else {
                                Log.d("lockAccount", "////////Cuenta bloqueada")
                                scope.launch {
                                    blockUserController.blockUser(userId)
                                    Log.d("lockAccount", "///////Cuenta bloqueada completed")
                                    Toast.makeText(
                                        context,
                                        "Error en la autenticacion, cuenta bloqueada.",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    navController.navigateToListLogin()
                                }
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
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////// LOGICA DE LA PANTALLA PRINCIPAL //////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //Elementos de la 2da Authentificacion//////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////////////
        if (!fromCamera) {
            Log.d("ConfirmationLoginScreen", "resul.value = $result")
            Log.d("ConfirmationLoginScreen", "imagePrintLogin.value == null")
            if (isSecondAuth == true) {
                Log.d("ConfirmationLoginScreen", "isSecondAuth == true")
                /// SIN IMAGENES Y CON SEGUNDA ACTIVADA ////////////////////////
                if (!isAuthenticaed) {
                    showDenyAccess = false
                    showConfirmationLoguin = false
                    if (isInternetAvailable) {
                        var mailCode: String = remember { secondAuthController.sendMail(context, userId) }
                        Log.d("MailConfirmation", "salida: $mailCode")
                        ElevatedCardMailConfirmation(
                            context,
                            navController,
                            secondAuthController,
                            userAccessController,
                            userId,
                            isInternetAvailable,
                            mailCode
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
                }
            } else {
                Log.d("ConfirmationLoginScreen", "isSecondAuth == false")
                /// SIN IMAGENES Y SIN SEGUNDA ACTIVADA ////////////////////////
                // Ingreso de usuario
                showConfirmationLoguin = true
            }
        } else {
            Log.d("ConfirmationLoginScreen", "resul.value = $result")
            Log.d("ConfirmationLoginScreen", "imagePrintLogin.value != null")
            /// CON IMAGENES Y CON SEGUNDA ACTIVADA ////////////////////////
            if (isSecondAuth == true) {
                Log.d("ConfirmationLoginScreen", "isSecondAuth == true")
                when (result.value) {
                    null -> {
                        Text(
                            "Esperando...",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    true -> {
                        if (!isAuthenticaed) {
                            showDenyAccess = false
                            showConfirmationLoguin = false
                            if (isInternetAvailable) {
                                var mailCode: String = remember { secondAuthController.sendMail(context, userId) }
                                Log.d("MailConfirmation", "salida: $mailCode")
                                ElevatedCardMailConfirmation(
                                    context,
                                    navController,
                                    secondAuthController,
                                    userAccessController,
                                    userId,
                                    isInternetAvailable,
                                    mailCode
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
                        }
                    }

                    false -> {
                        showConfirmationLoguin = false
                        showDenyAccess = true
                    }
                }
            } else {
                Log.d("ConfirmationLoginScreen", "isSecondAuth == false")
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
                        showDenyAccess = false
                        showConfirmationLoguin = true
                    }

                    false -> {
                        showConfirmationLoguin = false
                        showDenyAccess = true
                    }
                }
            }
        }
        //// Pantalla de confirmacion de logueo //////////////////////////////////////////////////////////////////////////
        if (showConfirmationLoguin) {
            pantallaDeConfirmacion(userId, userAccessController, navController)
        }
        /// Pantalla de denegacion de acceso //////////////////////////////////////////////////////////////////////////
        if (showDenyAccess) {
            pantallaAccesoDenegado(distance, navController)
        }
        ////// Mostrar o ocultar datos //////////////////////////////////////////////////////////////////////////
        if (showData) {
            datosParaPruebas(distance, imagePrintRegister, imagePrintLogin)
        }
        //Boton vuelta al logueo //////////////////////////////////////////////////////////////////////////
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
                .padding(bottom = 30.dp)
        ) {
            Text(
                "Volver",
                fontWeight = FontWeight.Bold
            )
        }
    }
}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////


@Composable
private fun hayImagen(result: MutableState<Boolean?>) =
    result.value == true

fun comprobarCodigo(
    mailCode: String,
    value1: String
): Boolean {
    Log.d("Comprobando codigo", "COMPROBACION : $mailCode = $value1")
    return mailCode == value1
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

