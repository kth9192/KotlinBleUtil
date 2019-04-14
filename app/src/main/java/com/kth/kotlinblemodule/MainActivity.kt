package com.kth.kotlinblemodule

import android.Manifest
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName
    private val REQUEST_ENABLE_BT = 1
    private var ble_scanner: BluetoothLeScanner? = null
    private var PERMISSION_ALL = 1
    private lateinit var viewModel: BleViewModel
    private lateinit var gatt: BluetoothGatt

    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val BluetoothAdapter.isDisabled: Boolean
        get() = !isEnabled

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this@MainActivity).get(BleViewModel::class.java)

        val PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH
        )

        BleUtil.checkPermissions(this, PERMISSIONS)

        fun PackageManager.missingSystemFeature(name: String): Boolean = !hasSystemFeature(name)

        packageManager.takeIf { it.missingSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) }?.also {
            Toast.makeText(this, "not supported", Toast.LENGTH_SHORT).show()
            finish()
        }

        ble_scanner = bluetoothAdapter?.bluetoothLeScanner

        bluetoothAdapter?.takeIf { it.isDisabled }?.apply {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }

        val adapter = BleAdapter()
        adapter.setHasStableIds(true)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        viewModel.getList()?.observe(this, Observer {
            adapter.submitList(ArrayList(it))
        })

        scan.setOnClickListener {
            BleUtil.scanLeDevice(it.isEnabled, ble_scanner, leScanCallback)
        }

        read.setOnClickListener {
//            BleUtil.read()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.deleteAll()
    }

    private val leScanCallback = object : ScanCallback() {

        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            Log.d("DeviceScan", "onScanResult: ${result?.device?.address} - ${result?.device?.name}")
            var bleModel = BleModel(result?.device?.address.toString(), result?.device).also { viewModel.insert(it) }
            Log.d(TAG,"테스트 " + bleModel.device?.address)
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
            Log.d("DeviceScan", "onBatchScanResults:${results.toString()}")
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.d("DeviceScan", "onScanFailed: $errorCode")
            val builder = AlertDialog.Builder(this@MainActivity)
            // 여기서 부터는 알림창의 속성 설정

            builder.setTitle(getString(R.string.title_scan_error))        // 제목 설정
                .setMessage(getString(R.string.error_search_device))
                .setPositiveButton("예", DialogInterface.OnClickListener { dialog, which ->
                    dialog.cancel()
                })
            builder.create().show()    // 알림창 띄우기
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {

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

            } else if (status == BluetoothProfile.STATE_DISCONNECTED){

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

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
        }
    }

}
