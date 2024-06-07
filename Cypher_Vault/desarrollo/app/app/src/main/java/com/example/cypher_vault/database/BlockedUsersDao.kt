package com.example.cypher_vault.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BlockedUsersDao {

    @Insert
    suspend fun insertBlockedUser(userPremium: BlockedUsers)

    @Query("SELECT * FROM blocked_users WHERE user_id = :userId")
    suspend fun getBlockedUser(userId: String): BlockedUsers?

    @Query("DELETE FROM blocked_users WHERE user_id = :userId")
    suspend fun deleteBlockedUser(userId: String)

}