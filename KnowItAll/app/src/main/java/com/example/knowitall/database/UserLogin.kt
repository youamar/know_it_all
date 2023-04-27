package com.example.knowitall.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_login_timestamp_table")
data class UserLogin(
    @PrimaryKey
    var email: String = "",
    @ColumnInfo(name = "login_timestamp")
    var loginTimestamp: String = ""
)

