package com.example.mobileauthenticator.devicesList

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileauthenticator.R
import com.example.mobileauthenticator.data.Device
import com.example.mobileauthenticator.deviceDetail.DeviceDetailActivity

const val DEVICE_ID = "device id"

class DevicesListActivity : AppCompatActivity() {
    private val newDeviceActivityRequestCode = 1
    private val devicesListViewModel by viewModels<DevicesListViewModel> {
        DevicesListViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.app_toolbar))

        val devicesAdapter = DevicesAdapter { device -> adapterOnClick(device) }

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = devicesAdapter

        devicesListViewModel.devicesLiveData.observe(this) {
            it?.let {
                devicesAdapter.submitList(it as MutableList<Device>)
            }
        }

        val fab: View = findViewById(R.id.fab)
//        fab.setOnClickListener { fabOnClick }
    }



    private fun adapterOnClick(device: Device) {
        val intent = Intent(this, DeviceDetailActivity()::class.java)
        intent.putExtra("id", "1")
        intent.putExtra(DEVICE_ID, device.id)
        startActivity(intent)
    }
}