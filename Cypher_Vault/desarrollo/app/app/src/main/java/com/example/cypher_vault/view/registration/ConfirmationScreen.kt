package com.example.cypher_vault.view.registration

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cypher_vault.R
import com.example.cypher_vault.controller.data.DatabaseController
import com.example.cypher_vault.controller.navigation.NavController
import com.example.cypher_vault.database.ImagesRegister
import com.example.cypher_vault.view.resources.fontFamily
import com.example.cypher_vault.view.resources.thirdColor
import kotlinx.coroutines.launch


private var databaseController = DatabaseController()
private val firstColor = Color(0xFF02a6c3)

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ConfirmationScreen(navController: NavController, userId: String, registerSuccessful: Boolean, messageError: String) {
    var showImage by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val imageRegisters = remember { mutableStateOf<List<ImagesRegister>?>(null) }
    coroutineScope.launch {
        imageRegisters.value = databaseController.getImageRegistersForUser(userId)
        Log.d("Imagen", imageRegisters.toString() + "user:" + userId);
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (registerSuccessful) {
            Image(
                painter = painterResource(id = R.drawable.successful),
                contentDescription = "Registro exitoso",
                modifier = Modifier.size(128.dp)
            )
            Text(
                "Registro exitoso!",
                fontSize = 20.sp,
                fontFamily = fontFamily,
                color = thirdColor,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(top = 16.dp)
            )
            OutlinedButton(
                onClick = { navController.navigateToListLogin() },
                shape = RoundedCornerShape(15.dp),
                border = BorderStroke(3.dp, firstColor),
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
                    color = thirdColor,
                    fontWeight = FontWeight.Bold
                )
            }
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
                    if (showImage) "Ocultar imágen" else "Ver imágen",
                    fontFamily = fontFamily,
                    color = thirdColor,
                    fontWeight = FontWeight.Bold
                )
            }
            if (showImage) {
                imageRegisters.value?.forEach { imageRegister ->
                    val bitmap = BitmapFactory.decodeByteArray(
                        imageRegister.imageData,
                        0,
                        imageRegister.imageData.size
                    )
                    bitmap?.let {
                        val imageBitmap = it.asImageBitmap()
                        Image(
                            painter = BitmapPainter(imageBitmap),
                            contentDescription = "Imágen guardada",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        } else {
            Image(
                painter = painterResource(id = R.drawable.failure),
                contentDescription = "Ocurrió un error",
                modifier = Modifier.size(128.dp)
            )
            Text(
                "Ocurrió un error",
                fontSize = 20.sp,
                fontFamily = fontFamily,
                color = Color.Red,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(top = 16.dp)
            )
            OutlinedButton(
                onClick = { navController.navigateToRegister() },
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
                    "Volver al inicio",
                    fontFamily = fontFamily,
                    color = thirdColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
