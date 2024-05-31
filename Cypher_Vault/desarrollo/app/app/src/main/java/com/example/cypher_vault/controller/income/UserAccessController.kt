package com.example.cypher_vault.controller.income

import androidx.compose.runtime.mutableStateOf
import com.example.cypher_vault.database.UserIncome
import com.example.cypher_vault.model.dbmanager.DatabaseManager
import com.example.cypher_vault.model.income.UserAccessManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class UserAccessController(private val userAccessManager: UserAccessManager) {

    val userIncomes = mutableStateOf<List<UserIncome>>(listOf())

    fun insertUserIncome(userId: String): Deferred<Unit> {
        val currentIncome = System.currentTimeMillis()
        val userIncome = UserIncome(income = currentIncome, user_id = userId)
        return insertUserIncomeAux(userIncome)
    }

    private fun insertUserIncomeAux(userIncome: UserIncome): Deferred<Unit> {
        return CoroutineScope(Dispatchers.IO).async {
            userAccessManager.insertUserIncome(userIncome)
        }
    }

    suspend fun loadLastIncome(userId: String) {
        userIncomes.value = userAccessManager.getLastIncome(userId) as List<UserIncome>
    }

    suspend fun checkIncomeOnDay(userId: String, specificIncome: Long): Int {
        return userAccessManager.hasIncomeOnDay(userId,specificIncome)
    }

    suspend fun loadAllIncomes(userId: String) {
        userIncomes.value = userAccessManager.getAllIncomes(userId) as List<UserIncome>
    }

    suspend fun loadLastFiveIncomes(userId: String) {
        userIncomes.value = userAccessManager.getLastFiveIncomes(userId) as List<UserIncome>
    }

}