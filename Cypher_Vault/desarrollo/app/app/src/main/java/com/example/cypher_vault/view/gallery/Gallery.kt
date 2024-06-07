package com.example.cypher_vault.view.gallery

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.cypher_vault.R
import com.example.cypher_vault.controller.navigation.NavController
import com.example.cypher_vault.controller.data.DatabaseController
import com.example.cypher_vault.controller.gallery.GalleryController
import com.example.cypher_vault.controller.messages.MessageController
import com.example.cypher_vault.controller.messages.getfullpasswordfield
import com.example.cypher_vault.controller.messages.getincorrectPassword
import com.example.cypher_vault.controller.messages.getvalidateAlphabeticCharacter
import com.example.cypher_vault.controller.messages.getvalidatePasswordCharacters
import com.example.cypher_vault.controller.messages.getvalidatePasswordLength
import com.example.cypher_vault.controller.messages.getvalidatePasswordLengthMax
import com.example.cypher_vault.controller.messages.getvalidatePasswordNotContainNumber
import com.example.cypher_vault.controller.messages.getvalidatePasswordNotContainUserName
import com.example.cypher_vault.controller.messages.getvalidatePasswordSpecialCharacters
import com.example.cypher_vault.controller.messages.getvalidatePasswordsSecialcharacters
import com.example.cypher_vault.controller.premium.PremiumController
import com.example.cypher_vault.database.User
import com.example.cypher_vault.database.UserIncome
import com.example.cypher_vault.model.premium.PremiumManager
import com.example.cypher_vault.model.session.SessionState
import com.example.cypher_vault.view.registration.LimitedTextBox
import com.example.cypher_vault.view.registration.findAncestorActivity
import com.example.cypher_vault.view.resources.redColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream


//Variables de entorno/////////////////////////////
val pixelesDeRedimensionamiento = 1f
val maximoImagenesPremium = 40 //860
val maximoImagenesModoPobre = 10 //42
val MAX_IMAGE_SELECTION = 30
var isPremium: Boolean? = false
var userPremiumSince = ""

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


