package com.example.cypher_vault.model.authentication

import android.content.Context
import android.util.Log
import com.example.cypher_vault.controller.data.DatabaseController
import com.example.cypher_vault.model.service.ServiceManager
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SecondAuthManager {

    var db = DatabaseController()

    fun authenticateWOConection(userId: String, dayPart: DayPart) : Boolean {
        val listaDeIngresos =  runBlocking { db.getLastFiveIncomes(userId) }
        val textoFormateado = formatIncomeDate(listaDeIngresos[0]?.income)
        val horaFormatoMilitar = pasarLaHoraAFormatoMilitar(textoFormateado)
        return when (dayPart) {
            DayPart.MORNING -> horaFormatoMilitar in 700..1459
            DayPart.AFTERNOON -> horaFormatoMilitar in 1500..2259
            DayPart.EVENING -> horaFormatoMilitar in 2300..2359 || horaFormatoMilitar in 0..659
        }
    }

    private fun pasarLaHoraAFormatoMilitar(textoFormateado: String): Int {
        val partes = textoFormateado.split(":")
        val horaInt = partes[0].toInt()
        val minutosInt = partes[1].split(" ")[0].toInt() // Suponiendo que el tiempo es "HH:mm - dd MMM yyyy"
        val tiempoMilitar = horaInt * 100 + minutosInt
        Log.d("auth", "Tiempo militar: $tiempoMilitar")
        return tiempoMilitar
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

    fun sendMail(context: Context, userId: String): String {
        val serviceManager = ServiceManager(context)
        var mail = ""
        runBlocking {
            val usuario = db.getUserById(userId)
            mail = usuario?.email.toString()
        }
        Log.d("auth", "Mail: $mail")
        return serviceManager.generateAndSendCode(context,mail)
    }

    //DEFINO LOS ENUM DE PARTES DEL DIA
    enum class DayPart {
        MORNING,
        AFTERNOON,
        EVENING
    }
}

