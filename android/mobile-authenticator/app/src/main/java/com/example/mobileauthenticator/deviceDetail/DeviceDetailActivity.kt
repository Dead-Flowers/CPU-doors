package com.example.mobileauthenticator.deviceDetail

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileauthenticator.R
import com.example.mobileauthenticator.devicesList.DEVICE_ID
import java.util.concurrent.Executor


class DeviceDetailActivity : AppCompatActivity() {
    private val deviceDetailViewModel by viewModels<DeviceDetailViewModel> {
        DeviceDetailViewModelFactory(this)
    }
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.device_detail_activity)
        var toolbar: Toolbar = findViewById(R.id.app_toolbar)
        setSupportActionBar(toolbar)

        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true);


        var currentDeviceId: Long? = null

        val deviceName: TextView = findViewById(R.id.device_detail_name)
        val deviceState: TextView = findViewById(R.id.device_detail_status)
        val changeStatusButton: Button = findViewById(R.id.change_status_button)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            currentDeviceId = bundle.getLong(DEVICE_ID)
        }

        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int,
                                                   errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(applicationContext,
                        "Authentication error: $errString", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(applicationContext,
                        "Authentication succeeded!", Toast.LENGTH_SHORT)
                        .show()

                    if (currentDeviceId != null) {
                        val newStatus = deviceDetailViewModel.updateStatus(currentDeviceId)
                        val deviceState: TextView = findViewById(R.id.device_detail_status)
                        deviceState.text = "Status: " + newStatus?.name
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .build()

        currentDeviceId?.let {
            val currentDevice = deviceDetailViewModel.getDeviceById(it)
            deviceName.text = currentDevice?.name
            deviceState.text = "Status: " + currentDevice?.state?.name

            changeStatusButton.setOnClickListener {
                if (currentDevice != null) {
                    biometricPrompt.authenticate(promptInfo)
                }
            }
        }
    }
}