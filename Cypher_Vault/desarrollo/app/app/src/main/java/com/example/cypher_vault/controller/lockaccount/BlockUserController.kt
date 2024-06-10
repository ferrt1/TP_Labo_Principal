package com.example.cypher_vault.controller.lockaccount

import android.content.Context
import android.util.Log
import com.example.cypher_vault.database.BlockedUsers
import com.example.cypher_vault.model.lockaccount.BlockUserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class BlockUserController(private val blockUserManager : BlockUserManager) {

    suspend fun blockUser(userId: String) {
        blockUserManager.setBlocked(userId)
    }

    suspend fun getBlockedUser(userId: String): BlockedUsers? {
        Log.d("lockAccount", "getBlockedUser")
        return blockUserManager.getBlockedUser(userId)
    }

    suspend fun getBlockedUsers(): List<BlockedUsers?> {
        return blockUserManager.getBlockedUsers()
    }

    fun getBlockDate(userId: String): String {
        return blockUserManager.getBlockDate(userId)
    }

    suspend fun setBlockDate(userId: String, date: Long) {
        blockUserManager.setBlockDate(userId, date)
    }

    fun deleteBlock(userId: String) {
        blockUserManager.deleteBlockUser(userId)
    }

    fun getAttempt(userId: String): Int? {
       return blockUserManager.getAttempt(userId)
    }

    suspend fun setAttempts(blockedUser: BlockedUsers, attempt: Int){
        blockUserManager.updateAttempts(blockedUser, attempt)
    }

    fun sendMail(context: Context, userId: String): String {
        Log.e("MailConfirmation", "Ingreso al mail")
        return blockUserManager.sendMail(context, userId)
    }


}