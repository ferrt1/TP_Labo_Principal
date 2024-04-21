package com.example.cypher_vault

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.util.Log

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            miBottonComponente()
        }
    }
}

@Composable
fun miBottonComponente() {

    val context = LocalContext.current
    val activity = context.findAncestorActivity()
    // Crea un estado mutable para almacenar el bitmap
    val imageBitmap = remember { mutableStateOf<Bitmap?>(null) }
    // Solicita los permisos de la cámara
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.CAMERA), 200)
    }

    // Crea un callback para manejar el resultado de la captura de la imagen
    val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        // Almacena el bitmap en el estado mutable
        imageBitmap.value = bitmap
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = {
            // Verifica si se tienen los permisos necesarios
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                // Inicia la cámara
                takePicture.launch(null)
            } else {
                Toast.makeText(context, "Permiso de cámara no concedido", Toast.LENGTH_LONG).show()
            }
        }) {
            Text("hola mundo")
        }

        // Muestra la imagen capturada
        imageBitmap.value?.let { bitmap ->
            Image(
                painter = BitmapPainter(bitmap.asImageBitmap()),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
    }

}



fun Context.findAncestorActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }
        context = context.baseContext
    }
    return null
}