@SuppressLint("CoroutineCreationDuringComposition")
@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Gallery(navController: NavController, userId: String, galleryController: GalleryController) {


//    //-----"CODIGO PARA QUE SE VEA EN NEGRO LA GALERIA SI QUIERE SACAR FOTOCAPTURA-----//
//    val block = LocalContext.current
//    // Usar DisposableEffect para configurar y limpiar la bandera FLAG_SECURE
//    DisposableEffect(Unit) {
//        // Configurar la bandera FLAG_SECURE
//        val activity = block as? Activity
//        activity?.window?.setFlags(
//            WindowManager.LayoutParams.FLAG_SECURE,
//            WindowManager.LayoutParams.FLAG_SECURE
//        )
//        // Limpiar la bandera FLAG_SECURE cuando el Composable se desecha
//        onDispose {
//            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
//        }
//    }
    //----------------------------------------------------------------------------------//

    var dbc = DatabaseController()

    // variable para selecionar las imagenes para eliminar
    val selectedImageIds = remember { mutableStateOf<List<Long>>(listOf()) }
    var longClickPerformed by remember { mutableStateOf(false) }
    val selectedImages = remember { mutableStateMapOf<Long, Boolean>() }



    //Variables para el mensaje de gallery
    var currentMessage by remember { mutableStateOf("") }
    val messageController = MessageController()
    var  MessageModeStatus by remember { mutableStateOf(false) }


    //Variable de la 2 verificacion
    var checkedSecondAuth : Boolean? by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = Unit) { // Key can be anything to trigger on recomposition
        val usuarioTemp = dbc.getUserById(userId)
        if (usuarioTemp != null) {
            checkedSecondAuth = usuarioTemp.authentication
        }
    }

    //Variables de cerrar sesion
    var alertCloseSession by remember { mutableStateOf(false) }
    val sessionState = remember { SessionState() }
    if (!sessionState.isLoggedIn) {
        // Show a message or navigate to the login screen
    }

    // Variables eliminar usuario
    val sheetDeleteState = rememberModalBottomSheetState()
    var showDeletePanel by remember { mutableStateOf(false) }
    val deletePasswordState = remember { mutableStateOf(TextFieldValue()) }
    var showError by remember { mutableStateOf(false) }

    // Variables cambio de contraseña
    val sheetPasswordState = rememberModalBottomSheetState()
    var showPasswordPanel by remember { mutableStateOf(false) }
    var nameState by remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf(TextFieldValue()) }
    val actualPasswordState = remember { mutableStateOf(TextFieldValue()) }
    val passwordVisible = remember { mutableStateOf(false) }
    var isContentVisiblpasswordState by remember { mutableStateOf(false) }

    //Variables de usuario Premium/Panel
    val premiumManager = PremiumManager()
    val premiumController = PremiumController(premiumManager)
    var usuarioPremium = premiumController.getPremiumUser(userId)
    isPremium = if (usuarioPremium != null) {
        usuarioPremium.premium_account
    } else {
        false
    }
    userPremiumSince = if (usuarioPremium != null) {
        premiumController.formatIncomeDate(usuarioPremium.active_subscription)
    } else {
        "no disponible"
    }
    val sheetState = rememberModalBottomSheetState()
    var showPremiumPanel by remember { mutableStateOf(false) }

    //Variables necesarias/////////////////////////
    val context = LocalContext.current
    val activity = context.findAncestorActivity()

    //Carga datos para el perfil y para el socalo de nombre/////////////////////
    var usuario by remember { mutableStateOf<User?>(null) }
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    LaunchedEffect(key1 = Unit) { // Key can be anything to trigger on recomposition
        val usuarioTemp = dbc.getUserById(userId)
        usuario = usuarioTemp
        nombre = usuarioTemp?.firstName.toString()
        email = usuarioTemp?.email.toString()
        contrasena = usuarioTemp?.password.toString()
    }
    var userImage by remember { mutableStateOf<ByteArray?>(null) }
    LaunchedEffect(key1 = userId) {
        withContext(Dispatchers.IO) {
            val user = dbc.getUserById(userId) // Nueva función para obtener el usuario por ID
            userImage = user?.profile_picture // Asigna la imagen de perfil del usuario
        }
    }


    //Acceso a la galeria/imagenes del celular///////////////////////
    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            activity!!,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            200
        )
    }

    //Carga de imagenes del usuario en la galeria//////////////////////
    LaunchedEffect(key1 = userId) {
        galleryController.loadImagesForUser(userId)
    }
    val images = galleryController.getGalleryImages()
    var indeximg by remember { mutableStateOf(images.size) }
    val imageUris = remember { mutableStateOf<List<Uri>>(listOf()) }
    indeximg = images.size
    LaunchedEffect(key1 = imageUris.value) {
        galleryController.loadImagesForUser(userId)
    }


    //Seleccion de imagenes de la galeria del celular y almacenamiento////////////////
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri>? ->
        val maxSelectionLimit = if (isPremium == true) maximoImagenesPremium else maximoImagenesModoPobre
        indeximg = images.size

        val limitedUris = uris?.take(MAX_IMAGE_SELECTION) ?: emptyList()

        limitedUris.forEach { uri ->
            if (indeximg < maxSelectionLimit) {
                uri?.let {
                    val inputStream = context.contentResolver.openInputStream(it)
                    val bitmapOriginal = BitmapFactory.decodeStream(inputStream)
                    // Test Imagen Original
                    // galleryController.saveBitmapToFile(context, bitmapOriginal, "original_image.png")
                    val bitmapResize = galleryController.reduceImageSize(
                        bitmapOriginal.asImageBitmap(),
                        pixelesDeRedimensionamiento
                    )
                    // Test Imagen Redim
                    // galleryController.saveBitmapToFile(context, bitmapResize.asAndroidBitmap(), "resized_image.png")
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    bitmapResize.asAndroidBitmap()
                        .compress(Bitmap.CompressFormat.PNG, 60, byteArrayOutputStream)
                    val compressedImageData = byteArrayOutputStream.toByteArray()
                    // Test Imagen bajo compresion
                    // galleryController.saveByteArrayToFile(context, compressedImageData, "compressed_image.png")

                    galleryController.saveImage(compressedImageData, userId)
                    imageUris.value += it
                    indeximg++
                    Log.e("proceso", "lo que vale ${indeximg},${images.size}")
                }
            }
        }
    }
    val selectedImageBitmap = remember { mutableStateOf<Bitmap?>(null) }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    // Variables de Ingresos de usuario
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var listaDeIngresos by remember { mutableStateOf<List<UserIncome>>(emptyList()) }
    var showIncomes by remember { mutableStateOf(false) }
    LaunchedEffect(userId) {
        // Cargar todos los ingresos del usuario//////////////////////////
        listaDeIngresos = galleryController.loadFiveIncomes(userId).value
    }
    Log.e("galeria", "LISTA DE INGRESOS : $listaDeIngresos")


    val launcherProfile = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri>? ->
        val maxSelectionLimit = if (isPremium == true) maximoImagenesPremium else maximoImagenesModoPobre
        indeximg = images.size

        // Only process the first 10 (or the max selection limit) images if more than allowed are selected
        val limitedUris = uris?.take(MAX_IMAGE_SELECTION) ?: emptyList()

        limitedUris.forEach { uri ->
            if (indeximg < maxSelectionLimit) {
                uri?.let {
                    val inputStream = context.contentResolver.openInputStream(it)
                    val bitmapOriginal = BitmapFactory.decodeStream(inputStream)
                    val bitmapResize = galleryController.reduceImageSize(
                        bitmapOriginal.asImageBitmap(),
                        1.0f // Ajusta este valor según sea necesario
                    )
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    bitmapResize.asAndroidBitmap()
                        .compress(Bitmap.CompressFormat.PNG, 60, byteArrayOutputStream)
                    val compressedImageData = byteArrayOutputStream.toByteArray()

                    scope.launch {
                        withContext(Dispatchers.IO) {
                            galleryController.updateProfileImage(userId, compressedImageData)
                        }
                        // Actualiza la imagen en la UI
                        userImage = compressedImageData
                    }
                }
                indeximg++
            }
        }
    }

    val onImageClick = {
        launcherProfile.launch("image/*")
    }

    //var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val showDialog = remember { mutableStateOf(false) }
    val qrCodeBitmap = remember { mutableStateOf<Bitmap?>(null) }
    val imageUrlText = remember { mutableStateOf<String?>(null) }

    val onShareButtonClick: () -> Unit = {
        Log.d("GalleryScreen", "Apretando")
        scope.launch {
            selectedImageBitmap.value?.let { bitmap ->
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                val imageData = byteArrayOutputStream.toByteArray()

                Log.d("GalleryScreen", "Sending image to server...")
                val imageUrl = galleryController.sendImageToServer(imageData)
                imageUrlText.value = imageUrl

                if (imageUrl != null) {
                    Log.d("GalleryScreen", "Image uploaded successfully. Generating QR code...")
                    // Generar el código QR con la URL de la imagen en el servidor
                    val qrBitmap = galleryController.generateQRCode(imageUrl)
                    qrCodeBitmap.value = qrBitmap

                    // Mostrar el QR en un diálogo
                    showDialog.value = true
                } else {
                    // Manejar error en el envío
                    Log.e("GalleryScreen", "Error uploading image")
                }
            }
        }
    }



    //Panel del usuario//////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = thirdColor,
                modifier = Modifier
                    .background(thirdColor)
                    .fillMaxHeight(),
                content = {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Información de usuario en el panel
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.Absolute.Center
                                ) {
                                    Text(
                                        text = "Panel de ",
                                        color = mainBackgroundColor,
                                        style = textStyleTittle2
                                    )
                                    Text(
                                        text = galleryController.capitalizarPrimeraLetra(nombre),
                                        color = firstColor,
                                        style = textStyleTittle2
                                    )
                                    Text(
                                        text = galleryController.procesarString(nombre),
                                        color = mainBackgroundColor,
                                        style = textStyleTittle2
                                    )
                                }
                                Spacer(modifier = Modifier.height(20.dp))
                                Row(
                                    horizontalArrangement = Arrangement.Absolute.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .height(100.dp)
                                            .fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularImage(
                                            byteArray = userImage,
                                            onClick = { onImageClick() }
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = email,
                            color = mainBackgroundColor,
                            style = textStyleTittle2
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        // Habilitar segunda verificacion ///////////////////////////////////////////////////////////////
                        Button(
                            onClick = {
                                checkedSecondAuth = !checkedSecondAuth!!
                                galleryController.saveSecondAuth(userId,
                                    checkedSecondAuth!!
                                )
                                Log.d("galeria", "checkedSecondAuth: $checkedSecondAuth")
                            },
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(3.dp, firstColor),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = firstColor
                            ),
                            modifier = Modifier
                                .width(290.dp)
                                .padding(top = 15.dp)
                        ) {
                            // Center the elements using a Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "2da Verificacion")

                                checkedSecondAuth?.let {
                                    Switch(
                                        colors = SwitchDefaults.colors(checkedThumbColor = thirdColor,checkedIconColor = thirdColor, checkedTrackColor= firstColor),
                                        checked = it,
                                        onCheckedChange = {
                                            checkedSecondAuth = it
                                            Log.d("galeria", "checkedSecondAuth: $checkedSecondAuth")
                                            galleryController.saveSecondAuth(userId,
                                                checkedSecondAuth!!)
                                        },
                                        thumbContent = if (checkedSecondAuth == true) {
                                            {
                                                Icon(
                                                    imageVector = Icons.Filled.Check,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(SwitchDefaults.IconSize),
                                                    tint = firstColor
                                                )
                                            }
                                        } else {
                                            null
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        // Boton para cambiar contraseña ////////////////////////////////////////////////////////////////////
                        Button(
                            onClick = {
                                nameState = nombre
                                showPasswordPanel = true },
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(3.dp, firstColor),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = firstColor
                            ),
                            modifier = Modifier
                                .width(290.dp)
                                .padding(top = 15.dp)
                        )
                        {
                            Text(text = "Cambiar Contraseña")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        // Boton para ver ingresos ////////////////////////////////////////////////////////////////////////
                        Button(
                            onClick = { showIncomes = !showIncomes },
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(3.dp, firstColor),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = firstColor
                            ),
                            modifier = Modifier
                                .width(290.dp)
                                .padding(top = 15.dp)
                        )
                        {
                            Text(text = "Tus ingresos en la App")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        // Mostrar la lista de ingresos si showIncomes es true/////////////////
                        if (showIncomes) {
                            IncomeList(galleryController, incomes = listaDeIngresos)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        // Boton para ver premium ///////////////////////////////////////////////////////////////////////////
                        Button(
                            onClick = { showPremiumPanel = true },
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(3.dp, firstColor),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = firstColor
                            ),
                            modifier = Modifier
                                .width(290.dp)
                                .padding(top = 15.dp)
                        )
                        {
                            Text(text = "Premium")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        // Boton para cerra la sesion ////////////////////////////////////////////////////////////////////
                        Button(
                            onClick = {
                                alertCloseSession = true
                            },
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(3.dp, firstColor),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = firstColor
                            ),
                            modifier = Modifier
                                .width(290.dp)
                                .padding(top = 15.dp)
                        )
                        {
                            Text(text = "Cerrar sesion")
                        }
                        // Boton para eliminar cuenta ///////////////////////////////////////////////////////////////////////////
                        Spacer(modifier = Modifier.weight(1f)) // Push the button to the bottom
                        Button(
                            onClick = {
                                showDeletePanel = true
                                deletePasswordState.value = TextFieldValue("")

                                      },
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(3.dp, firstColor),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = firstColor
                            ),
                            modifier = Modifier
                                .width(290.dp)
                                .padding(top = 15.dp)
                        ) {
                            Text(text = "Eliminar Cuenta")
                        }
                        Spacer(modifier = Modifier.height(50.dp))
                    }
                }
            )
        },
    ) {
        //Pantalla principal de la galeria///////////////////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        Scaffold(

            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),


            ///BARRA SUPERIOR ////////////////////////////////////////////////////////////////////////////////////////////////
            topBar = {

                CenterAlignedTopAppBar(
                    modifier = Modifier,

                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = thirdColor,
                        titleContentColor = thirdColor,
                    ),
                    title = {

                        Column(
                            modifier = Modifier
                                .clickable(
                                    onClick = {
                                        longClickPerformed = false // Restablecer el estado de longClickPerformed
                                        selectedImageIds.value = emptyList()
                                        selectedImages.keys.forEach { key ->
                                            selectedImages[key] = false
                                        }
                                    },
                                    indication = null,  // Desactivar la indicación visual del clic
                                    interactionSource = remember { MutableInteractionSource() }
                                ),

                            horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(horizontalArrangement = Arrangement.Absolute.Center) {
                                Text(
                                    text = "C",
                                    color = firstColor,
                                    style = textStyleTittle,
                                    onTextLayout = { /* No se necesita hacer nada aquí */ }
                                )
                                Text(
                                    text = "ypher ",
                                    color = mainBackgroundColor,
                                    style = textStyleTittle,
                                    onTextLayout = { /* No se necesita hacer nada aquí */ }
                                )
                                Text(
                                    text = "V",
                                    color = firstColor,
                                    style = textStyleTittle,
                                    onTextLayout = { /* No se necesita hacer nada aquí */ }
                                )
                                Text(
                                    text = "ault",
                                    color = mainBackgroundColor,
                                    style = textStyleTittle,
                                    onTextLayout = { /* No se necesita hacer nada aquí */ }
                                )

                            }
                            Row(horizontalArrangement = Arrangement.Absolute.Center) {
                                Text(
                                    text = "G",
                                    color = firstColor,
                                    style = textStyleTittle2,
                                    onTextLayout = { /* No se necesita hacer nada aquí */ }
                                )
                                Text(
                                    text = "aleria de ",
                                    color = mainBackgroundColor,
                                    style = textStyleTittle2,
                                    onTextLayout = { /* No se necesita hacer nada aquí */ }
                                )
                                Text(
                                    text = galleryController.capitalizarPrimeraLetra(nombre),
                                    color = firstColor,
                                    style = textStyleTittle2,
                                    onTextLayout = { /* No se necesita hacer nada aquí */ }
                                )
                                Text(
                                    text = galleryController.procesarString(nombre),
                                    color = mainBackgroundColor,
                                    style = textStyleTittle2,
                                    onTextLayout = { /* No se necesita hacer nada aquí */ }
                                )

                            }
                        }

                    },
                    navigationIcon = {
                        IconButton(onClick = { alertCloseSession = true }) {
                            Icon(
                                modifier = Modifier.width(30.dp),
                                tint = firstColor,
                                imageVector = Icons.Filled.ArrowBackIosNew,
                                contentDescription = "Localized description"
                            )
                        }
                    },
                    actions = {
                        Spacer(modifier = Modifier.height(20.dp))
                        IconButton(onClick = { abrirPanel(scope, drawerState) }) {
                            CircularImage(byteArray = userImage,
                                modifier = Modifier
                                .height(100.dp)
                                .width(100.dp))
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
            },

            //Zocalo de mensajes que se encuentra abajo de galeria tambien el boton de eliminar y cancelar

            bottomBar = {
                BottomAppBar(
                    containerColor = thirdColor,
                    contentColor = firstColor, // Mantener estos colores
                ) {

                    Box(
                        modifier = Modifier
                            .clickable(
                                onClick = {
                                    longClickPerformed = false // Restablecer el estado de longClickPerformed
                                    selectedImageIds.value = emptyList()
                                    selectedImages.keys.forEach { key ->
                                        selectedImages[key] = false
                                    }
                                },
                                indication = null,  // Desactivar la indicación visual del clic
                                interactionSource = remember { MutableInteractionSource() }
                            )
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp)) // Bordes redondeados
                            .background(Color.White) // Fondo blanco para el Box
                            .border(2.dp, thirdColor, RoundedCornerShape(16.dp)) // Borde con color y forma redondeada
                    ) {
                        if (longClickPerformed) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = {
                                        scope.launch {
                                            withContext(Dispatchers.IO) {
                                                galleryController.deleteImg(userId, selectedImageIds)
                                            }
                                        }
                                        indeximg=images.size
                                        longClickPerformed = false
                                        Log.e("proceso","lo que vale el indeximg luego de eliminar $indeximg")
                                    },
                                    shape = RoundedCornerShape(4.dp),
                                    border = BorderStroke(3.dp,firstColor),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = thirdColor,
                                        contentColor = wingWhite,
                                    ),
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                ) {
                                    Text(
                                        text = "Eliminar",
                                        color = mainBackgroundColor,
                                        style = textStyleTittle2,
                                        onTextLayout = { /* No se necesita hacer nada aquí */ }
                                    )
                                }
                                Button(
                                    onClick = {
                                        // Lógica para cancelar
                                        longClickPerformed = false // Restablecer el estado de longClickPerformed
                                        selectedImageIds.value = emptyList()
                                        selectedImages.keys.forEach { key ->
                                            selectedImages[key] = false
                                        }
                                    },
                                    shape = RoundedCornerShape(4.dp),
                                    border = BorderStroke(3.dp,firstColor),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = thirdColor,
                                        contentColor = wingWhite,
                                    ),
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                ) {
                                    Text(
                                        text = "Cancelar",
                                        color = mainBackgroundColor,
                                        style = textStyleTittle2,
                                        onTextLayout = { /* No se necesita hacer nada aquí */ }
                                    )
                                }
                            }
                        }
                        else {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                val icon = if (MessageModeStatus) {
                                    R.drawable.icoerror
                                } else {
                                    R.drawable.iconclarificatio
                                }
                                painterResource(id = icon)?.let {
                                    Image(
                                        painter = it,
                                        contentDescription = "",
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Text(
                                    text = currentMessage,
                                    fontFamily = com.example.cypher_vault.view.resources.fontFamily,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(end = 8.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            },
            //Agregar imagen a la galeria de imagenes//////////////////////////////////////////////////////////////////
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    if (isPremium == true) {
                        if(images.size < maximoImagenesPremium) {
                            launcher.launch("image/*")
                        } else {
                            MessageModeStatus=true
                            currentMessage=messageController.getmessageLimitModePremium()

                        }
                    } else {
                        if (images.size < maximoImagenesModoPobre) {
                            launcher.launch("image/*")
                        } else {
                            MessageModeStatus=true
                            currentMessage=messageController.getmessageLimitModePrueba()
                        }
                    }
                }) {
                    Icon(
                        modifier = Modifier.width(30.dp),
                        tint = firstColor,
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add"
                    )
                }
            },
            //Contenido de la galeria del usuario///////////////////////////////////////////////////////////////////////////
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            content = { innerPadding ->
                Column(
                    modifier = Modifier
                        .clickable(
                            onClick = {
                                longClickPerformed = false // Restablecer el estado de longClickPerformed
                                selectedImageIds.value = emptyList()
                                selectedImages.keys.forEach { key ->
                                    selectedImages[key] = false
                                }
                            },
                            indication = null,  // Desactivar la indicación visual del clic
                            interactionSource = remember { MutableInteractionSource() }
                        )
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(images.size) { index ->
                            val image = images[index]
                            val isSelected = selectedImages[image.id] ?: false
                            val bitmap =
                                BitmapFactory.decodeByteArray(
                                    image.imageData,
                                    0,
                                    image.imageData.size
                                )
                            bitmap?.let {
                                Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(100.dp)
                                        .padding(4.dp)
                                        .border(2.dp, Color.Black)
                                        .combinedClickable(
                                            onClick = {
                                                //para ver las imagenes (zoom)
                                                if (!longClickPerformed) {
                                                    selectedImageBitmap.value =
                                                        BitmapFactory.decodeByteArray(
                                                            image.imageData,
                                                            0,
                                                            image.imageData.size
                                                        )
                                                }
                                                //El caso que el usuario quiera borrar las imagenes
                                                else {
                                                    if (!selectedImageIds.value.contains(image.id)) {
                                                        selectedImages[image.id] = !isSelected
                                                        selectedImageIds.value =
                                                            selectedImageIds.value + image.id
                                                    } else if (selectedImageIds.value.contains(image.id)) {
                                                        selectedImages[image.id] = !isSelected
                                                        val updatedList =
                                                            selectedImageIds.value.filter { it != image.id }
                                                        selectedImageIds.value = updatedList

                                                    }
                                                }
                                                // Restablece el indicador de clic largo

                                            },
                                            onLongClick = {
                                                selectedImageIds.value =
                                                    selectedImageIds.value + image.id
                                                longClickPerformed = true
                                                selectedImages[image.id] = !isSelected
                                            }
                                        )
                                )
                                if (isSelected && longClickPerformed) {
                                    Checkbox(
                                        checked = true,
                                        onCheckedChange = null, // No interaction here
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = Color.Cyan, // El color del checkbox cuando está marcado
                                            checkmarkColor = Color.White // El color del checkmark dentro del checkbox
                                        ),
                                        modifier = Modifier
                                            .padding(4.dp)
                                    )
                                }
                            }
                        }
                    }
                    if (!MessageModeStatus) {
                        LaunchedEffect(Unit) {
                            val messageChannel = messageController.getMessageChannel()
                            for (message in messageChannel) {
                                currentMessage = message
                            }
                        }
                        } else {
                        CoroutineScope(Dispatchers.Default).launch {
                            delay(5000) // Espera 5 segundos reales
                            MessageModeStatus = false // Establece MessageModeStatus a false después de 5 segundos
                        }
                            }

                }

                // Mostrar la imagen seleccionada en un diálogo/////////////////////////////////////////////////////////////////
                if (selectedImageBitmap.value != null) {
                    Dialog(onDismissRequest = { selectedImageBitmap.value = null }) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Image(
                                bitmap = selectedImageBitmap.value!!.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Button(
                                onClick = onShareButtonClick,
                                shape = RoundedCornerShape(4.dp),
                                border = BorderStroke(3.dp, firstColor),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = thirdColor,
                                    contentColor = wingWhite,
                                ),
                                modifier = Modifier
                                    .padding(8.dp) // Agrega un poco de espacio alrededor del botón
                            ) {
                                Text(
                                    text = "Compartir",
                                    color = mainBackgroundColor,
                                    style = textStyleTittle2,
                                    onTextLayout = { /* No se necesita hacer nada aquí */ }
                                )
                            }
                        }
                    }
                }
                if (showDialog.value) {
                    Dialog(onDismissRequest = { showDialog.value = false }) {
                        qrCodeBitmap.value?.let {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = "QR Code",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp)
                                )
                                ClickableText(
                                    text = AnnotatedString("Enlace: ${imageUrlText.value}"),
                                    style = TextStyle(color = Color.Red),
                                    onClick = { offset ->
                                        // Aquí abres el enlace en el navegador del dispositivo
                                        val uri = Uri.parse(imageUrlText.value) // Suponiendo que imageUrlText contiene el enlace generado
                                        val intent = Intent(Intent.ACTION_VIEW, uri)
                                        context.startActivity(intent)
                                    }
                                )
                            }
                        }
                    }
                }
                // Mostrar el panel de premium ////////////////////////////////////////////////////////////////////////////////////
                //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                if (showPremiumPanel) {
                    ModalBottomSheet(
                        containerColor = premiumBackgroundColor,
                        onDismissRequest = {
                            showPremiumPanel = false
                        },
                        sheetState = sheetState
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            if (isPremium == true) {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.cyphervaulpremium),
                                        contentDescription = "",
                                        alignment = Alignment.TopCenter,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                ) {
                                    Text(
                                        text = "Premium User Since: ${userPremiumSince}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.White,
                                        modifier = Modifier.padding(bottom = 70.dp)
                                    )
                                }
                            } else {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.cyphervaultcomprapremium2),
                                        contentDescription = "Upgrade to premium",
                                        alignment = Alignment.TopCenter,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                ) {
                                    Button(
                                        onClick = {
                                            premiumController.buyPremium(userId)
                                            isPremium =
                                                premiumController.getPremiumUser(userId)?.premium_account
                                            val userPremiumSinceDate =
                                                premiumController.getPremiumUser(userId)?.active_subscription
                                            userPremiumSince = premiumController.formatIncomeDate(
                                                userPremiumSinceDate
                                            )
                                            showPremiumPanel = false
                                            navController.navigateToListLogin()
                                        },
                                        modifier = Modifier.padding(top = 16.dp, bottom = 70.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = premiumButtonColor,
                                            contentColor = premiumButtonTextColor,
                                        ),
                                    ) {
                                        Text(text = "Comprar con 1 toque")
                                    }
                                }
                            }
                        }
                    }
                }
                // Mostrar el panel de contraseña ////////////////////////////////////////////////////////////////////////////////////
                //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                if (showPasswordPanel) {
                    ModalBottomSheet(
                        containerColor = premiumBackgroundColor,
                        onDismissRequest = {
                            showPasswordPanel = false
                            actualPasswordState.value = TextFieldValue("")
                            passwordState.value = TextFieldValue("")
                        },
                        sheetState = sheetPasswordState
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                //// INGRESO PASSWORD ACTUAL /////////////////////////////////////////////////////////
                                Row(
                                    modifier = Modifier
                                        .width(290.dp)
                                        .padding(top = 15.dp)
                                        .border(
                                            BorderStroke(
                                                3.dp,
                                                com.example.cypher_vault.view.resources.firstColor
                                            ),
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                ) {
                                    TextField(
                                        value = actualPasswordState.value,
                                        onValueChange = {

                                            actualPasswordState.value = it

                                        },
                                        textStyle = TextStyle(
                                            color = if(!getvalidatePasswordLengthMax(actualPasswordState.value.text) || getvalidatePasswordSpecialCharacters(actualPasswordState.value.text) || getvalidatePasswordNotContainUserName(actualPasswordState.value.text,nameState) ) redColor else com.example.cypher_vault.view.resources.firstColor,
                                            fontSize = 13.sp,
                                            fontFamily = com.example.cypher_vault.view.resources.fontFamily,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        label = {
                                            Text(
                                                "Contraseña Actual",
                                                fontSize = 15.sp,
                                                fontFamily = com.example.cypher_vault.view.resources.fontFamily,
                                                color = com.example.cypher_vault.view.resources.thirdColor,
                                                fontWeight = FontWeight.Bold
                                            )
                                        },
                                        placeholder = {
                                            Text(
                                                "",
                                                style = TextStyle(
                                                    color = Color.Gray,
                                                    fontSize = 13.sp,
                                                    fontFamily = com.example.cypher_vault.view.resources.fontFamily
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
                                            cursorColor = com.example.cypher_vault.view.resources.thirdColor,
                                            focusedIndicatorColor = com.example.cypher_vault.view.resources.firstColor,
                                            unfocusedIndicatorColor = com.example.cypher_vault.view.resources.firstColor,
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                //// INGRESO DE NUEVO PASSWORD /////////////////////////////////////////////////////////
                                Row(
                                    modifier = Modifier
                                        .width(290.dp)
                                        .padding(top = 15.dp)
                                        .border(
                                            BorderStroke(
                                                3.dp,
                                                com.example.cypher_vault.view.resources.firstColor
                                            ),
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                ) {
                                    TextField(
                                        value = passwordState.value,
                                        onValueChange = {

                                            passwordState.value = it

                                        },
                                        textStyle = TextStyle(
                                            color = if(!getvalidatePasswordLengthMax(passwordState.value.text) || getvalidatePasswordSpecialCharacters(passwordState.value.text) || getvalidatePasswordNotContainUserName(passwordState.value.text,nameState) ) redColor else com.example.cypher_vault.view.resources.firstColor,
                                            fontSize = 13.sp,
                                            fontFamily = com.example.cypher_vault.view.resources.fontFamily,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        label = {
                                            Text(
                                                "Nueva Contraseña",
                                                fontSize = 15.sp,
                                                fontFamily = com.example.cypher_vault.view.resources.fontFamily,
                                                color = com.example.cypher_vault.view.resources.thirdColor,
                                                fontWeight = FontWeight.Bold
                                            )
                                        },
                                        placeholder = {
                                            Text(
                                                "15 caracteres alfanuméricos y 1 carácter especial.",
                                                style = TextStyle(
                                                    color = Color.Gray,
                                                    fontSize = 13.sp,
                                                    fontFamily = com.example.cypher_vault.view.resources.fontFamily
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
                                            cursorColor = com.example.cypher_vault.view.resources.thirdColor,
                                            focusedIndicatorColor = com.example.cypher_vault.view.resources.firstColor,
                                            unfocusedIndicatorColor = com.example.cypher_vault.view.resources.firstColor,
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .onFocusChanged { focusState ->
                                                if (focusState.isFocused) {
                                                    isContentVisiblpasswordState = true
                                                } else {
                                                    isContentVisiblpasswordState = false
                                                }
                                            }
                                    )

                                }
                            }
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                if (isContentVisiblpasswordState) {
                                    if (getfullpasswordfield(passwordState.value.text, nameState) != "") {
                                        Column(
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                (if (!getvalidatePasswordCharacters(passwordState.value.text)|| !getvalidatePasswordsSecialcharacters(passwordState.value.text)
                                                    ||!getvalidatePasswordLength(passwordState.value.text)
                                                    || !getvalidatePasswordLengthMax(passwordState.value.text) || !getvalidatePasswordNotContainNumber(passwordState.value.text)
                                                    || !getvalidateAlphabeticCharacter(passwordState.value.text)){
                                                    R.drawable.iconwarning
                                                } else if (getvalidatePasswordNotContainUserName(passwordState.value.text,nombre)
                                                    || getvalidatePasswordSpecialCharacters(passwordState.value.text)) {
                                                    R.drawable.icoerror
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
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    if (getfullpasswordfield(passwordState.value.text, nombre) != "null") {
                                                        LimitedTextBox(
                                                            text = getfullpasswordfield(passwordState.value.text, nombre),
                                                            maxWidth = 250.dp // Ajusta este valor según tus necesidades
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            ///// BOTON DE ACTUALIZAR CONTRASEÑA ////////////////////////////////////////////
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Button(
                                    onClick = {
                                        if (contrasena!= actualPasswordState.value.text){
                                            Toast.makeText(
                                                context,
                                                "La contraseña actual es incorrecta",Toast.LENGTH_SHORT).show()
                                        }else if(passwordState.value.text == actualPasswordState.value.text){
                                            Toast.makeText(
                                                context,
                                                "La contraseña actual es igual a la nueva",Toast.LENGTH_SHORT).show()
                                        }

                                        else{
                                            galleryController.changePassword(userId, passwordState.value.text)
                                            showPasswordPanel = false
                                            contrasena = passwordState.value.text
                                            passwordState.value = TextFieldValue("")
                                            actualPasswordState.value = TextFieldValue("")
                                            Toast.makeText(
                                                context,
                                                "Contraseña actualizada",Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.padding(top = 16.dp, bottom = 100.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = thirdColor,
                                        contentColor = wingWhite,
                                    ),
                                ) {
                                    Text(text = "Cambiar Contraseña")
                                }
                            }
                        }
                    }
                }
                /// Alerta de cerrar sesion /////////////////////////////////////////////////////////////
                ///////////////////////////////////////////////////////////////////////////////////////////
                if (alertCloseSession) {
                    AlertDialog(
                        onDismissRequest = { /* Do nothing */ },
                        title = { Text(text = "Confirmar salida") },
                        text = { Text(text = "¿Estás seguro de que quieres salir?") },
                        confirmButton = {
                            TextButton(onClick = { galleryController.closeSession(context,navController) }) {
                                Text(text = "Salir")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                alertCloseSession = false
                            }) {
                                Text(text = "Cancelar")
                            }
                        }
                    )
                }
                // Mostrar el panel de eliminar cuenta ////////////////////////////////////////////////////////////////////////////////////
                //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                if (showDeletePanel) {
                    ModalBottomSheet(
                        containerColor = premiumBackgroundColor,
                        onDismissRequest = {
                            showDeletePanel = false
                            deletePasswordState.value = TextFieldValue("")
                            showError = false
                        },
                        sheetState = sheetDeleteState
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                //// INGRESO DE PASSWORD /////////////////////////////////////////////////////////
                                Row(
                                    modifier = Modifier
                                        .width(290.dp)
                                        .padding(top = 15.dp)
                                        .border(
                                            BorderStroke(
                                                3.dp,
                                                com.example.cypher_vault.view.resources.firstColor
                                            ),
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                ) {
                                    TextField(
                                        value = deletePasswordState.value,
                                        onValueChange = {

                                            deletePasswordState.value = it
                                            showError = false

                                        },
                                        textStyle = TextStyle(
                                            color = if(!getvalidatePasswordLengthMax(deletePasswordState.value.text) || getvalidatePasswordSpecialCharacters(deletePasswordState.value.text) || getvalidatePasswordNotContainUserName(deletePasswordState.value.text,nameState) ) redColor else com.example.cypher_vault.view.resources.firstColor,
                                            fontSize = 13.sp,
                                            fontFamily = com.example.cypher_vault.view.resources.fontFamily,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        label = {
                                            Text(
                                                "Contraseña actual",
                                                fontSize = 15.sp,
                                                fontFamily = com.example.cypher_vault.view.resources.fontFamily,
                                                color = com.example.cypher_vault.view.resources.thirdColor,
                                                fontWeight = FontWeight.Bold
                                            )
                                        },
                                        placeholder = {
                                            Text(
                                                "",
                                                style = TextStyle(
                                                    color = Color.Gray,
                                                    fontSize = 13.sp,
                                                    fontFamily = com.example.cypher_vault.view.resources.fontFamily
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
                                            cursorColor = com.example.cypher_vault.view.resources.thirdColor,
                                            focusedIndicatorColor = com.example.cypher_vault.view.resources.firstColor,
                                            unfocusedIndicatorColor = com.example.cypher_vault.view.resources.firstColor,
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                if (showError) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.icoerror),
                                            contentDescription = "",
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp)) // Espacio entre la imagen y el texto
                                        com.example.cypher_vault.view.login.LimitedTextBox(
                                            text = getincorrectPassword(),
                                            maxWidth = 250.dp // Ajusta este valor según tus necesidades
                                        )
                                    }
                                }
                            }
                            ///// BOTON DE ELIMINAR CUENTA ////////////////////////////////////////////
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Button(
                                    onClick = {
                                        if (contrasena != deletePasswordState.value.text) {
                                            showError = true
                                        } else {
                                            galleryController.deleteAccount(userId) { success ->
                                                if (success) {
                                                    deletePasswordState.value = TextFieldValue("")
                                                    Toast.makeText(
                                                        context,
                                                        "Cuenta Eliminada", Toast.LENGTH_SHORT
                                                    ).show()
                                                    showDeletePanel = false
                                                    navController.navigateToListLogin()
                                                } else {
                                                    deletePasswordState.value = TextFieldValue("")
                                                    Toast.makeText(
                                                        context,
                                                        "No se pudo eliminar la cuenta", Toast.LENGTH_SHORT
                                                    ).show()
                                                    showDeletePanel = false
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier.padding(top = 16.dp, bottom = 100.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = thirdColor,
                                        contentColor = wingWhite,
                                    ),
                                ) {
                                    Text(text = "Eliminar Cuenta")
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}



//Apertura del panel de usuario//////////////////////////////////////////////////////////////////////////////////////////
fun abrirPanel(scope: CoroutineScope, drawerState: DrawerState) {
    scope.launch {
        drawerState.apply {
            if (isClosed) open() else close()
        }
    }
}


//Lista de ingresos/////////////////////////////////////////////////////////////////////////////////////////////////////
@Composable
fun IncomeList(galleryController: GalleryController, incomes: List<UserIncome>) {
    Column {
        incomes.forEach { income ->
            Text(text = galleryController.formatIncomeDate(income.income),
                color = mainBackgroundColor,
                style = textStyleTittle2,
                onTextLayout = { /* No se necesita hacer nada aquí */ })
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

//// Zona de mensajes //////////////////////////////////////////////////////////////////////////////////////////////
@Composable
fun LimitedTextBox(text: String, maxWidth: Dp) {
    Box(
        modifier = Modifier
            .width(maxWidth)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontFamily = com.example.cypher_vault.view.resources.fontFamily,
            color = com.example.cypher_vault.view.resources.thirdColor,
            fontWeight = FontWeight.Bold
        )
    }
}

// Imagen circular ///////////////////////////////////////////////////////////////////////////////////
@Composable
fun CircularImage(byteArray: ByteArray?, onClick:(() -> Unit)? = null, modifier: Modifier = Modifier) {
    val bitmap: Bitmap? = byteArrayToBitmap(byteArray)
    Log.d("galeria", "CircularImage: $bitmap")
    if (bitmap != null) {
        Spacer(modifier = Modifier.height(8.dp))
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "User Profile Image",
            modifier = modifier
                .size(100.dp)
                .clip(CircleShape)
                .let {
                    if (onClick != null) {
                        it.clickable(onClick = onClick)
                    } else {
                        it
                    }
                }
        )
    } else {
        Icon(
            modifier = Modifier
                .fillMaxSize()
                .let {
                    if (onClick != null) {
                        it.clickable(onClick = onClick)
                    } else {
                        it
                    }
                },
            tint = firstColor,
            imageVector = Icons.Filled.Person,
            contentDescription = "Perfil de Usuario"
        )
    }
}

// Función de ByteArray a Bitmap
fun byteArrayToBitmap(byteArray: ByteArray?): Bitmap? {
    return byteArray?.let {
        BitmapFactory.decodeByteArray(it, 0, it.size)
    }
}


//------------Barra de carga circular
/*
@Composable
fun IndeterminateCircularIndicator() {
    var loading by remember { mutableStateOf(false) }

    Button(onClick = { loading = true }, enabled = !loading) {
        Text("Start loading")
    }
    Button(onClick = { loading = false }, enabled = loading) {
        Text("stop")
    }

    if (!loading) return

    CircularProgressIndicator(
        modifier = Modifier.width(64.dp),
        indicatorColor=Color.Green,
        strokeWidth = 4.dp, // Specify the strokeWidth if needed
    )
}
*/

@Composable
fun ShowQRCodeDialog(bitmap: Bitmap, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Código QR")
        },
        text = {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Código QR"
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("Cerrar")
            }
        }
    )
}

