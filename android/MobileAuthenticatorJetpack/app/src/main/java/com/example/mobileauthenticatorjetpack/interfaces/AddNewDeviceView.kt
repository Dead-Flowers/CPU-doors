package com.example.mobileauthenticatorjetpack.interfaces

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobileauthenticatorjetpack.DeviceManagementViewModel

@Composable
fun AddNewDeviceView(viewModel: DeviceManagementViewModel) {
    var userPwd by remember { mutableStateOf("") }
    val context = LocalContext.current

    Surface {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp)
        ) {
            Text(text = "Verify password to add new device", fontSize = 18.sp)
            PasswordField(
                value = userPwd,
                onChange = { data -> userPwd = data },
                submit = {
                    viewModel.addNewDevice(context, userPwd)
                }
            )
            Button(onClick = {
                viewModel.addNewDevice(context, userPwd)
            }, shape = RoundedCornerShape(5.dp)) {
                Text(text = "Add new device")
            }
        }
    }
    DisposableEffect(Unit) {
        viewModel.isDeviceAlreadyAdded(context)
        onDispose {}
    }
}