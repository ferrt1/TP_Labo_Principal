package com.example.cypher_vault.view.gallery

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.cypher_vault.controller.authentication.AuthenticationController
import com.example.cypher_vault.controller.data.DatabaseController
import com.example.cypher_vault.controller.gallery.GalleryController
import com.example.cypher_vault.database.User
import com.example.cypher_vault.view.registration.findAncestorActivity
import java.io.ByteArrayOutputStream
import java.util.Locale

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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Gallery(authenticationController: AuthenticationController, userId: String, galleryController: GalleryController) {

    var usuario by remember { mutableStateOf<User?>(null) }
    var nombre by remember { mutableStateOf("") }
    var dbc = DatabaseController()
    LaunchedEffect(key1 = Unit) { // Key can be anything to trigger on recomposition
        val usuarioTemp = dbc.getUserById(userId)
        usuario = usuarioTemp
        nombre = usuarioTemp?.firstName.toString()
    }


    val context = LocalContext.current
    val activity = context.findAncestorActivity()

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

    LaunchedEffect(key1 = userId) {
        galleryController.loadImagesForUser(userId)
    }


    val images = galleryController.images.value


    val imageUris = remember { mutableStateOf<List<Uri>>(listOf()) }

    LaunchedEffect(key1 = imageUris.value) {
        galleryController.loadImagesForUser(userId)
    }

    val selectedImage = remember { mutableStateOf<Uri?>(null) }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val inputStream = context.contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                val imageData = byteArrayOutputStream.toByteArray()
                galleryController.saveImage(imageData, userId)
                imageUris.value += it
            }
        }
    val selectedImageBitmap = remember { mutableStateOf<Bitmap?>(null) }
    val textStyle = TextStyle(
        fontWeight = FontWeight.ExtraBold,
        fontSize = 42.sp,
        fontFamily = com.example.cypher_vault.view.resources.fontFamily,
        letterSpacing = 2.sp
    )
    val firstLetter = Color(0xFF2DDEFD)
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

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
                            )
                            Text(
                                text = "ypher ",
                                color = mainBackgroundColor,
                                style = textStyleTittle,
                            )
                            Text(
                                text = "V",
                                color = firstColor,
                                style = textStyleTittle,
                            )
                            Text(
                                text = "ault",
                                color = mainBackgroundColor,
                                style = textStyleTittle,
                            )

                        }
                        Row(horizontalArrangement = Arrangement.Absolute.Left) {
                            Text(
                                text = "G",
                                color = firstColor,
                                style = textStyleTittle2,
                            )
                            Text(
                                text = "aleria de ",
                                color = mainBackgroundColor,
                                style = textStyleTittle2,
                            )
                            Text(
                                text = capitalizarPrimeraLetra(nombre!!),
                                color = firstColor,
                                style = textStyleTittle2,
                            )
                            Text(
                                text = procesarString(nombre),
                                color = mainBackgroundColor,
                                style = textStyleTittle2,
                            )

                        }
                    }

                },
                navigationIcon = {
                    IconButton(onClick = { authenticationController.navigateToListLogin() }) {
                        Icon(
                            modifier = Modifier.width(30.dp),
                            tint = firstColor,
                            imageVector = Icons.Filled.ArrowBackIosNew,
                            contentDescription = "Localized description"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { authenticationController.navigateToProfile(userId) }) {
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
                            BitmapFactory.decodeByteArray(image.imageData, 0, image.imageData.size)
                        bitmap?.let {
                            Image(
                                bitmap = it.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(100.dp)
                                    .padding(4.dp)
                                    .border(2.dp, Color.Black)
                                    .clickable {
                                        selectedImageBitmap.value = BitmapFactory.decodeByteArray(
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


fun capitalizarPrimeraLetra(palabra: String): String {
    if (palabra.isEmpty()) {
        return palabra
    }
    return palabra.substring(0, 1).uppercase(Locale.getDefault())
}

fun procesarString(texto: String): String {
    if (texto.length <= 1) {
        return texto
    }

    val minusculas = texto.substring(1).lowercase(Locale.getDefault())
    return if (minusculas.length <= 16) {
        minusculas
    } else {
        minusculas.substring(0, 14) + ".."
    }
}
