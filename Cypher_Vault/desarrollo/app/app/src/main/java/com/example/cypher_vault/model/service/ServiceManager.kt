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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
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
    private val mutex = Mutex()
    fun getMailCode(): Unit = runBlocking {
        mutex.withLock {
            if (mailCode.isEmpty()) {
                generateRandomCode()
            }
            mailCode
        }
    }

    fun generateAndSendCode(context: Context, mail: String): String {
        var recipient = mail
        if(mailCode==""){
            runBlocking {
                generateRandomCode()
            }
        }
        val subject = "Tu código de verificación es $mailCode"
        val body = """
        Estimado Usuario,

        Su código de verificación para CypherVault es: $mailCode

        Por favor, ingrese este código en la aplicación para continuar.

        Atentamente,
        El equipo de CypherVault
    """.trimIndent()
        sendEmail(recipient, subject, body)
        return mailCode
    }

    fun generateAndSendUnblockCode(context: Context, mail: String): String {
        var recipient = mail
        if(mailCode==""){
            runBlocking {
                generateRandomCode()
            }
        }
        val subject = "Tu código de desbloqueo es $mailCode"
        val body = """
        Estimado Usuario,

        Su código de desbloqueo para CypherVault es: $mailCode

        Por favor, ingrese este código en la aplicación para poder desbloquear su cuenta.

        Atentamente,
        El equipo de CypherVault
    """.trimIndent()
        sendEmail(recipient, subject, body)
        return mailCode
    }

    private val emailUsername = "cyphervault@hotmail.com"
    private val emailPassword = "11111111111111a$"

    fun sendEmail(receiverEmail: String, subjects: String, body: String) {
        val stringSenderEmail = emailUsername
        val stringReceiverEmail = receiverEmail
        val stringPasswordSenderEmail = emailPassword
        val stringHost = "smtp-mail.outlook.com"

        val properties = Properties().apply {
            put("mail.transport.protocol", "smtp")
            put("mail.smtp.host", stringHost)
            put("mail.smtp.port", "587")
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
        }

        val session = Session.getInstance(properties, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(stringSenderEmail, stringPasswordSenderEmail)
            }
        })

        try {
            val mimeMessage = MimeMessage(session).apply {
                setFrom(InternetAddress(stringSenderEmail))
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

    fun generateRandomCode() {
        // Utilizar runBlocking para mantener el contexto de coroutinas si es necesario
        runBlocking {
            if (mailCode.isEmpty()) {
                val random = (10000..99999).random()
                mailCode = random.toString()
                Log.d("Random Code", "random CODE: $mailCode")
            }
        }
    }


}