package com.mewhpm.mewsync.services

import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.util.Log
import com.mewhpm.mewsync.data.BleDevice
import java.util.concurrent.CopyOnWriteArrayList

class BleDeviceSearchImpl(
    private val context: Context
): BleDeviceSearch {
    private var _list: CopyOnWriteArrayList<BleDevice>? = null
    private var _listener: BleDiscoveryEvents? = null

    private val bleScanner = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            _listener?.onSearching()
            //Log.e("DeviceListActivity","onScanResult: ${result?.device?.address} - ${result?.device?.name}")

            if (result != null) {
                if (_list?.any { e -> e.mac.toUpperCase().contentEquals(result.device.address.toUpperCase()) } == false) {
                    val bleDevice = BleDevice(id = 0, mac = result.device.address.toUpperCase(), name = result.device.name)
                    _list?.add(bleDevice)
                    _listener?.onDeviceFound()
                }
            }
        }

//        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
//            super.onBatchScanResults(results)
//            Log.d("DeviceListActivity","onBatchScanResults:${results.toString()}")
//            results?.forEach { it ->
//                if (_list?.any { e -> e.mac.toUpperCase().contentEquals(it.device.address.toUpperCase()) } == false) {
//                    val bleDevice = BleDevice(id = 0, mac = it.device.address.toUpperCase(), name = it.device.name)
//                    _list?.add(bleDevice)
//                    _listener?.onDeviceFound()
//                }
//            }
//        }

//        override fun onScanFailed(errorCode: Int) {
//            super.onScanFailed(errorCode)
//            //Log.e("DeviceListActivity", "onScanFailed: $errorCode")
//        }
    }

    private val bluetoothLeScanner: BluetoothLeScanner
        get() {
            val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            return bluetoothManager.adapter.bluetoothLeScanner
        }

    override fun bleDiscoverStop() {
        bluetoothLeScanner.stopScan(bleScanner)
    }

    override fun bleDiscoverStart(context: Context, list: CopyOnWriteArrayList<BleDevice>, listener: BleDiscoveryEvents) {
        list.clear()
        _list = list
        _listener = listener
        bluetoothLeScanner.startScan(bleScanner)
    }

    override fun connect(dev: BleDevice): Boolean {
        return true
    }

    override fun bleVerifyPin(dev: BleDevice, pin: String): Boolean {
        return true
    }
}