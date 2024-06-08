package com.example.cypher_vault.controller.lockaccount

import android.util.Log
import com.example.cypher_vault.database.BlockedUsers
import com.example.cypher_vault.model.lockaccount.BlockUserManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class BlockUserController(private val blockUserManager : BlockUserManager) {

    fun blockUser(userId: String) {
        blockUserManager.blockUser(userId)
    }

    suspend fun getBlockedUser(userId: String): BlockedUsers? {
        Log.d("lockAccount", "getBlockedUser")
        return blockUserManager.getBlockedUser(userId)
    }

    fun getBlockDate(userId: String): String {
        return blockUserManager.getBlockDate(userId)
    }

    fun deleteBlock(userId: String) {
        blockUserManager.deleteBlockUser(userId)
    }

    fun getAttempt(userId: String): Int? {
       return blockUserManager.getAttempt(userId)
    }

    fun setAttempts(userId: String, attempt: Int) {
        blockUserManager.setAttempts(userId, attempt)
    }

}