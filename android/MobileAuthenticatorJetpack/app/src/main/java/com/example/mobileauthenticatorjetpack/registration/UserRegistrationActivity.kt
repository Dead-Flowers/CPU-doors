package com.example.mobileauthenticatorjetpack.registration

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.mobileauthenticatorjetpack.ui.theme.MobileAuthenticatorJetpackTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserRegistrationActivity : ComponentActivity() {
    private val viewModel: UserRegistrationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MobileAuthenticatorJetpackTheme {
                UserRegistrationForm(viewModel)
            }
        }
    }

}