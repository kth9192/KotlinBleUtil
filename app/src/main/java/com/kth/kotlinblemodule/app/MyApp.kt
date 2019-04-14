package com.kth.kotlinblemodule.app

import android.app.Application
import android.bluetooth.BluetoothDevice

class MyApp : Application() {
    companion object{
        private var instance: MyApp? = null
        private var device: BluetoothDevice? = null

        fun getGlobalApplicationContext(): MyApp {

            if (instance == null) {
                throw IllegalStateException("This Application does not inherit MyApp")
            }

            return instance as MyApp
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    fun setBleDevice(flowerPot : BluetoothDevice?){
        device = flowerPot
    }

    fun getBleDevice() : BluetoothDevice?{

        return device
    }

    override fun onTerminate() {

        super.onTerminate()
        instance = null
    }
}