package com.example.mobileauthenticatorjetpack.registration

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RegistrationService {
    @POST("users/register")
    suspend fun registerUser(@Body body: RegisterUserDto): Response<RegisterUserResponse>
}

data class RegisterUserDto(
    val email: String,
    val password: String,
    @field:SerializedName("confirm_password")
    val confirmPassword: String
)

data class RegisterUserResponse(
    val id: String,
    val email: String
)