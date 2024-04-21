package com.example.cyphervaultbasededatiensos

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.room.Room
import com.example.cyphervaultbasededatiensos.ui.theme.CypherVaultBaseDeDatiensosTheme
import com.example.cyphervaultbasededatiensos.baseDeDatos.AppDatabase
import com.example.cyphervaultbasededatiensos.baseDeDatos.User
import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    val db = Room.databaseBuilder(
        appContext,
        AppDatabase::class.java, "database-name"
    ).build()



    @Test
    fun listaVacia() {
        // Context of the app under test.
        val userDao = db.userDao()
        val users: List<User> = userDao.getAll()
        val listaVacia: List<User>? = null
        assertEquals(listaVacia,users)
    }
}