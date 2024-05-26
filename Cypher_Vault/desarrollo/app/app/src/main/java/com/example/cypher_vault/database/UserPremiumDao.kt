package com.example.cypher_vault.database


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserPremiumDao {

    @Insert
    suspend fun insertPremiumActiveAccount(userPremium: UserPremium)

    @Query("SELECT * FROM user_premium WHERE user_id = :userId")
    suspend fun getPremiumAccount(userId: String): UserPremium?


}