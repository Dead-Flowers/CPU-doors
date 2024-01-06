package com.example.mobileauthenticatorjetpack.registration

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mobileauthenticatorjetpack.login.LoginField
import com.example.mobileauthenticatorjetpack.login.PasswordField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserRegistrationForm(viewModel: UserRegistrationViewModel) {
    var formData by remember { mutableStateOf(RegistrationForm()) }
    val context = LocalContext.current

    Surface {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = Color.White,
                    ),
                    title = {
                        Text("Registration")
                    }
                )
            },
        ) { innerPadding ->
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 30.dp)
                ) {
                    LoginField(
                        value = formData.email,
                        onChange = { data -> formData = formData.copy(email = data) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(5.dp))
                    PasswordField(
                        value = formData.pwd,
                        onChange = { data -> formData = formData.copy(pwd = data) },
                        submit = {},
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    PasswordField(
                        value = formData.confirmedPwd,
                        onChange = { data -> formData = formData.copy(confirmedPwd = data) },
                        submit = {},
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            if (!isRegistrationFormValid(formData)) {
                                Toast.makeText(
                                    context,
                                    "Invalid form data",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                viewModel.register(context, formData)
                            }
                        },
                        enabled = true,
                        shape = RoundedCornerShape(5.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Register")
                    }
                }
        }
    }
}

data class RegistrationForm(
    var email: String = "",
    var pwd: String = "",
    var confirmedPwd: String = ""
) {
    fun isNotEmpty(): Boolean {
        return email.isNotEmpty() && pwd.isNotEmpty() && confirmedPwd.isNotEmpty()
    }
}

fun isRegistrationFormValid(formData: RegistrationForm): Boolean {
    return formData.isNotEmpty() && formData.pwd == formData.confirmedPwd
}

