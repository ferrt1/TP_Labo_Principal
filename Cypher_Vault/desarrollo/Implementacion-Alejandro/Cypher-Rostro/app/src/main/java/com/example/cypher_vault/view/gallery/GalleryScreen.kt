package com.example.cypher_vault.view.gallery

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.cypher_vault.R
import com.example.cypher_vault.controller.authentication.AuthenticationController

@Composable
fun GalleryScreen(authenticationController: AuthenticationController) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        // Añadir imagen superpuesta desde recursos
        val painter = painterResource(id = R.drawable.galeria) // Reemplaza R.drawable.overlay_image con el ID de tu imagen en la carpeta res
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp) // Ajusta el padding según sea necesario
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.wrapContentSize()
        ) {
            Text( "¡Bien Iniciaste Sesion! ")
            Button(onClick = {
                authenticationController.navigateToListLogin()
            }) {
                Text("Volver a Test-Inicio")
            }

        }
    }
}
