package com.example.mobileauthenticatorjetpack

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobileauthenticatorjetpack.authentication.MeService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MeViewModel @Inject constructor(
    private val meService: MeService
) : ViewModel() {

    val id: MutableState<String> = mutableStateOf("")

    fun getId(context: Context) {
        viewModelScope.launch {
            try {
                var response = meService.getMe();
                if (response.isSuccessful && response.body() != null) {
                    id.value = response.body()!!.id
                }
            } catch (err: Exception) {
                Toast.makeText(context, "Error: $err", Toast.LENGTH_SHORT).show()
            }

        }
    }
}