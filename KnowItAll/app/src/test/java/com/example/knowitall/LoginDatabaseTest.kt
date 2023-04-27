package com.example.knowitall

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.knowitall.database.LoginDatabase
import com.example.knowitall.database.UserLogin
import com.example.knowitall.database.UserLoginDao
import org.junit.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
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
        val userLogin = UserLogin()
        loginDao.insert(userLogin)
        val latestLogin = loginDao.getLatestLogin()
        assertEquals(latestLogin?.loginTimestamp , -1)
    }
}
