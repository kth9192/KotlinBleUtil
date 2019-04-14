package com.kth.kotlinblemodule.repository

import android.bluetooth.BluetoothDevice
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kth.kotlinblemodule.repository.BleModel


class DeviceTypeConverter {

    companion object {

        @TypeConverter
        @JvmStatic
        fun toBleList(value: String): List<BleModel> {

            val listType = object : TypeToken<List<BleModel>>() {}.type

            return Gson().fromJson(value, listType)
        }

        @TypeConverter
        @JvmStatic
        fun toBleString(value: List<BleModel>): String {

            return Gson().toJson(value)
        }

        @TypeConverter
        @JvmStatic
        fun toDeviceList(value: String): BluetoothDevice {

            val listType = object : TypeToken<BluetoothDevice>() {}.type

            return Gson().fromJson(value, listType)
        }

        @TypeConverter
        @JvmStatic
        fun toDeviceString(value: BluetoothDevice): String {

            return Gson().toJson(value)
        }

    }
}