package com.example.mobileauthenticatorjetpack.controllers.details

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.mobileauthenticatorjetpack.controllers.CONTROLLER_ID
import com.example.mobileauthenticatorjetpack.ui.theme.MobileAuthenticatorJetpackTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ControllerDetailsActivity: ComponentActivity() {

    private val controllerDetailsViewModel: ControllerDetailsViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var currentControllerId: String? = null

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            currentControllerId = bundle.getString(CONTROLLER_ID)
        }

        setContent {
            MobileAuthenticatorJetpackTheme {
                ControllerDetailsView(viewModel = controllerDetailsViewModel, currentControllerId = currentControllerId)
            }
        }
    }
}