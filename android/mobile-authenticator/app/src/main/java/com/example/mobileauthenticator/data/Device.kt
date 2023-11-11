package com.example.mobileauthenticator.data

enum class DeviceStatus {
   OPEN, CLOSED
}

data class Device (
    val id: Long,
    val name: String,
    var state: DeviceStatus,
)