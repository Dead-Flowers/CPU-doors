package com.example.mobileauthenticatorjetpack

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobileauthenticatorjetpack.authentication.AddDeviceRequest
import com.example.mobileauthenticatorjetpack.authentication.DeviceDto
import com.example.mobileauthenticatorjetpack.authentication.DeviceService
import com.example.mobileauthenticatorjetpack.authentication.JwtTokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.util.Base64
import javax.inject.Inject

@HiltViewModel
class DeviceManagementViewModel @Inject constructor(
    private val deviceService: DeviceService,
    private val jwtTokenManager: JwtTokenManager
) : ViewModel() {

    val currentDeviceId: MutableState<String?> = mutableStateOf(null);

    fun isDeviceRegistered(context: Context) {
        viewModelScope.launch {
            try {
                val currentDeviceName = jwtTokenManager.getDeviceName()
                if (currentDeviceName == null) {
                    return@launch
                }
                var response = deviceService.getDevices();
                if (response.isSuccessful && response.body() != null) {
                    var devices = response.body()!!;
                    var foundDevice: DeviceDto?;
                    try {
                        foundDevice = devices.first { dev -> dev.name == currentDeviceName }
                        currentDeviceId.value = foundDevice.id;
                        jwtTokenManager.saveDeviceId(foundDevice.id)
                        context.startActivity(Intent(context, MainActivity::class.java))
                        (context as Activity).finish()
                        Toast.makeText(
                            context,
                            "Device verified: ${foundDevice.name}",
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (_: NoSuchElementException) {}
                } else {
                    Toast.makeText(
                        context,
                        "Unable to verify device: Connection error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (err: Exception) {
                Toast.makeText(
                    context,
                    "Unable to verify device: $err",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun addNewDevice(context: Context, userPwd: String, userDefinedDeviceName: String) {
        val keyPair = generateNewKeyPair();

        viewModelScope.launch {
            try {
                val response = deviceService.addDevice(AddDeviceRequest(
                    userPwd,
                    userDefinedDeviceName,
                    Base64.getEncoder().encodeToString(keyPair.public.encoded)
                ))
                if (response.isSuccessful && response.body() != null) {
                    currentDeviceId.value = response.body()!!.id
                    jwtTokenManager.saveDeviceId(response.body()!!.id)
                    jwtTokenManager.saveDeviceName(userDefinedDeviceName);
                    context.startActivity(Intent(context, MainActivity::class.java))
                    (context as Activity).finish()
                    Toast.makeText(
                        context,
                        "New device added: $userDefinedDeviceName",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        "Unable to register new device: $userDefinedDeviceName : ${response.errorBody().toString()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (err: Exception) {
                Toast.makeText(
                    context,
                    "Unable to add new device: $err",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun generateNewKeyPair(): KeyPair {
        val keyGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore")
        val keyGenParameterSpec = KeyGenParameterSpec.Builder("myKeyAlias", KeyProperties.PURPOSE_SIGN)
            .setDigests(KeyProperties.DIGEST_SHA256)
            .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
            .setUserAuthenticationRequired(true)
            .build()

        keyGenerator.initialize(keyGenParameterSpec)
        return keyGenerator.generateKeyPair()
    }

}