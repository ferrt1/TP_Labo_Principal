package com.example.cyphervaultbasededatiensos

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.cyphervaultbasededatiensos.baseDeDatos.Images
import com.example.cyphervaultbasededatiensos.baseDeDatos.ImagesRegister
import com.example.cyphervaultbasededatiensos.baseDeDatos.User
import com.example.cyphervaultbasededatiensos.sistema.DatabaseManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar la base de datos
        DatabaseManager.initialize(applicationContext)

        // Cargar datos ficticios en las tablas en un hilo de fondo
        lifecycleScope.launch(Dispatchers.IO) {
            loadDummyData()
        }
    }

    private suspend fun loadDummyData() {
        // Cargar datos ficticios en la tabla de usuarios
        val user1 = User(1, "John", "john@example.com", System.currentTimeMillis(),"1234")
        val user2 = User(2, "Alice", "alice@example.com", System.currentTimeMillis(),"1234")
        DatabaseManager.insertUser(user1)
        DatabaseManager.insertUser(user2)

        // Mostrar usuarios a través de Log
        val users = DatabaseManager.getAllUsers()
        for (user in users) {
            Log.e("tabla", "user: id=${user.uid}, name=${user.firstName}, email=${user.email}, entryDate=${user.entryDate}")
        }

        // Cargar datos ficticios en la tabla de imágenes
        val image1 = Images(1, ByteArray(10) { Random.nextInt(0, 256).toByte() }, 1)
        val image2 = Images(2, ByteArray(10) { Random.nextInt(0, 256).toByte() }, 1)
        DatabaseManager.insertImage(image1)
        DatabaseManager.insertImage(image2)

        // Mostrar imágenes a través de Log
        val images = DatabaseManager.getImagesForUser(1)
        for (image in images) {
            Log.e("tabla", "image: id=${image.id}, user_id=${image.user_id}")
        }

        // Cargar datos ficticios en la tabla de registros de imágenes
        fun generateRandomByteArray(size: Int): ByteArray {
            return ByteArray(size) { Random.nextInt(0, 256).toByte() }
        }
        val byteArray = generateRandomByteArray(10)
        val register1 = ImagesRegister(1, byteArray, 1)
        val register2 = ImagesRegister(2, byteArray, 2)
        DatabaseManager.insertImageRegister(register1)
        DatabaseManager.insertImageRegister(register2)

        // Mostrar registros de imágenes a través de Log
        val registers = DatabaseManager.getImageRegistersForImage(1)
        for (register in registers) {
            Log.e("tabla", "images_register: id=${register.id}, image_id=${register.imageData}, date=${register.user_id}")
        }
    }
}
