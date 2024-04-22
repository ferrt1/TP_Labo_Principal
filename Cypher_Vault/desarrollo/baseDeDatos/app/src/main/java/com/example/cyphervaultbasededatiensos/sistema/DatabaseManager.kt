package com.example.cyphervaultbasededatiensos.sistema

import android.content.Context
import androidx.room.Room
import com.example.cyphervaultbasededatiensos.baseDeDatos.AppDatabase
import com.example.cyphervaultbasededatiensos.baseDeDatos.Images
import com.example.cyphervaultbasededatiensos.baseDeDatos.ImagesRegister
import com.example.cyphervaultbasededatiensos.baseDeDatos.User

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

    fun getUserById(userId: Int): User? {
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

    fun getImagesForUser(userId: Int): List<Images> {
        return database.imageDao().getImagesForUser(userId)
    }

    // Métodos relacionados con la tabla de registros de imágenes
    fun insertImageRegister(imageRegister: ImagesRegister) {
        database.imageRegisterDao().insertImage(imageRegister)
    }

    fun getImageRegistersForImage(user_id: Long): List<ImagesRegister> {
        return database.imageRegisterDao().getImagesForUser(user_id)
    }

    // Otros métodos según sea necesario para otras operaciones con usuarios, imágenes e imágenes registros
}
