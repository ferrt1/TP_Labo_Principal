package com.example.cypher_vault.model.service

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import java.util.Properties
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class ServiceManager(private val context: Context) {
    fun hasInternetConnection(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun generateAndSendCode(to: String): String {
        val code = generateRandomCode()
        val subject = "Your CypherVault Verification Code"
        val message = """
        Dear User,

        Your verification code for CypherVault is: $code

        Please enter this code in the application to proceed.

        Best regards,
        The CypherVault Team
    """.trimIndent()

        if (sendEmail(to, subject, message)) {
            return code
        } else {
            throw Exception("Failed to send email")
        }
    }

    fun sendEmail(to: String, subject: String, message: String): Boolean {
        val props = Properties().apply {
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", "587")
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
        }

        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication("your-email@gmail.com", "your-email-password")
            }
        })

        return try {
            val mimeMessage = MimeMessage(session).apply {
                setFrom(InternetAddress("your-email@gmail.com"))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(to))
                setSubject(subject)
                setText(message)
            }
            Transport.send(mimeMessage)
            true
        } catch (e: MessagingException) {
            e.printStackTrace()
            false
        }
    }

    fun generateRandomCode(): String {
        val random = (10000..99999).random()
        return random.toString()
    }


}