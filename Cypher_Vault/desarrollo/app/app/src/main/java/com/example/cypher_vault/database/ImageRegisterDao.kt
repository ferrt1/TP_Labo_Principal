package com.example.cypher_vault.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
@Dao
interface ImageRegisterDao {
    @Insert
    fun insertImage(imagesRegister: ImagesRegister)

    @Query("SELECT * FROM images_register WHERE user_id = :userId")
    fun getImagesForUser(userId: String): List<ImagesRegister>


}