package com.example.cyphervaultbasededatiensos

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.cyphervaultbasededatiensos.baseDeDatos.AppDatabase
import com.example.cyphervaultbasededatiensos.baseDeDatos.User
import com.example.cyphervaultbasededatiensos.ui.theme.CypherVaultBaseDeDatiensosTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class MainActivity : ComponentActivity() {
    private lateinit var db: AppDatabase
    private var usuarios: List<User> by mutableStateOf(emptyList()) // Lista de usuarios

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar db antes de llamar a obtenerUsuarios()
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        ).build()

        // Llamar a las pruebas
        runTests()
    }

    private fun runTests() {
        lifecycleScope.launch {
            // Insertar usuarios de prueba
            insertTestUsers()

            // Obtener y mostrar los usuarios en Logcat
            showUsersFromDatabase()
        }
    }

    private suspend fun insertTestUsers() {
        // Insertar al menos dos usuarios de prueba
        withContext(Dispatchers.IO) {
            db.userDao().insertAll(
                User(
                    uid = 1,
                    firstName = "John",
                    email = "john@example.com",
                    entryDate = Date().time
                ),
                User(
                    uid = 2,
                    firstName = "Jane",
                    email = "jane@example.com",
                    entryDate = Date().time
                )
            )
        }
    }

    private suspend fun showUsersFromDatabase() {
        // Obtener usuarios de la base de datos
        val users = withContext(Dispatchers.IO) {
            db.userDao().getAll()
        }

        // Mostrar usuarios en Logcat
        users.forEach {
            Log.d("User", "ID: ${it.uid}, Name: ${it.firstName}, Email: ${it.email}, Entry Date: ${Date(it.entryDate)}")
        }
    }

    // Otros métodos y funciones...
}