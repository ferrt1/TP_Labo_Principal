package com.example.cypher_vault.model.dbmanager

import android.content.Context
import androidx.room.Room
import com.example.cypher_vault.database.AppDatabase
import com.example.cypher_vault.database.Images
import com.example.cypher_vault.database.ImagesRegister
import com.example.cypher_vault.database.User
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceLandmark


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

    // Funciones de inserción, actualización y consulta de imágenes
    fun insertImageRegister(imagesRegister: ImagesRegister) {
        database.imageRegisterDao().insertImage(imagesRegister)
    }

    fun getImagesForUser(userId: String): List<ImagesRegister> {
        return  database.imageRegisterDao().getImagesForUser(userId)
    }

    fun updateImage(imagesRegister: ImagesRegister) {
        database.imageRegisterDao().updateImage(imagesRegister)
    }

    // Funciones para actualizar y obtener contornos faciales y landmarks faciales
    fun updateFaceContours(userId: String, faceContours: List<FaceContour>) {
        database.imageRegisterDao().updateFaceContours(userId, faceContours)
    }

    fun updateFaceLandmarks(userId: String, faceLandmarks: List<FaceLandmark>) {
        database.imageRegisterDao().updateFaceLandmarks(userId, faceLandmarks)
    }

    fun getFaceContoursForUser(userId: String): List<FaceContour> {
        return  database.imageRegisterDao().getFaceContoursForUser(userId)
    }

    fun getFaceLandmarksForUser(userId: String): List<FaceLandmark> {
        return  database.imageRegisterDao().getFaceLandmarksForUser(userId)
    }

    // Otros métodos según sea necesario para otras operaciones con usuarios, imágenes e imágenes registros
}