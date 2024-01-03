package com.example.mobileauthenticatorjetpack.interfaces

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
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview(showBackground = true)
@Composable
fun DoorViewList() {
    val context = LocalContext.current
    val doors = listOf("Door 1", "Door 2", "Door 3", "Door 4")
    Surface {
        LazyColumn (
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 0.dp)
        ) {
            items(doors) {
                door -> DoorElement(name = door)
            }
        }
    }
}

@Composable
fun DoorElement(
    name: String
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
        Switch(checked = true, onCheckedChange = null)
    }
}