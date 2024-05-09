package com.example.cypher_vault.model.dbmanager

import com.example.cypher_vault.MainActivity
import com.example.cypher_vault.database.User
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.UUID

class DatabaseManagerTest {

    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    @get:Before
    //usuario1
    val uid = generateUID()
    val uid1 = uid
    val name = "Alejandro1"
    val email = "spectrelonewolf1@gmail.com"
    val user1 = User(uid = uid, firstName = name, email = email, entryDate = System.currentTimeMillis(), pin = null)
    val db = DatabaseManager.insertUser(user1)

    private fun generateUID(): String {
       return UUID.randomUUID().toString()
    }

    @Test
    fun getAllUsers() {

    }

    @Test
    fun getUserById() {
        val user2 = DatabaseManager.getUserById(uid1)
        assertEquals(user2, user1)
    }

    @Test
    fun insertUser() {

    }

    @Test
    fun deleteUser() {
    }

    @Test
    fun insertImage() {
    }

    @Test
    fun getImagesForUser() {
    }

    @Test
    fun insertImageRegister() {
    }

    @Test
    fun testGetImagesForUser() {
    }

    @Test
    fun updateImage() {
    }

    @Test
    fun updateFaceContours() {
    }

    @Test
    fun updateFaceLandmarks() {
    }

    @Test
    fun getFaceContoursForUser() {
    }

    @Test
    fun getFaceLandmarksForUser() {
    }
}