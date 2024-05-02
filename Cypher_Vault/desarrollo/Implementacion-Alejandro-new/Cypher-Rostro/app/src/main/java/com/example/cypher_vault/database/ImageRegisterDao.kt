package com.example.cypher_vault.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceLandmark

@Dao
interface ImageRegisterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertImage(imagesRegister: ImagesRegister)

    @Query("SELECT * FROM images_register WHERE user_id = :userId")
    fun getImagesForUser(userId: String): List<ImagesRegister>

    @Update
    fun updateImage(imagesRegister: ImagesRegister)

    @Query("UPDATE images_register SET faceContours = :faceContours WHERE user_id = :userId")
    fun updateFaceContours(userId: String, faceContours: List<FaceContour>)

    @Query("UPDATE images_register SET faceLandmarks = :faceLandmarks WHERE user_id = :userId")
    fun updateFaceLandmarks(userId: String, faceLandmarks: List<FaceLandmark>)

    @Query("SELECT faceContours FROM images_register WHERE user_id = :userId")
    fun getFaceContoursForUser(userId: String): List<FaceContour>

    @Query("SELECT faceLandmarks FROM images_register WHERE user_id = :userId")
    fun getFaceLandmarksForUser(userId: String): List<FaceLandmark>
}