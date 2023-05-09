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
    }
    override fun onCleared() {
        super.onCleared()
        Log.i("LoginViewModel", "LoginViewModel destroyed!")
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
                val userLogin = UserLogin(email, _currentTime.value?.let { it } ?: return)
                dao.insert(userLogin)
            }
        }
        else{
            _isValidEmail.value = false
        }
    }
}