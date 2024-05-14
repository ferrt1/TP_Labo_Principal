package com.example.cypher_vault.view.gallery

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.cypher_vault.controller.authentication.AuthenticationController
import com.example.cypher_vault.controller.gallery.GalleryController
import com.example.cypher_vault.view.registration.findAncestorActivity
import java.io.ByteArrayOutputStream
import com.example.cypher_vault.view.resources.*


@Composable
fun Gallery(authenticationController: AuthenticationController, userId: String, galleryController: GalleryController) {


    val context = LocalContext.current
    val activity = context.findAncestorActivity()

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 200)
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
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
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

    val textStyle = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 42.sp, fontFamily = fontFamily, letterSpacing = 2.sp)
    val firstLetter = Color(0xFF2DDEFD)

    val selectedImageBitmap = remember { mutableStateOf<Bitmap?>(null) }


    Column(modifier = Modifier.fillMaxSize()) {

        Box (
            modifier=Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(thirdColor)
                .padding(0.dp, 10.dp,0.dp, 10.dp)){
            Row (
                horizontalArrangement = Arrangement.Absolute.Left
            ){
                Text(
                    text = "G",
                    color = firstLetter,
                    style = textStyle,
                )
                Text(
                    text = "aleria Vault",
                    color = Color.White,
                    style = textStyle,
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(images.size) { index ->
                val image = images[index]
                val bitmap = BitmapFactory.decodeByteArray(image.imageData, 0, image.imageData.size)
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(4.dp)
                        .border(2.dp, Color.Black)
                        .clickable { selectedImageBitmap.value = BitmapFactory.decodeByteArray(image.imageData, 0, image.imageData.size) }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = { authenticationController.navigateToListLogin() },
                shape = RoundedCornerShape(15.dp),
                border = BorderStroke(3.dp, Color.Gray),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Gray
                ),
                modifier = Modifier.width(100.dp)
            ) {
                Text(
                    "Inicio",
                    fontFamily = fontFamily,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
            }

            OutlinedButton(
                onClick = { authenticationController.navigateToProfile(userId) },
                shape = RoundedCornerShape(15.dp),
                border = BorderStroke(3.dp, Color.Gray),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Gray
                ),
                modifier = Modifier.width(100.dp)
            ) {
                Text(
                    "Perfil",
                    fontFamily = fontFamily,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
            }

            OutlinedButton(
                onClick = {  launcher.launch("image/*") },
                shape = RoundedCornerShape(15.dp),
                border = BorderStroke(3.dp, thirdColor),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = thirdColor
                ),
                modifier = Modifier.width(100.dp)
            ) {
                Text(
                    "+",
                    fontFamily = fontFamily,
                    color = thirdColor,
                    fontWeight = FontWeight.Bold
                )
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


