package com.example.knowitall.screens.login

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.knowitall.database.LoginDatabase
import com.example.knowitall.database.UserLogin

class LoginViewModel(private val context: Context) : ViewModel() {
    private val dao = LoginDatabase.getInstance(context).userLoginDao
    private val _email = MutableLiveData<String>()
    val email: LiveData<String>
        get() = _email
    private val _currentTime = MutableLiveData<String>()
    val currentTime: LiveData<String>
        get() = _currentTime
    private val _emails = MutableLiveData<List<String>>()
    val emails: LiveData<List<String>>
        get() = _emails
    private val _isValidEmail = MutableLiveData<Boolean>()
    val isValidEmail: LiveData<Boolean>
        get() = _isValidEmail
    init {
        _email.value = ""
        _currentTime.value = ""
        _emails.value = dao.getAllEmails()
        // insert 3 dummy users if the database is empty
        if (dao.getAllEmails().isEmpty()) {
            val userLogin1 = UserLogin("dummy@gmail.com", "01-01-1997 00:00:00", 0)
            val userLogin2 = UserLogin("dummy2@gmail.com", "01-01-1997 00:00:00", 0)
            val userLogin3 = UserLogin("dummy3@gmail.com", "01-01-1997 00:00:00", 0)
            dao.insert(userLogin1)
            dao.insert(userLogin2)
            dao.insert(userLogin3)
            _emails.value = dao.getAllEmails()
        }
    }
    override fun onCleared() {
        super.onCleared()
        Log.i("LoginViewModel", "LoginViewModel destroyed!")
    }

    fun getLatestLogin(): UserLogin? {
        return dao.getLatestLogin()
    }

    fun updateUserLogin(userLogin: UserLogin) {
        dao.update(userLogin)
    }

    fun validateEmail(email: String){
        _email.value = email
        val currentTime = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        val formattedTime = formatter.format(currentTime)
        _currentTime.value = formattedTime
        if (_email.value?.let { Patterns.EMAIL_ADDRESS.matcher(it).matches() } == true){
            _isValidEmail.value = true
            val userLogin = dao.get(email)
            if (userLogin != null) {
                userLogin.loginTimestamp = _currentTime.value?.let { it } ?: return
                dao.update(userLogin)
            } else {
                val newUserLogin = UserLogin(email, _currentTime.value?.let { it } ?: return, 0)
                dao.insert(newUserLogin)
            }
        }
        else{
            _isValidEmail.value = false
        }
    }
}