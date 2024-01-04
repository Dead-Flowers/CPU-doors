package com.example.mobileauthenticatorjetpack

import android.content.Context
import android.hardware.fingerprint.FingerprintManager
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobileauthenticatorjetpack.authentication.ControllerDto
import com.example.mobileauthenticatorjetpack.authentication.ControllersService
import com.example.mobileauthenticatorjetpack.authentication.DeviceService
import com.example.mobileauthenticatorjetpack.authentication.JwtTokenManager
import com.example.mobileauthenticatorjetpack.authentication.SetControllerStateDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.Signature
import java.util.concurrent.Executor
import javax.inject.Inject


@HiltViewModel
class ControllersViewModel @Inject constructor(
    private val controllersService: ControllersService,
    private val deviceService: DeviceService,
    private val jwtTokenManager: JwtTokenManager
) : ViewModel() {

    var controllers: MutableState<List<ControllerDto>> = mutableStateOf(emptyList())

    fun getControllers(context: Context) {
        viewModelScope.launch {
            try {
                var response = controllersService.getControllers();
                if (response.isSuccessful && response.body() != null) {
                    controllers.value = response.body()!!
                } else {
                    Toast.makeText(
                        context,
                        "Unable to fetch controllers",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (err: Exception) {
                Toast.makeText(
                    context,
                    "Unable to fetch controllers: $err",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun changeControllerState(context: Context, controllerId: String, currentState: Boolean, newState: Boolean) {
        viewModelScope.launch {
            var biometricPrompt: BiometricPrompt
            var promptInfo: BiometricPrompt.PromptInfo
            var executor: Executor

            executor = ContextCompat.getMainExecutor(context)

            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            val key = keyStore.getKey("myKeyAlias", null) as PrivateKey

            biometricPrompt = BiometricPrompt(context as FragmentActivity, executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int,
                                                       errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        Toast.makeText(context,
                            "Authentication error: $errString", Toast.LENGTH_SHORT)
                            .show()
                    }

                    override fun onAuthenticationSucceeded(
                        result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)

                        var signature = result.cryptoObject!!.signature!!
                        if (signature == null) {
                            Toast.makeText(context,
                                "DUPA!", Toast.LENGTH_SHORT)
                                .show()
                        }

                        viewModelScope.launch {
                            try {
                                val deviceId = jwtTokenManager.getDeviceId()
                                if (deviceId == null) {
                                    Toast.makeText(context,
                                        "Failed to get device ID!", Toast.LENGTH_SHORT)
                                        .show()
                                }
                                var response = deviceService.challengeDevice(deviceId!!) // TODO
                                if (response.isSuccessful && response.body() != null) {
                                    val token = response.body()!!.challengeToken;

                                    signature.update(token.encodeToByteArray())
                                    var signedToken = Base64.encodeToString(signature.sign(), Base64.NO_WRAP)

                                    var signedResponse = controllersService.setControllerState(controllerId, SetControllerStateDto(
                                        token, signedToken, getState(currentState), getState(newState)
                                    ))
                                    if (signedResponse.isSuccessful && signedResponse.body() != null) {
                                        getControllers(context)
                                        Toast.makeText(context,
                                            "Authentication succeeded!", Toast.LENGTH_SHORT)
                                            .show()
                                    } else {
                                        Toast.makeText(context,
                                            "Failed to validate signed token!", Toast.LENGTH_SHORT)
                                            .show()
                                    }


                                } else {
                                    Toast.makeText(context,
                                        "Failed to download challenge token!", Toast.LENGTH_SHORT)
                                        .show()
                                }

                            } catch (err: Exception) {
                                Toast.makeText(context,
                                    "Failed auth: $err!", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        Toast.makeText(context, "Authentication failed",
                            Toast.LENGTH_SHORT)
                            .show()
                    }
                })


        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .build()

            var signature = Signature.getInstance("SHA256withRSA")
            signature.initSign(key)
            val cryptoObject: BiometricPrompt.CryptoObject = BiometricPrompt.CryptoObject(signature)

            biometricPrompt.authenticate(promptInfo, cryptoObject)
        }
    }

    private fun getState(state: Boolean): String {
        return if (state) {
            "open"
        } else {
            "closed"
        }
    }
}