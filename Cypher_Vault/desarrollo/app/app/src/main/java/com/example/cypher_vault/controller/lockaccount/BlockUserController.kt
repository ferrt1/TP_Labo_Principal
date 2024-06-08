package com.example.cypher_vault.controller.lockaccount

import com.example.cypher_vault.model.lockaccount.BlockUserManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class BlockUserController(private val blockUserManager : BlockUserManager) {

    fun blockUser(userId: String) {
        blockUserManager.blockUser(userId)
    }

    fun getBlockDate(userId: String): String {
        return blockUserManager.getBlockDate(userId)
    }

    fun deleteBlock(userId: String) {
        blockUserManager.deleteBlockUser(userId)
    }

//    fun getAttempt(userId: String): Int? {
//        runBlocking {
//            withContext(Dispatchers.IO) {
//                val attempts = blockUserManager.getAttempt(userId)
//                return@withContext attempts
//            }
//        }
//        return null
//    }

    fun setAttempts(userId: String, attempt: Int) {
        blockUserManager.setAttempts(userId, attempt)
    }

}