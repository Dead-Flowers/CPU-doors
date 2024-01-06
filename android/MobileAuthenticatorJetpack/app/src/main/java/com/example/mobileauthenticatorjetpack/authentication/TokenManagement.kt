package com.example.mobileauthenticatorjetpack.authentication

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


interface JwtTokenManager {
    suspend fun saveAccessJwt(token: String)
    suspend fun saveRefreshJwt(token: String)
    suspend fun saveDeviceId(id: String)
    suspend fun saveDeviceName(name: String)
    suspend fun getAccessJwt(): String?
    suspend fun getRefreshJwt(): String?
    suspend fun getDeviceId(): String?
    suspend fun getDeviceName(): String?
    suspend fun clearAllTokens()
    suspend fun clearUserTokens()
    suspend fun clearDevice()
}

class JwtTokenDataStore @Inject constructor(private val dataStore: DataStore<Preferences>) :
    JwtTokenManager {

    companion object {
        val ACCESS_JWT_KEY = stringPreferencesKey("access_jwt")
        val REFRESH_JWT_KEY = stringPreferencesKey("refresh_jwt")
        val DEVICE_ID = stringPreferencesKey("device_id")
        val DEVICE_NAME = stringPreferencesKey("device_name")
    }

    override suspend fun saveAccessJwt(token: String) {
        dataStore.edit { preferences ->
            preferences[ACCESS_JWT_KEY] = token
        }
    }

    override suspend fun saveRefreshJwt(token: String) {
        dataStore.edit { preferences ->
            preferences[REFRESH_JWT_KEY] = token
        }
    }

    override suspend fun saveDeviceId(id: String) {
        dataStore.edit { preferences ->
            preferences[DEVICE_ID] = id
        }
    }

    override suspend fun saveDeviceName(name: String) {
        dataStore.edit { preferences ->
            preferences[DEVICE_NAME] = name
        }
    }

    override suspend fun getAccessJwt(): String? {
        return dataStore.data.map { preferences ->
            preferences[ACCESS_JWT_KEY]
        }.first()
    }

    override suspend fun getRefreshJwt(): String? {
        return dataStore.data.map { preferences ->
            preferences[REFRESH_JWT_KEY]
        }.first()
    }

    override suspend fun getDeviceId(): String? {
        return dataStore.data.map { preferences ->
            preferences[DEVICE_ID]
        }.first()
    }

    override suspend fun getDeviceName(): String? {
        return dataStore.data.map { preferences ->
            preferences[DEVICE_NAME]
        }.first()
    }

    override suspend fun clearAllTokens() {
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_JWT_KEY)
            preferences.remove(REFRESH_JWT_KEY)
            preferences.remove(DEVICE_ID)
        }
    }

    override suspend fun clearUserTokens() {
        dataStore.edit { preferences ->
            preferences.remove(ACCESS_JWT_KEY)
            preferences.remove(REFRESH_JWT_KEY)
        }
    }

    override suspend fun clearDevice() {
        dataStore.edit { preferences ->
            preferences.remove(DEVICE_ID)
            preferences.remove(DEVICE_NAME)
        }
    }
}

@Module
@InstallIn(SingletonComponent::class)
class TokenManagementModule {
    @[Provides Singleton]
    fun provideDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> {
        val AUTH_PREFERENCES = "auth-preferences"
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            produceFile = { appContext.preferencesDataStoreFile(AUTH_PREFERENCES) }
        )
    }

    @[Provides Singleton]
    fun provideJwtTokenManager(dataStore: DataStore<Preferences>): JwtTokenManager {
        return JwtTokenDataStore(dataStore = dataStore)
    }


}