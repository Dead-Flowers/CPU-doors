package com.example.mobileauthenticatorjetpack.devicemanagement

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DeviceService {
    @GET("users/devices")
    suspend fun getDevices(): Response<List<DeviceDto>>

    @GET("users/devices/{deviceId}")
    suspend fun getDevice(deviceId: String): Response<DeviceDto>

    @POST("users/devices")
    suspend fun addDevice(@Body body: AddDeviceRequest): Response<DeviceDto>

//    @DELETE("users/devices/{deviceId}")
//    suspend fun deleteDevice(deviceId: String): Void

    //TODO: add remove device with POST
    @POST("users/devices/{id}/remove")
    suspend fun removeDevice(@Path(value = "id") id: String, @Body body: RemoveDeviceDto): Response<Void>

    @GET("users/devices/{id}/challenge")
    suspend fun challengeDevice(@Path(value = "id") id: String): Response<ChallengeDeviceDto>
}

data class ChallengeDeviceDto(
    @field:SerializedName("challenge_token")
    val challengeToken: String
)

data class RemoveDeviceDto(
    @field:SerializedName("challenge_token")
    val challengeToken: String,
    @field:SerializedName("challenge_token_signature")
    val challengeTokenSignature: String
)

data class DeviceDto(
    val id: String,
    val name: String,
    @field:SerializedName("created_at")
    val createdAt: String
)

data class AddDeviceRequest(
    @field:SerializedName("user_password")
    val userPassword: String,
    @field:SerializedName("device_name")
    val deviceName: String,
    @field:SerializedName("device_public_key")
    val devicePublicKey: String
)
