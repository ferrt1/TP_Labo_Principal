package com.example.cypher_vault.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserIncomeDao {

    @Insert
    suspend fun insertIncome(userIncome: UserIncome)

    @Query("SELECT * FROM user_income ORDER BY income DESC LIMIT 1")
    suspend fun getLastIncome(): List<UserIncome?>

    @Query("SELECT COUNT(*) FROM user_income WHERE income = :specificIncome")
    suspend fun hasIncomeOnDay(specificIncome: Long): Int

    @Query("SELECT * FROM user_income ORDER BY income DESC")
    suspend fun getAllIncomes(): List<UserIncome?>

    @Query("SELECT * FROM user_income ORDER BY income DESC LIMIT 10")
    suspend fun getLastTenIncomes(): List<UserIncome?>
}