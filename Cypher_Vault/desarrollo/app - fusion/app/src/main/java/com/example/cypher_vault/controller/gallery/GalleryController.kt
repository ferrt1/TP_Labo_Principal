package com.example.cypher_vault.controller.gallery


import androidx.compose.runtime.mutableStateOf
import com.example.cypher_vault.database.Images
import com.example.cypher_vault.model.dbmanager.DatabaseManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext


class GalleryController() {

    fun saveImage(imageData: ByteArray, userId: String): Deferred<Unit> {
        return CoroutineScope(Dispatchers.IO).async {
            val image = Images(imageData = imageData, user_id = userId)
            DatabaseManager.insertImage(image)
        }
    }

    val images = mutableStateOf<List<Images>>(listOf())

    suspend fun loadImagesForUser(userId: String) {
        images.value = withContext(Dispatchers.IO) {
            DatabaseManager.getImagesForUser(userId)
        }
    }
}