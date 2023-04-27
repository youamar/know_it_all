package com.example.knowitall.screens.login

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.util.Log
import android.util.Patterns
import android.widget.Toast
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
    init {
        _email.value = ""
        _currentTime.value = ""
    }
    override fun onCleared() {
        super.onCleared()
        Log.i("LoginViewModel", "LoginViewModel destroyed!")
    }
    fun validateEmail(email: String){
        _email.value = email
        _currentTime.value = SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Calendar.getInstance().time)
        if (_email.value?.let { Patterns.EMAIL_ADDRESS.matcher(it).matches() } == true){
            Toast.makeText(context, "Valid !", Toast.LENGTH_SHORT).show()
            val userLogin = dao.get(email)
            if(userLogin != null) {
                userLogin.loginTimestamp = _currentTime.value!!
                dao.update(userLogin)
            } else {
                dao.insert(UserLogin(email, _currentTime.value!!))
            }
        }
        else{
            Toast.makeText(context, "Invalid !", Toast.LENGTH_SHORT).show()
        }
    }
    fun getEmails() {
        _emails.value = dao.getAllEmails()
    }
}