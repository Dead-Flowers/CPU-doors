package com.example.mobileauthenticatorjetpack.devicemanagement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.mobileauthenticatorjetpack.ui.theme.MobileAuthenticatorJetpackTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeviceManagementActivity : ComponentActivity(){
    private val deviceManagementViewModel: DeviceManagementViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MobileAuthenticatorJetpackTheme {
                DeviceManagementView(viewModel = deviceManagementViewModel)
            }
        }

    }

}