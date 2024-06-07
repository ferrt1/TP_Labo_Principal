package com.example.cypher_vault.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "blocked_users",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["uid"],
        childColumns = ["user_id"],
        onDelete = ForeignKey.CASCADE
    )]
)

data class BlockedUsers (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val block_date: Long?, // Fecha de pago
    val blocked_user: Boolean?,
    val user_id: String
    )