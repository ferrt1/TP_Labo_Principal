package com.example.cypher_vault.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "images",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["uid"],
        childColumns = ["user_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Images(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var imageData: ByteArray,
    val user_id: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Images

        if (id != other.id) return false
        if (!imageData.contentEquals(other.imageData)) return false
        if (user_id != other.user_id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + imageData.contentHashCode()
        result = 31 * result + user_id.hashCode()
        return result
    }
}