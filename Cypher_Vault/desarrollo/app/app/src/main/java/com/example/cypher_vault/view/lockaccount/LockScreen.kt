package com.example.cypher_vault.view.lockaccount

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cypher_vault.controller.authentication.SecondAuthController
import com.example.cypher_vault.controller.data.DatabaseController
import com.example.cypher_vault.controller.lockaccount.BlockUserController
import com.example.cypher_vault.controller.navigation.NavController
import com.example.cypher_vault.controller.service.ServiceController
import com.example.cypher_vault.database.BlockedUsers
import com.example.cypher_vault.model.authentication.SecondAuthManager
import com.example.cypher_vault.model.lockaccount.BlockUserManager
import com.example.cypher_vault.model.service.ServiceManager
import com.example.cypher_vault.view.gallery.textStyleTittle2
import com.example.cypher_vault.view.login.comprobarCodigo
import com.example.cypher_vault.view.login.mainBackgroundColor
import com.example.cypher_vault.view.login.secondColor
import com.example.cypher_vault.view.login.wingWhite
import com.example.cypher_vault.view.resources.fontFamily
import com.example.cypher_vault.view.resources.thirdColor
import kotlinx.coroutines.launch

private var databaseController = DatabaseController()
private val firstColor = Color(0xFF02a6c3)

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun LockScreen(navController: NavController, userId: String) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    //Variables del usuario
    var blockedUser by remember { mutableStateOf<BlockedUsers?>(null) }
    var isBlocked by remember { mutableStateOf(false) }
    var blockedDate by remember { mutableStateOf(0L) }
    LaunchedEffect(Unit) {
        blockedUser = databaseController.getBlockedUser(userId)
        isBlocked = blockedUser?.blocked_user == true
        blockedDate = blockedUser?.block_date!!
    }
    //Variables para el envio de mail
