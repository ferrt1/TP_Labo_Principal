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

    @Query("SELECT * FROM blocked_users")
    suspend fun getAllBlockedUsers(): List<BlockedUsers>

    @Query("DELETE FROM blocked_users WHERE user_id = :userId")
    suspend fun deleteBlockedUser(userId: String)

    @Query("SELECT attempts FROM blocked_users WHERE user_id = :userId")
    suspend fun getUserAttempts(userId: String): Int?

    @Query("UPDATE blocked_users SET attempts = :attempts WHERE user_id = :userId")
    suspend fun updateUserAttempts(userId: String, attempts: Int)

    @Query("UPDATE blocked_users SET blocked_user = :blocked WHERE user_id = :userId")
    suspend fun setBlocked(userId: String, blocked: Boolean)
    @Query("UPDATE blocked_users SET block_date = :date WHERE user_id = :userId")
    suspend fun setBlockDate(userId: String, date: Long)

}