package com.example.mobileauthenticatorjetpack.interfaces

import android.os.Build
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobileauthenticatorjetpack.DeviceManagementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceManagementView(viewModel: DeviceManagementViewModel) {

    val maxNameLength = 40
    val defaultDeviceName = "${Build.MANUFACTURER.capitalize()} ${Build.MODEL.capitalize()}"

    var userPwd by remember { mutableStateOf("") }
    var userDefinedDeviceName by remember { mutableStateOf(defaultDeviceName) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                ),
                title = {
                    Text("Device registration")
                }
            )
        },
    ) {
         it -> Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(horizontal = 30.dp)
            ) {
                TextField(
                    value = userDefinedDeviceName,
                    onValueChange = {
                        if (it.length <= maxNameLength) {
                            userDefinedDeviceName = it
                        }
                    },
                    singleLine = true,
                    label = { Text("Device name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(5.dp))
                Text(text = "Verify password", fontSize = 18.sp)
                PasswordField(
                    value = userPwd,
                    onChange = { data -> userPwd = data },
                    submit = {
                        viewModel.addNewDevice(context, userPwd, userDefinedDeviceName)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(onClick = {
                    viewModel.addNewDevice(context, userPwd, userDefinedDeviceName)
                }, shape = RoundedCornerShape(5.dp)) {
                    Text(text = "Add new device")
                }
            }
        }
        DisposableEffect(Unit) {
            viewModel.isDeviceRegistered(context)
            onDispose {}
        }
}
