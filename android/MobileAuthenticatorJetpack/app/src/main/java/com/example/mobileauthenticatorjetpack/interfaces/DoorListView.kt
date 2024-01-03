package com.example.mobileauthenticatorjetpack.interfaces

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobileauthenticatorjetpack.ControllersViewModel

@Composable
fun DoorViewList(viewModel: ControllersViewModel) {
    val context = LocalContext.current
    val doors = viewModel.controllers.value
    Surface {
        Button(onClick = {
            viewModel.getControllers(context)
        }) {
            Text(text = "Reload")
        }
        LazyColumn (
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 0.dp)
        ) {
            items(doors) {
                door -> DoorElement(id = door.id, name = door.givenName, state = door.state, viewModel, context)
            }
        }
        DisposableEffect(Unit) {
            viewModel.getControllers(context)
            onDispose {}
        }
    }
}

@Composable
fun DoorElement(
    id: String,
    name: String,
    state: String?,
    viewModel: ControllersViewModel,
    context: Context,
) {
    Row(
        Modifier
            .background(Color.LightGray, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp)
            .fillMaxWidth()
            .height(64.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = name, fontSize=24.sp)
        Spacer(Modifier.weight(1f))
        Switch(checked = state != null && state.lowercase() == "open", onCheckedChange = {
            viewModel.changeControllerState(context, id, it, it.not())
        })
    }
}