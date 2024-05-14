package com.example.cypher_vault.view.gallery

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cypher_vault.R
import com.example.cypher_vault.controller.authentication.AuthenticationController
import com.example.cypher_vault.view.registration.findAncestorActivity
import com.example.cypher_vault.view.resources.*


@Composable
fun UserProfile(authenticationController: AuthenticationController, userId: String) {
    val context = LocalContext.current
    val activity = context.findAncestorActivity()

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
                    text = "P",
                    color = firstLetter,
                    style = textStyle,
                )
                Text(
                    text = "erfil Vault",
                    color = Color.White,
                    style = textStyle,
                )
            }
        }

        // Aquí puedes agregar los elementos de la vista de perfil del usuario
        // como su foto de perfil, nombre, correo electrónico, etc.

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
                onClick = { /* Aquí puedes agregar la acción para editar el perfil */ },
                shape = RoundedCornerShape(15.dp),
                border = BorderStroke(3.dp, Color.Gray),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Gray
                ),
                modifier = Modifier.width(100.dp)
            ) {
                Text(
                    "Editar",
                    fontFamily = fontFamily,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
            }

            OutlinedButton(
                onClick = { authenticationController.navigateToListLogin() },
                shape = RoundedCornerShape(15.dp),
                border = BorderStroke(3.dp, thirdColor),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = thirdColor
                ),
                modifier = Modifier.width(100.dp)
            ) {
                Text(
                    "Salir",
                    fontFamily = fontFamily,
                    color = thirdColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
