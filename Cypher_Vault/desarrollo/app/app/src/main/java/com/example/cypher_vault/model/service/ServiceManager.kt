package com.example.cypher_vault.model.service

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.util.Log
import com.example.cypher_vault.controller.data.DatabaseController
import kotlinx.coroutines.runBlocking
import android.os.Bundle;
import android.view.View;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

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
        if(mailCode==""){
            generateRandomCode()
        }
        return mailCode
    }

    fun generateAndSendCode(context: Context, mail: String) {
        var recipient = mail
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
        buttonSendEmail(recipient, subject, body)
    }

    private val emailUsername = "e7fc17113f8442"
    private val emailPassword = "894e458f6395d1"

    fun buttonSendEmail(receiverEmail: String, subjects: String, body: String) {
        val stringSenderEmail = emailUsername
        val stringReceiverEmail =receiverEmail
        val stringPasswordSenderEmail = emailPassword
        val stringHost = "sandbox.smtp.mailtrap.io"

        val properties = Properties().apply {
            put("mail.smtp.host", stringHost)
            put("mail.smtp.port", "587")
            put("mail.smtp.starttls.enable", "true");
            put("mail.smtp.auth", "true")
        }

        val session = Session.getInstance(properties, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(stringSenderEmail, stringPasswordSenderEmail)
            }
        })

        try {
            val mimeMessage = MimeMessage(session).apply {
                addRecipient(Message.RecipientType.TO, InternetAddress(stringReceiverEmail))
                subject = subjects
                setText(body)
            }

            Thread {
                try {
                    Log.d("SendEmail", "Enviando correo electrónico...")
                    Transport.send(mimeMessage)
                } catch (e: MessagingException) {
                    e.printStackTrace()
                    Log.e("SendEmail", "Error al enviar el correo electrónico: ${e.message}")
                }
            }.start()
        } catch (e: MessagingException) {
            e.printStackTrace()
            Log.e("SendEmail", "Error al enviar el correo electrónico: ${e.message}")
        }
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