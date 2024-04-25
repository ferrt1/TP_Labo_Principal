package com.example.cyphervaultbasededatiensos.baseDeDatos

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "first_name") val firstName: String?,
    @ColumnInfo(name = "email") val email: String?,
    @ColumnInfo(name = "entry_date") val entryDate: Long, // Fecha de ingreso
    @ColumnInfo(name = "pin") val pin: String? // PIN del usuario
)