//    var user by remember { mutableStateOf<User?>(null) }
//    var email by remember { mutableStateOf("") }
//    LaunchedEffect(userId) {
//        launch {
//            user = databaseController.getUserById(userId)
//            email = user?.email.toString()
//        }
//    }

    //Variables de conexion a la red de internet
    val serviceManager = remember { ServiceManager(context) }
    val serviceController = remember { ServiceController(serviceManager) }
    var isInternetAvailable by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isInternetAvailable = serviceController.isInternetAvailable()
    }

    //Variables de bloqueo
    var blockUserManager = BlockUserManager()
    var blockUserController = BlockUserController(blockUserManager)
    val coroutineScope = rememberCoroutineScope()

    //Variables del formulario
    var secondAuthManager = SecondAuthManager()
    var secondAuthController = SecondAuthController(secondAuthManager)

    var valorCodigo = remember { mutableStateOf(TextFieldValue()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                    .fillMaxSize()
                    .padding(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //Pantallita de recepcion
                //////////////// CON CONEXION A INTERNET //////////////////////////////////////////////////////////////////
                ///////////////////////////////////////////////////////////////////////////////////////////////////////////
                if (isInternetAvailable) {
                    var mailCode: String =
                        remember { blockUserController.sendMail(context, userId) }
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
                            text = "Cuenta",
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
                            text = "Bloqueada",
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
                            border = BorderStroke(
                                3.dp,
                                com.example.cypher_vault.view.gallery.firstColor
                            ),
                            onClick = { Log.d("Assist chip", "WbTwilight world") },
                            label = {
                                if (isInternetAvailable) {
                                    Text(
                                        text = "con conexion",
                                        color = com.example.cypher_vault.view.gallery.firstColor,
                                        style = textStyleTittle2,
                                        onTextLayout = { /* No se necesita hacer nada aquí */ })
                                } else {
                                    Text(
                                        text = "sin conexion",
                                        color = com.example.cypher_vault.view.gallery.firstColor,
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
                                        tint = com.example.cypher_vault.view.gallery.firstColor
                                    )

                                } else {
                                    Icon(
                                        Icons.Filled.WifiOff,
                                        contentDescription = "Localized description",
                                        Modifier.size(AssistChipDefaults.IconSize),
                                        tint = com.example.cypher_vault.view.gallery.firstColor
                                    )
                                }
                            }
                        )
                    }
                    //// Mensaje cuando hay conexion a internet //////////////////////////////////////////////////////////////////
                    Row(
                        horizontalArrangement = Arrangement.Absolute.Center
                    ) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "En este momento tu cuenta se encuentra bloqueda," +
                                    "para desbloquearla ingresa el codigo que enviamos a tu mail." +
                                    " Ten en cuenta que si su ingreso es erroneo se bloquea la cuenta por 30min." +
                                    "Recuerda que puedes usar el boton de limpiar campos para volver a escribirlo.",
                            color = com.example.cypher_vault.view.gallery.firstColor,
                            style = textStyleTittle2,
                            textAlign = TextAlign.Center,
                            onTextLayout = { /* No se necesita hacer nada aquí */ },
                            modifier = Modifier.padding(horizontal = 12.dp)
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
                                onValueChange = { nuevoValor ->
                                    valorCodigo.value = nuevoValor
                                },
                                textStyle = TextStyle(
                                    color = com.example.cypher_vault.view.gallery.firstColor,
                                    fontSize = 30.sp,
                                    fontFamily = fontFamily,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                ),
                                placeholder = {
                                    Text(
                                        "",
                                        style = TextStyle(
                                            color = Color.Gray,
                                            fontSize = 16.sp,
                                            fontFamily = fontFamily
                                        )
                                    )
                                },
                                label = {
                                    Text(
                                        "",
                                        fontSize = 20.sp,
                                        fontFamily = fontFamily,
                                        color = thirdColor,
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
                                    cursorColor = thirdColor,
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
                                        containerColor = com.example.cypher_vault.view.gallery.thirdColor,
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
                                    text = "Intento 1 de 1",
                                    color = com.example.cypher_vault.view.gallery.firstColor,
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
                                            Log.d("lockAccount", "////////Codigo Correcto")
                                            scope.launch {
                                                userId.let { blockUserController.deleteBlock(it) }
                                                Log.d("lockAccount", "///////Blockeo Eliminado")
                                                Toast.makeText(
                                                    context,
                                                    "Bloqueo Eliminado",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                navController.navigateToGallery(userId)
                                            }
                                        } else {
                                            Log.d("lockAccount", "////////Codigo Incorrecto")
                                            scope.launch {
                                                var time = System.currentTimeMillis()
                                                userId.let {
                                                    blockUserController.setBlockDate(
                                                        it,
                                                        time
                                                    )
                                                }
                                                Log.d(
                                                    "lockAccount",
                                                    "///////updateAttempts completed"
                                                )
                                                Toast.makeText(
                                                    context,
                                                    "Error en la autenticacion,se bloquea la cuenta por 30min",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                navController.navigateToListLogin()
                                            }
                                        }
                                    },
                                    shape = RoundedCornerShape(15.dp),
                                    border = BorderStroke(3.dp, Color.Gray),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = com.example.cypher_vault.view.gallery.thirdColor,
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
                } else {
                    /////// SIN CONEXION A INTERNET //////////////////////////////////////////////////////////////////
                    /////////////////////////////////////////////////////////////////////////////////////////////////////////
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
                            text = "Cuenta",
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
                            text = "Bloqueada",
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
                            border = BorderStroke(
                                3.dp,
                                com.example.cypher_vault.view.gallery.firstColor
                            ),
                            onClick = { Log.d("Assist chip", "WbTwilight world") },
                            label = {
                                if (isInternetAvailable) {
                                    Text(
                                        text = "con conexion",
                                        color = com.example.cypher_vault.view.gallery.firstColor,
                                        style = textStyleTittle2,
                                        onTextLayout = { /* No se necesita hacer nada aquí */ })
                                } else {
                                    Text(
                                        text = "sin conexion",
                                        color = com.example.cypher_vault.view.gallery.firstColor,
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
                                        tint = com.example.cypher_vault.view.gallery.firstColor
                                    )

                                } else {
                                    Icon(
                                        Icons.Filled.WifiOff,
                                        contentDescription = "Localized description",
                                        Modifier.size(AssistChipDefaults.IconSize),
                                        tint = com.example.cypher_vault.view.gallery.firstColor
                                    )
                                }
                            }
                        )
                    }
                    //// Mensaje cuando no hay conexion a internet //////////////////////////////////////////////////////////////////
                    Spacer(modifier = Modifier.height(20.dp))
                    //// Texto de la 2da Authentificacion por mail ////////////////////////////
                    Row {
                        Text(
                            text = "La cuenta se encuentra bloqueda, por favor vuelve a " +
                                    "intentarlo cuando tengas conexion a internet",
                            color = com.example.cypher_vault.view.gallery.firstColor,
                            style = textStyleTittle2,
                            textAlign = TextAlign.Center,
                            onTextLayout = { /* No se necesita hacer nada aquí */ },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }

            }
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

fun comprobarCodigo(
    mailCode: String,
    value1: String
): Boolean {
    Log.d(
        "Comprobando codigo", "COMPROBACION : $mailCode = $value1)")
        return mailCode == value1
}
