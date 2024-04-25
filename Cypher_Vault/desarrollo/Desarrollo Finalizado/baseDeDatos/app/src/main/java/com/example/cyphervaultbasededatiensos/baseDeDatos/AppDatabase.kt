package com.example.cyphervaultbasededatiensos.baseDeDatos

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [User::class, Images::class, ImagesRegister::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun imageDao(): ImageDao
    abstract fun imageRegisterDao(): ImageRegisterDao
}