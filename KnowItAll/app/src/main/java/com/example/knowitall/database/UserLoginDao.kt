package com.example.knowitall.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserLoginDao {
    @Insert
    fun insert(userLogin: UserLogin)
    @Update
    fun update(userLogin: UserLogin)
    @Query("SELECT * FROM user_login_timestamp_table WHERE email = :key")
    fun get(key: String): UserLogin?
    @Query("DELETE FROM user_login_timestamp_table")
    fun clear()
    @Query("SELECT * FROM user_login_timestamp_table ORDER BY login_timestamp DESC LIMIT 1")
    fun getLatestLogin(): UserLogin?
    @Query("SELECT email FROM user_login_timestamp_table ORDER BY login_timestamp DESC")
    fun getAllEmails(): List<String>
}