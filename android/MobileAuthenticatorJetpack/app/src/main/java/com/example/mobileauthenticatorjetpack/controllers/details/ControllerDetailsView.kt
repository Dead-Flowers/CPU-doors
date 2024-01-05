package com.example.mobileauthenticatorjetpack.controllers.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControllerDetailsView(
    viewModel: ControllerDetailsViewModel,
    currentControllerId: String?
) {
    val context = LocalContext.current
    val events = viewModel.controllerEvents.value

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                ),
                title = {
                    Text("Controller events")
                },
                navigationIcon = {
                    IconButton(onClick = {viewModel.onBack(context)}) {
                        Icon(Icons.Rounded.ArrowBack, "")
                    }
                }
            )
        },
    ) {
        innerPadding -> LazyColumn (
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 0.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            if (events.isEmpty()) {
                Text(
                    text = "No events",
                    fontSize = 18.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 10.dp)
                )
            }
        }
        items(events) {
                event -> EventElement(event.id, event.date, event.invokingDevice, event.state)
        }
    }
        DisposableEffect(Unit) {
            viewModel.getControllerEvents(context, currentControllerId)
            onDispose {}
        }
    }

}

@Composable
fun EventElement(
    eventId: String,
    date: String,
    invokingDevice: String?,
    state: String
) {
    Row(
        Modifier
            .background(Color.hsl(0f, 0f, 0.85f, 1f), RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp)
            .fillMaxWidth()
            .height(72.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(text = invokingDevice ?: "EXTERNAL", fontSize=18.sp)
            Text(text = date, fontSize = 12.sp)
            Text(text = state, fontSize = 18.sp)
        }
    }
}