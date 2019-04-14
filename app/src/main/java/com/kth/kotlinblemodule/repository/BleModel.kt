package com.kth.kotlinblemodule.repository

import android.bluetooth.BluetoothDevice
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BleModel(
    @PrimaryKey var macAddr: String,
    var device: BluetoothDevice?
)