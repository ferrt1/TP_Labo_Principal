package com.example.cypher_vault.controller.premium

import com.example.cypher_vault.controller.data.DatabaseController
import com.example.cypher_vault.database.UserPremium
import com.example.cypher_vault.model.premium.PremiumManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class PremiumController(private val premiumManager: PremiumManager) {


    fun getPremiumUser(userId: String): UserPremium? {
        return premiumManager.getPremiumUser(userId)
    }

    fun insertPremiumUser(userId: String) {
        premiumManager.insertPremiumUser(userId)
    }

    fun formatIncomeDate(date: Long?): String {
       return premiumManager.formatIncomeDate(date)
    }
}