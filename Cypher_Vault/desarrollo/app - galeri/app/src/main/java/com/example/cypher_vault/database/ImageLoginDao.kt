package com.example.cypher_vault.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
@Dao
interface ImageLoginDao {
    @Insert
    fun insertImage(imagesLogin: ImagesLogin)

    @Query("SELECT * FROM images_login WHERE user_id = :userId")
    fun getImagesForUser(userId: String): List<ImagesLogin>

    @Query("DELETE FROM images_login WHERE user_id = :userId")
    fun deleteImagesForUser(userId: String)

}