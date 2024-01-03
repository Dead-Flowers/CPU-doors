package com.example.mobileauthenticatorjetpack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.mobileauthenticatorjetpack.interfaces.AddNewDeviceView
import com.example.mobileauthenticatorjetpack.ui.theme.MobileAuthenticatorJetpackTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeviceManagementActivity : ComponentActivity(){
    private val deviceManagementViewModel: DeviceManagementViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MobileAuthenticatorJetpackTheme {
                AddNewDeviceView(viewModel = deviceManagementViewModel)
            }
        }

    }

}