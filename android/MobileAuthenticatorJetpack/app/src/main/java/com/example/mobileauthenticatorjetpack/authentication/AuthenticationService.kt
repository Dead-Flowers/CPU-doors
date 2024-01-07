package com.example.mobileauthenticatorjetpack.authentication

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface RefreshTokenService {
    @POST("auth/token/refresh")
    suspend fun refreshToken(
        @Body body: RefreshRequest
    ): Response<RefreshNetworkResponse>
}

data class RefreshRequest(
    val refresh: String
)

interface AuthService {
    @POST("auth/token/pair")
    suspend fun login(
        @Body body: LoginRequest
    ): Response<AuthNetworkResponse>
}

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthNetworkResponse(
    val email: String,
    val refresh: String,
    val access: String
)

data class RefreshNetworkResponse(
    val refresh: String,
    val access: String
)

interface MeService {
    @GET("users/me")
    suspend fun getMe(): Response<MeResponse>
}

data class MeResponse(
    val id: String,
    val email: String
)