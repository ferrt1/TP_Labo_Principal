package com.example.cypher_vault.database

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [User::class, Images::class, ImagesRegister::class, ImagesLogin::class, UserIncome::class, UserPremium::class, BlockedUsers::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun imageDao(): ImageDao
    abstract fun imageRegisterDao(): ImageRegisterDao
    abstract fun imageLoginDao(): ImageLoginDao
    abstract fun userIncomeDao(): UserIncomeDao
    abstract fun userPremiumDao(): UserPremiumDao
    abstract fun blockedUsersDao(): BlockedUsersDao
}