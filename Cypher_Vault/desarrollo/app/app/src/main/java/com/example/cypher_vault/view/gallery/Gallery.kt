package com.example.cypher_vault.view.gallery

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.cypher_vault.R
import com.example.cypher_vault.controller.navigation.NavController
import com.example.cypher_vault.controller.data.DatabaseController
import com.example.cypher_vault.controller.gallery.GalleryController
import com.example.cypher_vault.database.User
import com.example.cypher_vault.database.UserIncome
import com.example.cypher_vault.view.registration.findAncestorActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

//Variables de entorno
val pixelesDeRedimensionamiento = 1f
//Colores de la ui, tipo de letra, etc.
val firstColor = Color(0xFF02a6c3)
val secondColor = Color(0xFF01243a)
val thirdColor = Color(0xFF005767)
val mainBackgroundColor = Color(0xFFdcdcdc)
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


@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Gallery(navController: NavController, userId: String, galleryController: GalleryController) {

    //Variables necesarias
    val context = LocalContext.current
    val activity = context.findAncestorActivity()
    //Carga datos para el perfil y para el socalo de nombre
    var usuario by remember { mutableStateOf<User?>(null) }
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var dbc = DatabaseController()
    LaunchedEffect(key1 = Unit) { // Key can be anything to trigger on recomposition
        val usuarioTemp = dbc.getUserById(userId)
        usuario = usuarioTemp
        nombre = usuarioTemp?.firstName.toString()
        email = usuarioTemp?.email.toString()
    }
    //Acceso a la galeria/imagenes del celular
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
    //Carga de imagenes del usuario en la galeria
    LaunchedEffect(key1 = userId) {
        galleryController.loadImagesForUser(userId)
    }
    val images = galleryController.images.value
    val imageUris = remember { mutableStateOf<List<Uri>>(listOf()) }
    LaunchedEffect(key1 = imageUris.value) {
        galleryController.loadImagesForUser(userId)
    }
    //Seleccion de imagenes de la galeria del celular y almacenamiento
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val inputStream = context.contentResolver.openInputStream(it)
                val bitmapOriginal = BitmapFactory.decodeStream(inputStream)
                //Test Imagen Original
                //galleryController.saveBitmapToFile(context, bitmapOriginal, "original_image.png")
                val bitmapResize = galleryController.reduceImageSize(bitmapOriginal.asImageBitmap(), pixelesDeRedimensionamiento)
                //Test Imagen Redim
                //galleryController.saveBitmapToFile(context, bitmapResize.asAndroidBitmap(), "resized_image.png")
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmapResize.asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 60, byteArrayOutputStream)
                val compressedImageData = byteArrayOutputStream.toByteArray()
                //Test Imagen bajo compresion
                //galleryController.saveByteArrayToFile(context, compressedImageData, "compressed_image.png")
                galleryController.saveImage(compressedImageData, userId)
                imageUris.value += it
            }
        }
    val selectedImageBitmap = remember { mutableStateOf<Bitmap?>(null) }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    //Panel del usuario
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = thirdColor,
                modifier = Modifier.background(thirdColor).fillMaxHeight(),
                content = {
                    DrawerContent(userId,galleryController,nombre,email)
                }
            )
        },
    ) {
        //Pantalla principal de la galeria
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),

            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = thirdColor,
                        titleContentColor = thirdColor,
                    ),
                    title = {
                        Column(horizontalAlignment = Alignment.Start) {
                            Row(horizontalArrangement = Arrangement.Absolute.Left) {
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
                            Row(horizontalArrangement = Arrangement.Absolute.Left) {
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
                        IconButton(onClick = { navController.navigateToListLogin() }) {
                            Icon(
                                modifier = Modifier.width(30.dp),
                                tint = firstColor,
                                imageVector = Icons.Filled.ArrowBackIosNew,
                                contentDescription = "Localized description"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { abrirPanel(scope, drawerState ) }) {
                            Icon(
                                modifier = Modifier
                                    .height(100.dp)
                                    .width(30.dp),
                                tint = firstColor,
                                imageVector = Icons.Filled.Person,
                                contentDescription = "Perfil de Usuario"
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
            },
            bottomBar = {
                BottomAppBar(
                    containerColor = thirdColor,
                    contentColor = firstColor,
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = "Area de mensaje sys?",
                        onTextLayout = { /* No se necesita hacer nada aquí */ }
                    )
                }
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { launcher.launch("image/*") }) {
                    Icon(
                        modifier = Modifier.width(30.dp),
                        tint = firstColor,
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add"
                    )
                }
            },
            //Contenido de la galeria del usuario
            content = { innerPadding ->
                Column(
                    modifier = Modifier
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
                                        .clickable {
                                            selectedImageBitmap.value =
                                                BitmapFactory.decodeByteArray(
                                                    image.imageData,
                                                    0,
                                                    image.imageData.size
                                                )
                                        }
                                )
                            }
                        }
                    }
                }
                if (selectedImageBitmap.value != null) {
                    Dialog(onDismissRequest = { selectedImageBitmap.value = null }) {
                        Image(
                            bitmap = selectedImageBitmap.value!!.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        )
    }
}

//Apertura del panel de usuario
fun abrirPanel(scope: CoroutineScope, drawerState: DrawerState) {
    scope.launch {
        drawerState.apply {
            if (isClosed) open() else close()
        }
    }
}

//Contenido del panel de usuario
@Composable
fun DrawerContent(userId: String, galleryController: GalleryController,nombre: String, email: String) {
    var listaDeIngresos by remember { mutableStateOf<List<UserIncome>>(emptyList()) }
    var showIncomes by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        // Cargar todos los ingresos del usuario
        listaDeIngresos = galleryController.loadAllIncomes(userId).value
    }
    Log.e("galeria","LISTA DE INGRESOS : $listaDeIngresos")
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
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
                Row(
                    horizontalArrangement = Arrangement.Absolute.Center
                ) {
                    Box(
                        modifier = Modifier.height(height = 100.dp).fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        IconButton(
                            onClick = { /* Acción de la imagen */ },
                            modifier = Modifier
                                .size(100.dp)
                        ) {
                            Icon(
                                modifier = Modifier.fillMaxSize(),
                                tint = firstColor,
                                imageVector = Icons.Filled.Person,
                                contentDescription = "Perfil de Usuario"
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = email,
            color = mainBackgroundColor,
            style = textStyleTittle2,
            onTextLayout = { /* No se necesita hacer nada aquí */ }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { /* Acción cambiar contraseña */ },
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(3.dp, firstColor),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = firstColor
            ),
            modifier = Modifier.width(290.dp).padding(top = 15.dp)
        )
        {
            Text(text = "Cambiar Contraseña")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { showIncomes = !showIncomes },
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(3.dp, firstColor),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = firstColor
            ),
            modifier = Modifier.width(290.dp).padding(top = 15.dp)
        )
        {
            Text(text = "Tus ingresos en la App")
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Mostrar la lista de ingresos si showIncomes es true
        if (showIncomes) {
            IncomeList(galleryController,incomes = listaDeIngresos)
        }
    }
}

//Lista de ingresos
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

