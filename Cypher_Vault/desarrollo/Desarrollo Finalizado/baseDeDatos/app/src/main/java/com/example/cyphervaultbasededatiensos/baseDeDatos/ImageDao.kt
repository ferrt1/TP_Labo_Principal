package com.example.cyphervaultbasededatiensos.baseDeDatos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
@Dao
interface ImageDao {
    @Insert
    fun insertImage(images: Images)

    @Query("SELECT * FROM images WHERE user_id = :userId")
    fun getImagesForUser(userId: Int): List<Images>

    // Otros métodos según sea necesario
}