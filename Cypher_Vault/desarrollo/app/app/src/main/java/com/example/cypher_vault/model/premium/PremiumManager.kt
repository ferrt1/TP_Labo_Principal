package com.example.cypher_vault.model.premium

import com.example.cypher_vault.controller.data.DatabaseController
import com.example.cypher_vault.database.UserPremium
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PremiumManager() {

    val databaseController = DatabaseController()

    fun getPremiumUser(userId: String): UserPremium? {
        return runBlocking {
            databaseController.getPremiumActiveAccount(userId)
        }
    }

    fun insertPremiumUser(userId: String): Deferred<Unit> {
        return CoroutineScope(Dispatchers.IO).async {
            val activeSubscription = System.currentTimeMillis()
            val premiumAccount = true
            val userPremium = UserPremium(active_subscription =  activeSubscription, premium_account = premiumAccount, user_id = userId)
            databaseController.insertPremiumActiveAccount(userPremium)
        }
    }
    fun formatIncomeDate(date: Long?): String {
        return if (date != null) {
            val date = Date(date)
            val formatter = SimpleDateFormat("HH:mm - dd MMM yyyy", Locale.getDefault())
            formatter.format(date)
        } else {
            "Fecha no disponible"
        }
    }

}