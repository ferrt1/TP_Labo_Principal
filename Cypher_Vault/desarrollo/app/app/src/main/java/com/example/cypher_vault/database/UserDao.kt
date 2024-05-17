package com.example.cypher_vault.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<User>

    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
            "email LIKE :last LIMIT 1")
    fun findByName(first: String, last: String): User
    @Insert
    fun insert(user: User) // Método para insertar un solo usuario
    @Query("SELECT * FROM user WHERE email = :email LIMIT 1")
    fun findByEmail(email: String): User? // Método para buscar un usuario por su correo electrónico
    @Delete
    fun delete(user: User)
    @Insert
    fun insertAll(vararg users: User)
    @Query("SELECT * FROM user WHERE uid = :userId")
    fun getUserById(userId: String): User?
}