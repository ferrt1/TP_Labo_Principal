package com.example.cypher_vault.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_premium",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["uid"],
        childColumns = ["user_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class UserPremium(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val active_subscription: Long?, // Fecha de pago
    val premium_account: Boolean?,
    val user_id: String
)
