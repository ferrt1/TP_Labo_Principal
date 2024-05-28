package com.example.cypher_vault.controller.session

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.cypher_vault.MainActivity

object SessionController {
    fun logout(context: Context) {
        // Perform logout operations here, such as:
        // - Clearing user data
        // - Invalidating access tokens
        // - Redirecting to the login screen

        // Restart the application
        restartApplication(context)
    }
    private fun restartApplication(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)
    }
}