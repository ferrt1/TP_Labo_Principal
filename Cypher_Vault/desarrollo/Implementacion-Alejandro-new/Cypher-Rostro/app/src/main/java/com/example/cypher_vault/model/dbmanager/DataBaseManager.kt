package com.example.cypher_vault.model.dbmanager

import android.content.Context
import androidx.room.Room
import com.example.cypher_vault.database.AppDatabase
import com.example.cypher_vault.database.Images
import com.example.cypher_vault.database.ImagesRegister
import com.example.cypher_vault.database.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
    fun insertImageRegister(imagesRegister: ImagesRegister): Boolean {
        return try {
            database.imageRegisterDao().insertImage(imagesRegister)
            true // La inserción fue exitosa
        } catch (e: Exception) {
            e.printStackTrace()
            false // Ocurrió un error durante la inserción
        }
    }

    fun getImagesForUser(userId: String): List<ImagesRegister> {
        return database.imageRegisterDao().getImagesForUser(userId)
    }

    fun updateImage(imagesRegister: ImagesRegister) {
        database.imageRegisterDao().updateImage(imagesRegister)
    }

    // Funciones para actualizar y obtener contornos faciales y landmarks faciales
    fun updateFaceContours(userId: String, faceContours: List<FaceContour>) {
        val faceContoursJson = Gson().toJson(faceContours)
        database.imageRegisterDao().updateFaceContours(userId, faceContoursJson)
    }

    fun updateFaceLandmarks(userId: String, faceLandmarks: List<FaceLandmark>) {
        val faceLandmarksJson = Gson().toJson(faceLandmarks)
        database.imageRegisterDao().updateFaceLandmarks(userId, faceLandmarksJson)
    }

    fun getFaceContoursForUser(userId: String): List<FaceContour> {
        val faceContoursJson = database.imageRegisterDao().getFaceContoursForUser(userId)
        return Gson().fromJson(faceContoursJson, object : TypeToken<List<FaceContour>>() {}.type)
    }

    fun getFaceLandmarksForUser(userId: String): List<FaceLandmark> {
        val faceLandmarksJson = database.imageRegisterDao().getFaceLandmarksForUser(userId)
        return Gson().fromJson(faceLandmarksJson, object : TypeToken<List<FaceLandmark>>() {}.type)
    }

    // Otros métodos según sea necesario para otras operaciones con usuarios, imágenes e imágenes registros
}