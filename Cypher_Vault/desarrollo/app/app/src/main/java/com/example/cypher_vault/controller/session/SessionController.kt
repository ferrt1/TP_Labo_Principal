package com.example.cypher_vault.controller.session

import android.content.Context
import android.content.Intent
import com.example.cypher_vault.MainActivity
import com.example.cypher_vault.controller.navigation.NavController

object SessionController {

    fun logout(context: Context, navController: NavController) {
        navController.navigateToListLogin()
        //restartApplication(context)
    }

    private fun restartApplication(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)
    }
}