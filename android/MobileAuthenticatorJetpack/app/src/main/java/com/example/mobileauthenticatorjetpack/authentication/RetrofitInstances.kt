package com.example.mobileauthenticatorjetpack.authentication

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import javax.inject.Singleton

val BASE_URL = "http://192.168.1.201:8000/api/"

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

@Module
@InstallIn(SingletonComponent::class)
class RetrofitClientModule {

    @Provides
    @Singleton
    fun provideRetrofit(@TokenRefreshClient okHttpClient: OkHttpClient): RefreshTokenService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(RefreshTokenService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthenticationApi(@PublicClient okHttpClient: OkHttpClient): AuthService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(AuthService::class.java)
    }

    @Provides
    @Singleton
    fun provideMeApi(@AuthenticatedClient okHttpClient: OkHttpClient): MeService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(MeService::class.java)
    }

}