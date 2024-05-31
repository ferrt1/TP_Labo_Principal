package com.example.cypher_vault.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserIncomeDao {

    @Insert
    suspend fun insertIncome(userIncome: UserIncome)

    @Query("SELECT * FROM user_income WHERE user_id = :userId ORDER BY income DESC LIMIT 1")
    suspend fun getLastIncome(userId: String): List<UserIncome?>

    @Query("SELECT COUNT(*) FROM user_income WHERE income = :specificIncome and user_id = :userId")
    suspend fun hasIncomeOnDay(userId: String, specificIncome: Long): Int

    @Query("SELECT * FROM user_income WHERE user_id = :userId ORDER BY income DESC")
    suspend fun getAllIncomes(userId: String): List<UserIncome?>

    @Query("SELECT * FROM user_income WHERE user_id = :userId ORDER BY income DESC LIMIT 5")
    suspend fun getLastFiveIncomes(userId: String): List<UserIncome?>
}
