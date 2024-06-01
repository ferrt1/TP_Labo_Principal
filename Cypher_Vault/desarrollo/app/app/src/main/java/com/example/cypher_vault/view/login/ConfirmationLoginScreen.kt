package com.example.cypher_vault.view.login

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cypher_vault.R
import com.example.cypher_vault.controller.authentication.AuthenticationController
import com.example.cypher_vault.controller.data.DatabaseController
import com.example.cypher_vault.controller.income.UserAccessController
import com.example.cypher_vault.controller.navigation.NavController
import com.example.cypher_vault.controller.service.ServiceController
import com.example.cypher_vault.model.income.UserAccessManager
import com.example.cypher_vault.view.resources.fontFamily
import com.example.cypher_vault.view.resources.thirdColor
import com.example.cypher_vault.model.service.ServiceManager

private val firstColor = Color(0xFF02a6c3)

@Composable
fun ConfirmationLoginScreen(navController: NavController, userId: String) {
    val context = LocalContext.current
    val authenticationController = AuthenticationController(context)
    var dbc = DatabaseController()

    //Prototipado de la pantalla para logueo con camara
    val imagePrintRegister = remember { mutableStateOf<Bitmap?>(null) }
    val imagePrintLogin = remember { mutableStateOf<Bitmap?>(null) }
    val result = remember { mutableStateOf<Boolean?>(null) }
    var showImage by remember { mutableStateOf(false) }

    // Se agrega ingreso de usuario
    val userAccessManager = UserAccessManager()
    val userAccessController = UserAccessController(userAccessManager)

    //Variables de la 2da Authentificacion
    var isSecondAuth : Boolean? by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = Unit) { // Key can be anything to trigger on recomposition
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
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
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
                    contentDescription = "Bienvenido de nuevo",
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
                    "No son la misma persona.",
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
        Text(
            "-----------------La que va amiwi-------------------",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
        //Elementos de la 2da Authentificacion
        ImageWithLandmarks(imagePrintRegister)
        ImageWithLandmarks(imagePrintLogin)
        if(imagePrintRegister.value == null && imagePrintLogin.value == null){
            if (isSecondAuth == true) {
                Text(
                    "sin imagenes, pero con segunda activada",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
            }else{
                Text(
                    "sin imagenes, pero sin segunda activada",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }else{
            if (isSecondAuth == true) {
                Text(
                    "con imagenes, pero con segunda activada",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
            }else{
                Text(
                    "con imagenes, pero sin segunda activada",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
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
