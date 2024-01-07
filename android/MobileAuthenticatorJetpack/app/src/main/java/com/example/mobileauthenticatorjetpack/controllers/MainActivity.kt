package com.example.mobileauthenticatorjetpack.controllers

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import com.example.mobileauthenticatorjetpack.ui.theme.MobileAuthenticatorJetpackTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    private val controllersViewModel: ControllersViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MobileAuthenticatorJetpackTheme {
                ControllersListView(viewModel = controllersViewModel)
            }
        }
    }
}
