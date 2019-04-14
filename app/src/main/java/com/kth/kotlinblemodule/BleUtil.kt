package com.kth.kotlinblemodule

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

object BleUtil {

    private var PERMISSION_ALL: Int = 1
    private lateinit var gatt: BluetoothGatt
    private var SCAN_PERIOD: Long = 10000

    fun setScanPeriod(time: Long) {
        SCAN_PERIOD = time
    }

    fun checkPermissions(context: Context?, permissions: Array<String>) {

        if (context != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(context as Activity, permissions, PERMISSION_ALL)
                }
            }
        }
    }

    fun scanLeDevice(enable: Boolean, ble_scanner: BluetoothLeScanner?, leScanCallback: ScanCallback) {

        when (enable) {
            true -> {
                // Stops scanning after a pre-defined scan period.
                Handler().postDelayed({
                    ble_scanner?.stopScan(leScanCallback)
                }, SCAN_PERIOD)
                ble_scanner?.startScan(leScanCallback)
            }
            else -> {
                ble_scanner?.stopScan(leScanCallback)
            }
        }
    }

    fun connect(context: Context?, device: BluetoothDevice, callback: BluetoothGattCallback) {
        gatt = device.connectGatt(context, false, callback)
    }

    fun read(service: String, characteristic: String) {
        gatt.readCharacteristic(
            gatt.getService(UUID.fromString(service)).getCharacteristic(
                UUID.fromString(characteristic)
            )
        )
    }
}