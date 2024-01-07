package com.example.mobileauthenticatorjetpack.retrofit

import com.example.mobileauthenticatorjetpack.authentication.AuthService
import com.example.mobileauthenticatorjetpack.authentication.AuthenticatedClient
import com.example.mobileauthenticatorjetpack.authentication.PublicClient
import com.example.mobileauthenticatorjetpack.authentication.RefreshTokenService
import com.example.mobileauthenticatorjetpack.authentication.TokenRefreshClient
import com.example.mobileauthenticatorjetpack.controllers.ControllersService
import com.example.mobileauthenticatorjetpack.devicemanagement.DeviceService
import com.example.mobileauthenticatorjetpack.registration.RegistrationService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

val BASE_URL = "http://192.168.1.201:8000/api/"

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
    fun provideRegisterApi(@AuthenticatedClient okHttpClient: OkHttpClient): RegistrationService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(RegistrationService::class.java)
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