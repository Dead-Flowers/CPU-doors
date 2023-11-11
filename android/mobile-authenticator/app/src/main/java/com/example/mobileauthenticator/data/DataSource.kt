package com.example.mobileauthenticator.data

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class DataSource(resources: Resources) {
    private val initialDeviceList = deviceList(resources)
    private val devicesLiveData = MutableLiveData(initialDeviceList)

    fun addDevice(device: Device) {
        val currentList = devicesLiveData.value
        if (currentList == null) {
            devicesLiveData.postValue(listOf(device))
        } else {
            val updatedList = currentList.toMutableList()
            updatedList.add(0, device)
            devicesLiveData.postValue(updatedList)
        }
    }

    fun changeDevice(changedDevice: Device) {
        val currentList = devicesLiveData.value
        if (currentList == null) {
            devicesLiveData.postValue(listOf(changedDevice))
        } else {
            val updatedList = currentList.toMutableList()
            val currentDevice = getDeviceForId(changedDevice.id)
            val index = updatedList.indexOf(currentDevice)
            updatedList[index] = changedDevice
            devicesLiveData.postValue(updatedList)
        }
    }

    fun removeDevice(device: Device) {
        val currentList = devicesLiveData.value
        if (currentList != null) {
            val updatedList = currentList.toMutableList()
            updatedList.remove(device)
            devicesLiveData.postValue(updatedList)
        }
    }

    /* Returns flower given an ID. */
    fun getDeviceForId(id: Long): Device? {
        devicesLiveData.value?.let { devices ->
            return devices.firstOrNull{ it.id == id}
        }
        return null
    }

    fun getDevicesList(): LiveData<List<Device>> {
        return devicesLiveData
    }

    companion object {
        private var INSTANCE: DataSource? = null

        fun getDataSource(resources: Resources): DataSource {
            return synchronized(DataSource::class) {
                val newInstance = INSTANCE ?: DataSource(resources)
                INSTANCE = newInstance
                newInstance
            }
        }
    }
}