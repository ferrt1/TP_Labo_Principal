package com.example.cypher_vault.model.lockaccount

import com.example.cypher_vault.controller.data.DatabaseController
import com.example.cypher_vault.database.BlockedUsers
import com.example.cypher_vault.database.UserPremium
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class BlockUserManager {

    val db = DatabaseController()

    fun blockUser(userId: String): Deferred<Unit> {
        return CoroutineScope(Dispatchers.IO).async {
            val blockDate = System.currentTimeMillis()
            val isBlocked = true
            val blockedUser = BlockedUsers(block_date = blockDate, blocked_user = isBlocked, user_id = userId)
            db.insertBlockUser(blockedUser)
        }
    }

    fun getBlockDate(userId: String): String {
        val blockUser = db.getBlockedUser(userId)
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

}