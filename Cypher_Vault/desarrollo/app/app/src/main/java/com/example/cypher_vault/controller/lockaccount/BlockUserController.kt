package com.example.cypher_vault.controller.lockaccount

import com.example.cypher_vault.model.lockaccount.BlockUserManager

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
}