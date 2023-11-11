package com.example.mobileauthenticator.devicesList

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mobileauthenticator.data.DataSource
import com.example.mobileauthenticator.data.Device
import com.example.mobileauthenticator.data.DeviceStatus
import kotlin.random.Random

class DevicesListViewModel(val dataSource: DataSource) : ViewModel() {
    val devicesLiveData = dataSource.getDevicesList()

    fun insertDevice(deviceName: String?) {
        if (deviceName == null) {
            return
        }

        val newDevice = Device(
            Random.nextLong(),
            deviceName,
            DeviceStatus.CLOSED
        )

        dataSource.addDevice(newDevice)
    }
}

class DevicesListViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DevicesListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DevicesListViewModel(
                dataSource = DataSource.getDataSource(context.resources)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}