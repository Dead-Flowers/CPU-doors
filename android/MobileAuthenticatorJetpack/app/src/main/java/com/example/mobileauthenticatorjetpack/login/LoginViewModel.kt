package com.example.mobileauthenticatorjetpack.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobileauthenticatorjetpack.authentication.AuthService
import com.example.mobileauthenticatorjetpack.authentication.JwtTokenManager
import com.example.mobileauthenticatorjetpack.authentication.LoginRequest
import com.example.mobileauthenticatorjetpack.authentication.RefreshRequest
import com.example.mobileauthenticatorjetpack.authentication.RefreshTokenService
import com.example.mobileauthenticatorjetpack.devicemanagement.DeviceManagementActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val tokenManager: JwtTokenManager,
    private val authService: AuthService,
    private val refreshTokenService: RefreshTokenService
): ViewModel() {

    val loginFailed: MutableState<Boolean> = mutableStateOf(false);

    fun useRefreshToken(context: Context) {
        viewModelScope.launch {
            try {
                val refreshToken = tokenManager.getRefreshJwt();
                if (refreshToken != null) {
                    var response = refreshTokenService.refreshToken(RefreshRequest(refreshToken))
                    if (response.isSuccessful && response.body() != null) {
                        tokenManager.saveAccessJwt(response.body()!!.access);
                        tokenManager.saveRefreshJwt(response.body()!!.refresh);
                        context.startActivity(Intent(context, DeviceManagementActivity::class.java))
                        (context as Activity).finish()
                    }
                }
            } catch (err: Exception) {
                Toast.makeText(context, "Logged in", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun login(credentials: Credentials, context: Context) {
        viewModelScope.launch {
            try {
                var response = authService.login(LoginRequest(credentials.login, credentials.pwd))
                if (response.isSuccessful && response.body() != null) {
                    loginFailed.value = false;
                    tokenManager.saveAccessJwt(response.body()!!.access);
                    tokenManager.saveRefreshJwt(response.body()!!.refresh);
                    Toast.makeText(context, "Logged in", Toast.LENGTH_SHORT).show()
                    context.startActivity(Intent(context, DeviceManagementActivity::class.java))
                    (context as Activity).finish()
                } else {
                    loginFailed.value = true;
                    Toast.makeText(context, "Failed to log in", Toast.LENGTH_SHORT).show()
                }
            } catch (err: Exception) {
                Toast.makeText(context, "Error: $err", Toast.LENGTH_SHORT).show()
            }
        }
    }
}