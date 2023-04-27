package com.example.knowitall.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserLogin::class], version = 1, exportSchema = false)
abstract class LoginDatabase : RoomDatabase() {

    abstract val userLoginDao: UserLoginDao

    companion object {

        @Volatile
        private var INSTANCE: LoginDatabase? = null

        fun getInstance(context: Context): LoginDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        LoginDatabase::class.java,
                        "user_login_timestamp_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}