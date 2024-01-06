package com.example.mobileauthenticatorjetpack.controllers

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControllersListView(viewModel: ControllersViewModel) {
    val context = LocalContext.current
    val doors = viewModel.controllers.value
    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                ),
                title = {
                    Text("Controllers")
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.onRefreshButtonClicked(context)
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            tint = Color.White,
                            contentDescription = null
                        )
                    }
                    IconButton(onClick = { menuExpanded = !menuExpanded }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "More"
                        )
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
//                        DropdownMenuItem(text = { Text("Remove Device") }, onClick = { /*TODO*/ })
                        DropdownMenuItem(text = { Text("Logout") }, onClick = { viewModel.logoutUser(context) })
                    }
                },
            )
        },
    ) {
        innerPadding -> LazyColumn (
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 0.dp)
    ) {
        items(doors) {
                door -> ControllerElement(controllerId = door.id, name = door.givenName, state = door.state, stateChangeEnabled = door.isOnline, viewModel, context)
        }
    }
        DisposableEffect(Unit) {
            viewModel.getControllers(context)
            onDispose {}
        }
    }
}

@Composable
fun ControllerElement(
    controllerId: String,
    name: String,
    state: String?,
    stateChangeEnabled: Boolean,
    viewModel: ControllersViewModel,
    context: Context,
) {
    Row(
        Modifier
            .background(MaterialTheme.colorScheme.inverseOnSurface, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp)
            .fillMaxWidth()
            .height(96.dp)
            .clickable(onClick = { viewModel.goToDetails(context, controllerId) }),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = name, fontSize=24.sp)
        Spacer(Modifier.weight(1f))
        if (!stateChangeEnabled) {
            Text(text = "Device disabled", fontSize = 14.sp)
        }
        Switch(
            checked = state != null && state.lowercase() == "open",
            onCheckedChange = {
                viewModel.changeControllerState(context, controllerId, it.not(), it)
            },
            enabled = stateChangeEnabled
        )
    }
}