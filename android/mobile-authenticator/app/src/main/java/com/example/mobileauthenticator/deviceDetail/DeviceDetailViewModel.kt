package com.example.mobileauthenticator.deviceDetail

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mobileauthenticator.data.DataSource
import com.example.mobileauthenticator.data.Device
import com.example.mobileauthenticator.data.DeviceStatus

class DeviceDetailViewModel(private val dataSource: DataSource): ViewModel() {
    fun getDeviceById(id: Long): Device? {
        return dataSource.getDeviceForId(id)
    }

    fun removeDevice(device: Device) {
        dataSource.removeDevice(device)
    }

    fun updateStatus(id: Long) : DeviceStatus? {
        val device: Device? = dataSource.getDeviceForId(id)
        if (device == null) return null
        if (device.state == DeviceStatus.OPEN) {
            device.state = DeviceStatus.CLOSED
        } else {
            device.state = DeviceStatus.OPEN
        }
        dataSource.changeDevice(device)
        return device.state
    }
}

class DeviceDetailViewModelFactory(private val context: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeviceDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DeviceDetailViewModel(
                dataSource = DataSource.getDataSource(context.resources)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}