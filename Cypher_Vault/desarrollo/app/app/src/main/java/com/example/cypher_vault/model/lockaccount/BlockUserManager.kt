package com.example.cypher_vault.model.lockaccount

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.cypher_vault.controller.data.DatabaseController
import com.example.cypher_vault.controller.lockaccount.BlockUserController
import com.example.cypher_vault.database.BlockedUsers
import com.example.cypher_vault.database.UserPremium
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class BlockUserManager(private val userId: String){

    val db = DatabaseController()
    val maxAttempts = 3
    var user: BlockedUsers? = null

    suspend fun getBlockedUser(userId: String): BlockedUsers? {
        user = getBlockedUserAsync(userId).await()
        Log.d("lockAccount", "///////user: $user")
        return user
    }

    fun getBlockedUserAsync(userId: String): Deferred<BlockedUsers?> {
        return CoroutineScope(Dispatchers.IO).async {
            db.getBlockedUser(userId)
        }
    }

    fun blockUser(userId: String): Deferred<Unit> {
        val blockUserExists =  user
        if (blockUserExists != null) {
            return CoroutineScope(Dispatchers.IO).async {
                db.setBlocked(userId, true)
            }
        }else{
            return CoroutineScope(Dispatchers.IO).async {
                val blockDate = 0.toLong()
                val isBlocked = true
                val blockedUser = BlockedUsers(block_date = blockDate, blocked_user = isBlocked, user_id = userId, attempts = 3)
                db.insertBlockUser(blockedUser)
            }
        }
    }

    fun createBlockUser(userId: String): Any? {
        Log.d("lockAccount", "///////createBlockUser")
        Log.d("lockAccount", "///////user: $user")
        val blockUserExists =  user
        if (user != null || user?.user_id == userId ) {
            return null
            }else{
                return CoroutineScope(Dispatchers.IO).async {
                    val blockDate = 0.toLong()
                    val isBlocked = false
                    val blockedUser = BlockedUsers(block_date = blockDate, blocked_user = isBlocked, user_id = userId, attempts = 0)
                    db.insertBlockUser(blockedUser)
                }
        }
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
        var userAttemp : Int? = user?.attempts
        return userAttemp
    }

    fun setBlocked(userId: String) {
       db.setBlocked(userId, true)
    }

    fun setAttempts(userId: String, attempts: Int) {
        Log.d("lockAccount", "///////setAttempts")
        Log.d("lockAccount", "///////attempts: $attempts")
        Log.d("lockAccount", "///////user: $user")
        if(user != null ){
            db.updateAttempts(userId, attempts)
        }
    }

}