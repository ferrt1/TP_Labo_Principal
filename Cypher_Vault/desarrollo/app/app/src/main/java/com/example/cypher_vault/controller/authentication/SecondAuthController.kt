package com.example.cypher_vault.controller.authentication

import android.content.Context
import android.util.Log
import com.example.cypher_vault.model.authentication.SecondAuthManager


class SecondAuthController(manager: SecondAuthManager) {

    private val manager: SecondAuthManager = manager

    fun authenticateWOConection(userId : String, dayPart: SecondAuthManager.DayPart): Boolean {
        return manager.authenticateWOConection(userId,dayPart)
    }

    fun sendMail(context: Context, userId: String) {
        Log.e("MailConfirmation", "Ingreso al mail")
        return manager.sendMail(context, userId)
    }

}