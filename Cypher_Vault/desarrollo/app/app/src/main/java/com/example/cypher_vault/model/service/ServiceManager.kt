package com.example.cypher_vault.model.service

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.util.Log
import com.example.cypher_vault.controller.data.DatabaseController
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.runBlocking

class ServiceManager(private val context: Context) {

    var db = DatabaseController()

    fun hasInternetConnection(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private var mailCode: String = ""
    fun getMailCode(): String {
        return mailCode
    }
    fun generateAndSendCode(context: Context, userId: String) {
        var recipient = ""
        runBlocking {
            val usuario = db.getUserById(userId)
            recipient = usuario?.email.toString()
        }
        if(mailCode==""){
            generateRandomCode()
        }
        val subject = "Tu código de verificación de CypherVault"
        val body = """
        Estimado Usuario,

        Su código de verificación para CypherVault es: $mailCode

        Por favor, ingrese este código en la aplicación para continuar.

        Atentamente,
        El equipo de CypherVault
    """.trimIndent()
        sendEmail(context, recipient, subject, body)
    }

    fun sendEmail(context: Context, recipient: String, subject: String, body: String, attachmentUri: Uri? = null) {
        val emailSelectorIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
        }

        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            setSelector(emailSelectorIntent)
        }

        attachmentUri?.let {
            emailIntent.putExtra(Intent.EXTRA_STREAM, it)
        }

        try {
            context.startActivity(emailIntent)
        } catch (e: ActivityNotFoundException) {
            // Manejar el caso en que no hay aplicaciones de correo electrónico disponibles
            e.printStackTrace()
        }
    }

    fun generateRandomCode() {
        if(mailCode==""){
            val random = (10000..99999).random()
            mailCode = random.toString()
            Log.d("Random Code", "random CODE: " + random.toString())
        }else{
            mailCode = mailCode
        }
    }
}