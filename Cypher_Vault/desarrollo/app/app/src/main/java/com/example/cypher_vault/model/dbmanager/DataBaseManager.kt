package com.example.cypher_vault.model.dbmanager

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.room.Room
import com.example.cypher_vault.database.AppDatabase
import com.example.cypher_vault.database.BlockedUsers
import com.example.cypher_vault.database.ImageDao
import com.example.cypher_vault.database.Images
import com.example.cypher_vault.database.ImagesLogin
import com.example.cypher_vault.database.ImagesRegister
import com.example.cypher_vault.database.User
import com.example.cypher_vault.database.UserIncome
import com.example.cypher_vault.database.UserPremium
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.security.SecureRandom


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

    fun getUserById(userId: String): User? {
        return database.userDao().getUserById(userId)
    }

    fun insertUser(user: User) {
        database.userDao().insert(user)
    }

    fun deleteUser(user: User) {
        database.userDao().delete(user)
    }

    suspend fun updateUserImage(userId: String, newImage: ByteArray) {
        withContext(Dispatchers.IO) {
            database.userDao().updateProfileImage(userId, newImage)
        }
    }

    // Métodos relacionados con la tabla de imágenes
    fun insertImage(image: Images) {
        database.imageDao().insertImage(image)
    }

    fun getImagesForUser(userId: String): List<Images> {
        return database.imageDao().getImagesForUser(userId)
    }

    // Métodos relacionados con la tabla de registros de imágenes
    fun insertImageRegister(imageRegister: ImagesRegister) {
        database.imageRegisterDao().insertImage(imageRegister)
    }

    fun getImageRegistersForImage(user_id: String): List<ImagesRegister> {
        return database.imageRegisterDao().getImagesForUser(user_id)
    }

    fun getLastImageRegisterForUser(user_id: String): ImagesRegister? {
        val imageRegisters = database.imageRegisterDao().getImagesForUser(user_id)
        return imageRegisters.lastOrNull()
    }


    fun insertImageLogin(imagesLogin: ImagesLogin) {
        database.imageLoginDao().insertImage(imagesLogin)
    }

    fun getImageLoginForImage(user_id: String): List<ImagesLogin> {
        return database.imageLoginDao().getImagesForUser(user_id)
    }

    fun deleteLoginImagesForUser(userId: String) {
        database.imageLoginDao().deleteImagesForUser(userId)
    }

    fun getLastImageLoginForUser(user_id: String): ImagesLogin? {
        val imagesLogin = database.imageLoginDao().getImagesForUser(user_id)
        return imagesLogin.lastOrNull()
    }

    suspend fun insertIncome(userIncome: UserIncome) {
        database.userIncomeDao().insertIncome(userIncome)
    }

    suspend fun getLastIncome(userId: String): List<UserIncome?> {
        return database.userIncomeDao().getLastIncome(userId)
    }

    suspend fun hasIncomeOnDay(userId: String,specificIncome: Long): Int {
        return database.userIncomeDao().hasIncomeOnDay(userId,specificIncome)
    }

    suspend fun getAllIncomes(userId: String): List<UserIncome?> {
        return database.userIncomeDao().getAllIncomes(userId)
    }

    suspend fun getLastFiveIncomes(userId: String): List<UserIncome?> {
        return database.userIncomeDao().getLastFiveIncomes(userId)
    }

    suspend fun insertPremiumActiveAccount(userPremium: UserPremium) {
        database.userPremiumDao().insertPremiumActiveAccount(userPremium)
    }

    suspend fun getPremiumActiveAccount(userId: String): UserPremium? {
        return database.userPremiumDao().getPremiumAccount(userId)
    }

    suspend fun updatePassword(uid: String, newPassword: String) {
        database.userDao().updatePassword(uid, newPassword)
    }

    suspend fun deleteUser(uid: String) {
        withContext(Dispatchers.IO) {
            database.userDao().deleteUserById(uid)
        }
    }

    suspend fun deleteImgUser(uid: String) {
        val batchSize = 10 // Número máximo de imágenes a procesar en cada lote
        val imageDao = database.imageDao()
        val images = imageDao.getImagesForUser(uid)

        images?.chunked(batchSize)?.forEach { batch ->
            processAndDeleteBatch(batch, imageDao)
        }
    }


    suspend fun processAndDeleteBatch(batch: List<Images>, imageDao: ImageDao) {
        val random = SecureRandom()
        batch.forEach { image ->
            try {
                // Sobrescribir la imagen con datos aleatorios
                val randomData = ByteArray(image.imageData.size)
                random.nextBytes(randomData)
                image.imageData = randomData
                imageDao.updateImage(image)

                // Borrar la imagen
                imageDao.deleteImage(image)

            } catch (e: Exception) {
                // Manejar cualquier excepción que ocurra durante el procesamiento de la imagen
                Log.e("Error", "Error processing image: ${e.message}")
            } finally {
                // Liberar memoria
                System.gc() // Llamar al recolector de basura para liberar la memoria no utilizada
            }
        }
    }

    suspend fun saveSecondAuth(userId: String, b: Boolean) {
        withContext(Dispatchers.IO) {
            database.userDao().saveSecondAuth(userId, b)
        }
    }

    suspend fun deleteImgs(selectedImageIds: MutableState<List<Long>>) {
        withContext(Dispatchers.IO) {
            val imageDao = database.imageDao()
            val random = SecureRandom()

            // Ejecutar las operaciones de forma concurrente
            val jobs = selectedImageIds.value.map { id ->
                async {
                    val image = imageDao.getImageById(id)
                    image?.let {
                        // Generar los datos aleatorios para cada imagen
                        val randomData = ByteArray(it.imageData.size)
                        random.nextBytes(randomData)

                        // Actualizar los datos de la imagen con los datos aleatorios
                        it.imageData = randomData
                        imageDao.updateImage(it)

                        // Eliminar la imagen
                        imageDao.deleteImage(it)
                    }
                }
            }

            // Esperar a que todas las operaciones terminen
            jobs.awaitAll()
        }
    }

    suspend fun insertBlockedUser(blockedUser: BlockedUsers) {
        database.blockedUsersDao().insertBlockedUser(blockedUser)
    }

    suspend fun getBlockedUser(userId: String): BlockedUsers? {
        return  database.blockedUsersDao().getBlockedUser(userId)
    }

    suspend fun getBlockedUsers(): List<BlockedUsers> {
        return database.blockedUsersDao().getAllBlockedUsers()
    }

    suspend fun deleteBlockedUser(userId: String) {
        database.blockedUsersDao().deleteBlockedUser(userId)
    }

    suspend fun getUserAttemps(userId: String): Int {
        return database.blockedUsersDao().getUserAttempts(userId)!!
    }

    suspend fun updateAttempts(userId: String, attempts: Int) {
        database.blockedUsersDao().updateUserAttempts(userId, attempts)
    }

    suspend fun setBlocked(userId: String, blocked: Boolean) {
        database.blockedUsersDao().setBlocked(userId, blocked)
    }

    suspend fun setBlockDate(userId: String, date: Long) {
        database.blockedUsersDao().setBlockDate(userId, date)
    }
}