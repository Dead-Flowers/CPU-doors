package com.example.mobileauthenticatorjetpack

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import com.example.mobileauthenticatorjetpack.interfaces.ControllersListView
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
