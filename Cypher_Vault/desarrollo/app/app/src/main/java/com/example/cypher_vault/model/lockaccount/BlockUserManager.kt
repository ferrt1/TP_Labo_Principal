package com.example.cypher_vault.model.lockaccount

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.cypher_vault.controller.data.DatabaseController
import com.example.cypher_vault.controller.lockaccount.BlockUserController
import com.example.cypher_vault.database.BlockedUsers
import com.example.cypher_vault.database.UserPremium
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class BlockUserManager {

    val db = DatabaseController()
    val maxAttempts = 3
    private var user: BlockedUsers? = null

    suspend fun getBlockedUser(userId: String): BlockedUsers? {
        user = getBlockedUserAsync(userId).await()
        Log.d("lockAccount", "///////user: $user")
        if (user == null) {
            createBlockUser(userId)
            user = getBlockedUserAsync(userId).await()  // Asegúrate de actualizar `user` después de crear
        }
        return user
    }

    suspend fun getBlockedUsers(): List<BlockedUsers?> {
        return db.getBlockedUsers()
    }

    private fun getBlockedUserAsync(userId: String): Deferred<BlockedUsers?> {
        return CoroutineScope(Dispatchers.IO).async {
            db.getBlockedUser(userId)
        }
    }

    suspend fun createBlockUser(userId: String) {
        Log.d("lockAccount", "///////createBlockUser")
        Log.d("lockAccount", "///////user: $user")
        val blockDate = 0.toLong()
        val isBlocked = false
        val blockedUser = BlockedUsers(block_date = blockDate, blocked_user = isBlocked, user_id = userId, attempts = 0)
        db.insertBlockUser(blockedUser).await()
    }

    fun getBlockDate(userId: String): String {
        val blockUser = user
        val blockDate = getUntilDays(blockUser?.block_date)
        return blockDate
    }

    private fun getUntilDays(blockDate: Long?): String {
        val currentTime = System.currentTimeMillis()
        val timeDifference = currentTime - (blockDate ?: 0)
        val days = timeDifference / (24 * 60 * 60 * 1000)
        return days.toString()
    }

    fun deleteBlockUser(userId: String) {
        db.deleteBlockedUser(userId)
    }

    fun getAttempt(userId: String): Int? {
        val userAttempt: Int? = user?.attempts
        return userAttempt
    }

    suspend fun setBlocked(userId: String) {
        db.setBlocked(userId, true).await()
    }

    suspend fun updateAttempts(blockedUsers: BlockedUsers, attempts: Int) {
        user = blockedUsers
        val userId = user?.user_id
        Log.d("lockAccount", "///////updateAttempts")
        Log.d("lockAccount", "///////attempts: $attempts")
        Log.d("lockAccount", "///////userId: $userId")

        if (userId != null) {
            db.updateAttempts(userId, attempts).await()
        }
        if(attempts >= maxAttempts){
            if (userId != null) {
                Log.d("lockAccount", "///////Blockeo de usuario: $userId")
                setBlocked(userId)
            }
        }
    }

    suspend fun setBlockDate(userId: String, date: Long) {
        db.setBlockDate(userId, date).await()
    }
}