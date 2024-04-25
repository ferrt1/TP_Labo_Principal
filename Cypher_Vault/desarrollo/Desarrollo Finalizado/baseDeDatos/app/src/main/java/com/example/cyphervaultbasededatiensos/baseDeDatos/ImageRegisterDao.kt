package com.example.cyphervaultbasededatiensos.baseDeDatos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
@Dao
interface ImageRegisterDao {
    @Insert
    fun insertImage(imagesRegister: ImagesRegister)

    @Query("SELECT * FROM images WHERE user_id = :userId")
    fun getImagesForUser(userId: Long): List<ImagesRegister>

    // Otros métodos según sea necesario
}