package com.example.knowitall

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.knowitall.database.LoginDatabase
import com.example.knowitall.database.UserLogin
import com.example.knowitall.database.UserLoginDao
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import java.io.IOException

/**
 * This is not meant to be a full set of tests. For simplicity, most of your samples do not
 * include tests. However, when building the Room, it is helpful to make sure it works before
 * adding the UI.
 */
@RunWith(AndroidJUnit4::class)
class LoginDatabaseTest {

    private lateinit var loginDao: UserLoginDao
    private lateinit var db: LoginDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, LoginDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        loginDao = db.userLoginDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetLogin() {
        val currentTime = SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Calendar.getInstance().time)
        val userLogin = UserLogin("54915@etu.he2b.be",currentTime)
        loginDao.insert(userLogin)
        val latestLogin = loginDao.getLatestLogin()
        assertEquals(latestLogin?.loginTimestamp , currentTime)
    }
}