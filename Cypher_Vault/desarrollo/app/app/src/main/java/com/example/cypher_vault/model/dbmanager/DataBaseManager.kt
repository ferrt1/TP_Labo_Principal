package com.example.cypher_vault.model.dbmanager

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.example.cypher_vault.database.AppDatabase
import com.example.cypher_vault.database.Images
import com.example.cypher_vault.database.ImagesLogin
import com.example.cypher_vault.database.ImagesRegister
import com.example.cypher_vault.database.User
import com.example.cypher_vault.database.UserIncome


object DatabaseManager {
    private lateinit var database: AppDatabase

    fun initialize(context: Context) {
        database = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java, "my_database"
        ).build()
    }

    // Métodos relacionados con la tabla de usuarios
    fun getAllUsers(): List<User> {
        return database.userDao().getAll()
    }

    fun getUserById(userId: String): User? {
        return database.userDao().getUserById(userId)
    }

    fun insertUser(user: User) {
        database.userDao().insert(user)
    }

    fun deleteUser(user: User) {
        database.userDao().delete(user)
    }

    // Métodos relacionados con la tabla de imágenes
    fun insertImage(image: Images) {
        database.imageDao().insertImage(image)
    }

    fun getImagesForUser(userId: String): List<Images> {
        return database.imageDao().getImagesForUser(userId)
    }

    // Métodos relacionados con la tabla de registros de imágenes
    fun insertImageRegister(imageRegister: ImagesRegister) {
        database.imageRegisterDao().insertImage(imageRegister)
    }

    fun getImageRegistersForImage(user_id: String): List<ImagesRegister> {
        return database.imageRegisterDao().getImagesForUser(user_id)
    }

    fun getLastImageRegisterForUser(user_id: String): ImagesRegister? {
        val imageRegisters = database.imageRegisterDao().getImagesForUser(user_id)
        return imageRegisters.lastOrNull()
    }


    fun insertImageLogin(imagesLogin: ImagesLogin) {
        database.imageLoginDao().insertImage(imagesLogin)
    }

    fun getImageLoginForImage(user_id: String): List<ImagesLogin> {
        return database.imageLoginDao().getImagesForUser(user_id)
    }

    fun deleteLoginImagesForUser(userId: String) {
        database.imageLoginDao().deleteImagesForUser(userId)
    }

    fun getLastImageLoginForUser(user_id: String): ImagesLogin? {
        val imagesLogin = database.imageLoginDao().getImagesForUser(user_id)
        return imagesLogin.lastOrNull()
    }

    suspend fun insertIncome(userIncome: UserIncome) {
        database.userIncomeDao().insertIncome(userIncome)
    }

    suspend fun getLastIncome(userId: String): List<UserIncome?> {
        return database.userIncomeDao().getLastIncome(userId: String)
    }

    suspend fun hasIncomeOnDay(userId: String,specificIncome: Long): Int {
        return database.userIncomeDao().hasIncomeOnDay(userId,specificIncome)
    }

    suspend fun getAllIncomes(userId: String): List<UserIncome?> {
        return database.userIncomeDao().getAllIncomes(userId)
    }

    suspend fun getLastTenIncomes(userId: String): List<UserIncome?> {
        return database.userIncomeDao().getLastTenIncomes(userId)
    }


    // Otros métodos según sea necesario para otras operaciones con usuarios, imágenes e imágenes registros
}