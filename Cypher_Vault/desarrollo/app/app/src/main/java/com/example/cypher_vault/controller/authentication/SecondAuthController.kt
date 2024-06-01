package com.example.cypher_vault.controller.authentication

import com.example.cypher_vault.model.authentication.SecondAuthManager


class SecondAuthController(manager: SecondAuthManager) {

    private val manager: SecondAuthManager = manager

    fun authenticateWOConection(userId : String, dayPart: SecondAuthManager.DayPart): Boolean {
        return manager.authenticateWOConection(userId,dayPart)
    }

}