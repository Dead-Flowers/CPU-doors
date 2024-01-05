package com.example.mobileauthenticatorjetpack.controllers

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ControllersService {
    @GET("controllers")
    suspend fun getControllers(): Response<List<ControllerDto>>

    @GET("controllers/{id}")
    suspend fun getController(@Path(value = "id") id: String): Response<ControllerDto>

    @GET("controllers/{id}/events")
    suspend fun getControllerEvents(@Path(value = "id") id: String): Response<List<ControllerEventDto>>

    @POST("controllers/{id}/set-state")
    suspend fun setControllerState(@Path(value = "id") id: String, @Body body: SetControllerStateDto): Response<Void>
}

data class SetControllerStateDto(
    @field:SerializedName("challenge_token")
    val challengeToken: String,
    @field:SerializedName("challenge_token_signature")
    val challengeTokenSignature: String,
    @field:SerializedName("previous_state")
    val previousState: String,
    @field:SerializedName("new_state")
    val newState: String,
)

data class ControllerDto (
    val id: String,
    val state: String?,
    @field:SerializedName("internal_name")
    val internalName: String,
    @field:SerializedName("given_name")
    val givenName: String,
    @field:SerializedName("is_online")
    val isOnline: Boolean
)

data class ControllerEventDto(
    val id: String,
    @field:SerializedName("invoking_device")
    val invokingDevice: String?,
    val date: String,
    val state: String
)