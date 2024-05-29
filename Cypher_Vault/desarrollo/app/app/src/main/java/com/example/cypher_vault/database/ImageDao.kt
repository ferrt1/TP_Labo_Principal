package com.example.cypher_vault.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ImageDao {
    @Insert
    fun insertImage(images: Images)

    @Query("SELECT * FROM images WHERE user_id = :userId")
    fun getImagesForUser(userId: String): List<Images>

    // Otros métodos según sea necesario
    @Query("DELETE FROM images WHERE user_id = :userId")
    fun deleteImagesForUser(userId: String)

    @Query("SELECT * FROM Images WHERE id = :imageId")
    fun getImageById(imageId: Long): Images?

    @Update
    fun updateImage(image: Images)

    @Delete
    fun deleteImage(image: Images)

}