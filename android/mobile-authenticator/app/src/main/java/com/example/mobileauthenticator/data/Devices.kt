package com.example.mobileauthenticator.data

import android.content.res.Resources

fun deviceList(resources: Resources): List<Device> {
    return listOf(
        Device(
            id = 1,
            name = "Garage door",
            state = DeviceStatus.CLOSED
        ),
        Device(
            id = 2,
            name = "Kitchen door",
            state = DeviceStatus.OPEN
        ),
    )
}