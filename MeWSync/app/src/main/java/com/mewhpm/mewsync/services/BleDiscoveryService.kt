package com.mewhpm.mewsync.services

import android.app.Service
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.mewhpm.mewsync.services.BleService.Companion.EXTRA_ACTION
import com.mewhpm.mewsync.services.BleService.Companion.EXTRA_DATA_MAC
import com.mewhpm.mewsync.services.BleService.Companion.EXTRA_DATA_NAME
import com.mewhpm.mewsync.services.BleService.Companion.EXTRA_DATA_TIMESTAMP
import com.mewhpm.mewsync.services.BleService.Companion.EXTRA_RESULT_CODE
import com.mewhpm.mewsync.services.BleService.Companion.EXTRA_RESULT_CODE_IN_PROGRESS
import com.mewhpm.mewsync.services.BleService.Companion.EXTRA_RESULT_CODE_OK
import java.util.*
import java.util.concurrent.CopyOnWriteArraySet

class BleDiscoveryService: Service() {
    companion object {
        const val BLE_DISCOVERY_START = 1
        const val BLE_DISCOVERY_STOP = 2
    }

    private val _foundDevices: CopyOnWriteArraySet<String> = CopyOnWriteArraySet()
    private val bleScanner = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            sendProcessIntent()
            if (result != null &&
                result.device != null &&
                result.device.name != null &&
                (result.device.type == BluetoothDevice.DEVICE_TYPE_LE /*|| result.device.type == BluetoothDevice.DEVICE_TYPE_DUAL*/)
            ) {
                val addr = result.device.address.toUpperCase()
                if (!_foundDevices.contains(addr)) {
                    _foundDevices.add(addr)
                    sendOkIntent(result.device.address.toUpperCase() to result.device.name)
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            bleDiscoverStop()
            sendErrorIntent(errorCode, "onScanFailed")
        }
    }

    private val bluetoothLeScanner: BluetoothLeScanner
        get() {
            val bluetoothManager = this.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            return bluetoothManager.adapter.bluetoothLeScanner
        }

    private fun bleDiscoverStop() {
        bluetoothLeScanner.stopScan(bleScanner)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) return START_STICKY
        if (!intent.hasExtra(EXTRA_ACTION)) return START_STICKY

        when (intent.getIntExtra(EXTRA_ACTION, 0)) {
            BLE_DISCOVERY_START -> {
                _foundDevices.clear()
                bluetoothLeScanner.startScan(bleScanner)
            }
            BLE_DISCOVERY_STOP -> {
                bleDiscoverStop()
            }
        }
        return START_STICKY
    }

    private fun sendProcessIntent() {
        val intentAnswer = Intent()
        intentAnswer.action = this::class.java.simpleName
        intentAnswer.addCategory(Intent.CATEGORY_DEFAULT)
        intentAnswer.putExtra(EXTRA_RESULT_CODE, EXTRA_RESULT_CODE_IN_PROGRESS)
        sendBroadcast(intentAnswer)
    }

    private fun sendErrorIntent(code: Int, msg: String) {
        val intentAnswer = Intent()
        intentAnswer.action = this::class.java.simpleName
        intentAnswer.addCategory(Intent.CATEGORY_DEFAULT)
        intentAnswer.putExtra(EXTRA_RESULT_CODE, BleService.EXTRA_RESULT_CODE_ERROR)
        intentAnswer.putExtra(BleService.BLE_INTENT_ERR_MESSAGE, msg)
        intentAnswer.putExtra(BleService.BLE_INTENT_ERR_CODE, code)
        sendBroadcast(intentAnswer)
    }

    private fun sendOkIntent(data: Pair<String, String>) {
        val intentAnswer = Intent()
        intentAnswer.action = this::class.java.simpleName
        intentAnswer.addCategory(Intent.CATEGORY_DEFAULT)
        intentAnswer.putExtra(EXTRA_RESULT_CODE, EXTRA_RESULT_CODE_OK)
        intentAnswer.putExtra(EXTRA_DATA_MAC, data.first)
        intentAnswer.putExtra(EXTRA_DATA_NAME, data.second)
        intentAnswer.putExtra(EXTRA_DATA_TIMESTAMP, Date().time)
        sendBroadcast(intentAnswer)
    }
}