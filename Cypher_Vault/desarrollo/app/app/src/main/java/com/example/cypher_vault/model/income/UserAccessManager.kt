package com.example.cypher_vault.model.income

import com.example.cypher_vault.database.UserIncome
import com.example.cypher_vault.model.dbmanager.DatabaseManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserAccessManager {

    suspend fun insertUserIncome(userIncome: UserIncome) {
        withContext(Dispatchers.IO) {
            DatabaseManager.insertIncome(userIncome)
        }
    }

    suspend fun getLastIncome(userId: String): List<UserIncome?> {
        return withContext(Dispatchers.IO) {
            DatabaseManager.getLastIncome(userId)
        }
    }

    suspend fun hasIncomeOnDay(userId: String, specificIncome: Long): Int {
        return withContext(Dispatchers.IO) {
            DatabaseManager.hasIncomeOnDay(userId,specificIncome)
        }
    }

    suspend fun getAllIncomes(userId: String): List<UserIncome?> {
        return withContext(Dispatchers.IO) {
            DatabaseManager.getAllIncomes(userId)
        }
    }

    suspend fun getLastTenIncomes(userId: String): List<UserIncome?> {
        return withContext(Dispatchers.IO) {
            DatabaseManager.getLastTenIncomes(userId)
        }
    }
}