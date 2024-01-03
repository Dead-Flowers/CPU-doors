package com.example.mobileauthenticatorjetpack

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobileauthenticatorjetpack.authentication.AuthService
import com.example.mobileauthenticatorjetpack.authentication.JwtTokenManager
import com.example.mobileauthenticatorjetpack.authentication.LoginRequest
import com.example.mobileauthenticatorjetpack.interfaces.Credentials
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val tokenManager: JwtTokenManager,
    private val authService: AuthService
): ViewModel() {

    fun login(credentials: Credentials, context: Context) {
        viewModelScope.launch {
            try {
                var response = authService.login(LoginRequest(credentials.login, credentials.pwd))
                if (response.isSuccessful && response.body() != null) {
//                    val tokenManager = provideJwtTokenManager(dataStore);
                    tokenManager.saveAccessJwt(response.body()!!.access);
                    tokenManager.saveRefreshJwt(response.body()!!.refresh);
                    Toast.makeText(context, "Logged in", Toast.LENGTH_SHORT).show()
                    context.startActivity(Intent(context, MainActivity::class.java))
                    (context as Activity).finish()
                }
            } catch (err: Exception) {
                Toast.makeText(context, "Error: $err", Toast.LENGTH_SHORT).show()
            }
        }
    }
}