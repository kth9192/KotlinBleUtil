package com.kth.kotlinblemodule.util

import android.app.Activity
import android.bluetooth.*
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import com.kth.kotlinblemodule.app.MyApp
import java.util.*

object BleUtil {

    private var PERMISSION_ALL: Int = 1
    private lateinit var gatt: BluetoothGatt
    private var SCAN_PERIOD: Long = 10000

    val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = MyApp.getGlobalApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    fun setScanPeriod(time: Long) {
        SCAN_PERIOD = time
    }

    fun checkPermissions(context: Context?, permissions: Array<String>) {

        if (context != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(context as Activity, permissions,
                        PERMISSION_ALL
                    )
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