package com.kth.kotlinblemodule.background

import android.app.Service
import android.bluetooth.*
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.nfc.NfcAdapter.EXTRA_DATA
import android.bluetooth.BluetoothGattCharacteristic
import android.os.Binder
import com.kth.kotlinblemodule.util.BleUtil
import com.kth.kotlinblemodule.util.Const


class BleService : Service() {

    private val TAG = BleService::class.java.simpleName

    private lateinit var gatt: BluetoothGatt
    private val binder = MyBinder()
    private var mConnectionState: Int = Const.STATE_CONNECTED

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    private val gattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)

            if (status == BluetoothProfile.STATE_CONNECTED) {
                for (service in gatt?.services!!) {
                    Log.d(TAG, service.uuid.toString())
                    for (characteristic in service.characteristics) {
                        Log.d(TAG, characteristic.uuid.toString())
                    }
                }
            }
        }

        override fun onReadRemoteRssi(gatt: BluetoothGatt?, rssi: Int, status: Int) {
            super.onReadRemoteRssi(gatt, rssi, status)
            if (status == BluetoothProfile.STATE_CONNECTED) {

            } else if (status == BluetoothProfile.STATE_DISCONNECTED) {

            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
            super.onCharacteristicChanged(gatt, characteristic)
        }

        override fun onDescriptorRead(gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int) {
            super.onDescriptorRead(gatt, descriptor, status)
        }
    }

    private fun broadcastUpdate(action: String) {
        val intent = Intent(action)
        sendBroadcast(intent)
    }

    fun connect(mac: String): Boolean {

        if (BleUtil.bluetoothAdapter == null) {
            return false
        }

        BleUtil.connect(this, BleUtil.bluetoothAdapter!!.getRemoteDevice(mac), gattCallback)

        return true
    }

    private fun broadcastUpdate(action: String, characteristic: BluetoothGattCharacteristic) {
        val intent = Intent(action)

        if (Const.MY_SERVICE == characteristic.uuid.toString()) {

            //TODO need custom

            val flag = characteristic.properties
            var format = -1
            if (flag and 0x01 != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16
                Log.d(TAG, "Heart rate format UINT16.")
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8
                Log.d(TAG, "Heart rate format UINT8.")
            }
            val heartRate = characteristic.getIntValue(format, 1)!!
            Log.d(TAG, String.format("Received heart rate: %d", heartRate))
            intent.putExtra(EXTRA_DATA, heartRate.toString())
        } else {

            // For all other profiles, writes the data formatted in HEX.
            val data = characteristic.value
            if (data != null && data.isNotEmpty()) {
                val stringBuilder = StringBuilder(data.size)
                for (byteChar in data)
                    stringBuilder.append(String.format("%02X ", byteChar))
                intent.putExtra(EXTRA_DATA, String(data) + "\n" + stringBuilder.toString())
            }
        }
        sendBroadcast(intent)
    }

    inner class MyBinder : Binder() {
        internal val service: BleService
            get() = this@BleService
    }
}