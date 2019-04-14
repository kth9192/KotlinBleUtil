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
import android.content.BroadcastReceiver
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import com.kth.kotlinblemodule.background.BleService
import com.kth.kotlinblemodule.repository.BleModel
import com.kth.kotlinblemodule.util.BleUtil
import com.kth.kotlinblemodule.view.BleAdapter
import com.kth.kotlinblemodule.viewmodel.BleViewModel
import kotlin.collections.ArrayList
import android.content.ComponentName
import android.os.IBinder
import android.content.ServiceConnection
import com.kth.kotlinblemodule.util.Const


class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName
    private val REQUEST_ENABLE_BT = 1
    private var ble_scanner: BluetoothLeScanner? = null
    private var PERMISSION_ALL = 1
    private lateinit var viewModel: BleViewModel
    private lateinit var gatt: BluetoothGatt
    private val bluetoothAdapter = BleUtil.bluetoothAdapter

    private var bleService: BleService? = null

    private lateinit var bleModel: BleModel

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

        bluetoothAdapter?.takeIf { it.isDisabled }?.apply {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }

        ble_scanner = bluetoothAdapter?.bluetoothLeScanner

        //뷰설정
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

        connect.setOnClickListener {
            bleService?.connect(bleModel.macAddr)
        }

        bindService(Intent(this, BleService::class.java), bleConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.deleteAll()
        unbindService(bleConnection)
    }

    private val leScanCallback = object : ScanCallback() {

        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            Log.d("DeviceScan", "onScanResult: ${result?.device?.address} - ${result?.device?.name}")
            bleModel = BleModel(result?.device?.address.toString(), result?.device )
                .also { viewModel.insert(it) }
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

    private val gattUpdateReceiver = object : BroadcastReceiver() {

        private lateinit var bluetoothLeService: BleService

        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            when (action) {
                Const.ACTION_GATT_CONNECTED -> {

                }
                Const.ACTION_GATT_DISCONNECTED -> {

                }
                Const.ACTION_GATT_SERVICES_DISCOVERED -> {

                }
                Const.ACTION_DATA_AVAILABLE -> {

                }
            }
        }
    }

    private val bleConnection = object : ServiceConnection {

        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            bleService = (service as BleService.MyBinder).service

        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            bleService = null
        }
    }

}
