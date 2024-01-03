package com.example.mobileauthenticatorjetpack.authentication

import com.google.gson.annotations.SerializedName
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
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.Date
import javax.inject.Singleton

val BASE_URL = "http://192.168.1.17:8000/api/"

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
}

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

interface ControllersService {
    @GET("controllers")
    suspend fun getControllers(): Response<List<ControllerDto>>

    @GET("controllers/{id}")
    suspend fun getController(id: String): Response<ControllerDto>
}

data class ControllerDto (
    val id: String,
    val state: String?,
    @field:SerializedName("internal_name")
    val internalName: String,
    @field:SerializedName("given_name")
    val givenName: String,
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

    @Provides
    @Singleton
    fun provideDeviceApi(@AuthenticatedClient okHttpClient: OkHttpClient): DeviceService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(DeviceService::class.java)
    }

    @Provides
    @Singleton
    fun provideControllersApi(@AuthenticatedClient okHttpClient: OkHttpClient): ControllersService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(ControllersService::class.java)
    }


}