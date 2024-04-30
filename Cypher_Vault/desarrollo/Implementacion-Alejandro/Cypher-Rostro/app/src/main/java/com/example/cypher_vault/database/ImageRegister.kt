package com.example.cypher_vault.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "images_register",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["uid"],
        childColumns = ["user_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class ImagesRegister(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val imageData: ByteArray,
    val user_id: Int // referencia al usuario que posee la imagen
)
