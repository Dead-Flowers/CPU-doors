package com.example.mobileauthenticatorjetpack.controllers.details

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobileauthenticatorjetpack.authentication.JwtTokenManager
import com.example.mobileauthenticatorjetpack.controllers.ControllerEventDto
import com.example.mobileauthenticatorjetpack.controllers.ControllersService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ControllerDetailsViewModel @Inject constructor(
    private val controllersService: ControllersService,
    private val jwtTokenManager: JwtTokenManager
) : ViewModel() {

    val controllerEvents: MutableState<List<ControllerEventDto>> = mutableStateOf(emptyList())
    fun getControllerEvents(context: Context, currentControllerId: String?) {
        viewModelScope.launch {
            if (currentControllerId == null) {
                Toast.makeText(
                    context,
                    "Missing selected current controller",
                    Toast.LENGTH_SHORT
                ).show()
                return@launch
            }
            try {
                val response = controllersService.getControllerEvents(currentControllerId);
                if (response.isSuccessful && response.body() != null) {
                    controllerEvents.value = response.body()!!
                } else {
                    Toast.makeText(
                        context,
                        "Unable to fetch controller events",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (err: Exception) {
                Toast.makeText(
                    context,
                    "Unable to fetch controller events: $err",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun onBack(context: Context) {
        (context as Activity).finish()
    }
}