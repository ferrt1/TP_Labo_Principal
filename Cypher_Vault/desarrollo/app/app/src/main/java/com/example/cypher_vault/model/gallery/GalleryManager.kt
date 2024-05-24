package com.example.cypher_vault.model.gallery

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cypher_vault.database.UserIncome
import com.example.cypher_vault.view.gallery.mainBackgroundColor
import com.example.cypher_vault.view.gallery.textStyleTittle2
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GalleryManager {

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

    fun formatIncomeDate(income: Long?): String {
        return if (income != null) {
            val date = Date(income)
            val formatter = SimpleDateFormat("HH:mm - dd MMM yyyy", Locale.getDefault())
            formatter.format(date)
        } else {
            "Fecha no disponible"
        }
    }


}