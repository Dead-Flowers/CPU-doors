package com.example.mobileauthenticatorjetpack.registration

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class UserRegistrationViewModel @Inject constructor(
    private val registrationService: RegistrationService
) : ViewModel() {
    fun register(context: Context, registrationForm: RegistrationForm) {
        viewModelScope.launch {
            try {
                val response = registrationService.registerUser(RegisterUserDto(
                    registrationForm.email,
                    registrationForm.pwd,
                    registrationForm.confirmedPwd
                ))
                if (response.isSuccessful && response.body() != null) {
                    Toast.makeText(
                        context,
                        "Registration successful",
                        Toast.LENGTH_SHORT
                    ).show()

                    goBackToLogin(context)
                } else {
                    Toast.makeText(
                        context,
                        "Error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (err: Exception) {
                Toast.makeText(
                    context,
                    "Error",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun goBackToLogin(context: Context) {
        (context as Activity).finish()
    }
}