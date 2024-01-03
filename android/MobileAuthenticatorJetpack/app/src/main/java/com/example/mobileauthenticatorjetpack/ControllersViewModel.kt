package com.example.mobileauthenticatorjetpack

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobileauthenticatorjetpack.authentication.ControllerDto
import com.example.mobileauthenticatorjetpack.authentication.ControllersService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.security.KeyPairGenerator
import java.util.concurrent.Executor
import javax.inject.Inject

@HiltViewModel
class ControllersViewModel @Inject constructor(
    private val controllersService: ControllersService
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
        var biometricPrompt: BiometricPrompt
        var promptInfo: BiometricPrompt.PromptInfo
        var executor: Executor

        executor = ContextCompat.getMainExecutor(context)

        val keyGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore")
        val keyGenParameterSpec = KeyGenParameterSpec.Builder("myKeyAlias", KeyProperties.PURPOSE_SIGN)
            .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
            .setUserAuthenticationRequired(true)
            .build()

        keyGenerator.initialize(keyGenParameterSpec)
        val keyPair = keyGenerator.generateKeyPair()

        val fragmentActivity = context as FragmentActivity;

        biometricPrompt = BiometricPrompt(fragmentActivity, executor,
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

                    Toast.makeText(context,
                        "Authentication succeeded!", Toast.LENGTH_SHORT)
                        .show()
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

        biometricPrompt.authenticate(promptInfo)
    }
}