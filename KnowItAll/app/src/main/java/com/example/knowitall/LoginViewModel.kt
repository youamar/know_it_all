package com.example.knowitall

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel(private val context: Context) : ViewModel() {
    private val _email = MutableLiveData<String>()
    val email: LiveData<String>
        get() = _email
    private val _currentTime = MutableLiveData<String>()
    val currentTime: LiveData<String>
        get() = _currentTime
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
        _currentTime.value = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().time)
        if (_email.value?.let { Patterns.EMAIL_ADDRESS.matcher(it).matches() } == true){
            Toast.makeText(context, "Valid !", Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(context, "Invalid !", Toast.LENGTH_SHORT).show()
        }
    }
}