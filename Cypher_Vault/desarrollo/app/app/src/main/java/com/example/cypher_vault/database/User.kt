package com.example.cypher_vault.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey val uid: String,
    @ColumnInfo(name = "first_name") val firstName: String?,
    @ColumnInfo(name = "email") val email: String?,
    @ColumnInfo(name = "entry_date") val entryDate: Long, // Fecha de ingreso
    @ColumnInfo(name = "password") val password: String?, // PIN del usuario
    @ColumnInfo(name = "double_authentication") val authentication: Boolean?, //doble autentificacion
    @ColumnInfo(name = "encryption_salt") val encryptionSalt: String?, // Salt para derivar la clave de cifrado
    @ColumnInfo(name = "profile_picture") val profile_picture: ByteArray // Salt para derivar la clave de cifrado
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        return profile_picture.contentEquals(other.profile_picture)
    }

    override fun hashCode(): Int {
        return profile_picture.contentHashCode()
    }
}