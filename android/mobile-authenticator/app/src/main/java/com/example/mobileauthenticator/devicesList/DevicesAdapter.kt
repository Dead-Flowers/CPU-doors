package com.example.mobileauthenticator.devicesList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileauthenticator.R
import com.example.mobileauthenticator.data.Device

class DevicesAdapter(private val onClick: (Device) -> Unit) : ListAdapter<Device, DevicesAdapter.DeviceViewHolder>(DeviceDiffCallback) {

    class DeviceViewHolder(itemView: View, val onClick: (Device) -> Unit) : RecyclerView.ViewHolder(itemView) {
        val deviceNameTextView: TextView = itemView.findViewById(R.id.device_name)
        val deviceStatusTextView: TextView = itemView.findViewById(R.id.device_status)
        private var currentDevice: Device? = null

        init {
            itemView.setOnClickListener {
                currentDevice?.let {
                    onClick(it)
                }
            }
        }

        fun bind(device: Device) {
            currentDevice = device;

            deviceNameTextView.text = device.name
            deviceStatusTextView.text = "Status: " + device.state.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.device_item, parent, false)
        return DeviceViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val device = getItem(position)
        holder.bind(device)
    }
}

object DeviceDiffCallback : DiffUtil.ItemCallback<Device>() {
    override fun areItemsTheSame(oldItem: Device, newItem: Device): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Device, newItem: Device): Boolean {
        return oldItem.id == newItem.id
    }

}