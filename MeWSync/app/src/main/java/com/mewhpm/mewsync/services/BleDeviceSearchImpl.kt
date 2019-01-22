package com.mewhpm.mewsync.services

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothClassicService
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothConfiguration
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService
import com.github.douglasjunior.bluetoothlowenergylibrary.BluetoothLeService
import com.mewhpm.mewsync.data.BleDevice
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

class BleDeviceSearchImpl: BleDeviceSearch {
    private var conf: BluetoothConfiguration? = null
    private var service: BluetoothService? = null

    override fun bleDiscoverStop() {
        service?.stopScan()
        service?.stopService()
        service = null
    }

    override fun bleDiscoverStart(context: Context, list: CopyOnWriteArrayList<BleDevice>, listener: BleDiscoveryEvents) {
        list.clear()
        conf = BluetoothConfiguration()

        conf?.context = context
        conf?.bluetoothServiceClass = BluetoothLeService::class.java
        conf?.bufferSize = 1024
        conf?.characterDelimiter = '\n'
        conf?.callListenersInMainThread = true
        conf?.deviceName = "MeW Pro"

        conf?.uuidService = UUID.fromString("e7810a71-73ae-499d-8c15-faa9aef0c3f2")
        conf?.uuidCharacteristic = UUID.fromString("bef8d6c9-9c21-4c9e-b632-bd58c1009f9f")
        //conf?.transport = BluetoothDevice.TRANSPORT_AUTO
        conf?.uuid = null // UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")

        BluetoothService.init(conf)
        service = BluetoothService.getDefaultInstance()
        service?.setOnScanCallback(object : BluetoothService.OnBluetoothScanCallback {
            override fun onDeviceDiscovered(device: BluetoothDevice, rssi: Int) {
                Log.e("###", "Found: ${device.address}")

                if (!list.any { e -> e.mac.toUpperCase().contentEquals(device.address.toUpperCase()) }) {
                    val bleDevice = BleDevice(id = 0, mac = device.address.toUpperCase(), name = device.name)
                    list.add(bleDevice)
                    listener.onDeviceFound()
                    //Log.e("###", "Found: ${device.address}")
                }
            }

            override fun onStartScan() { Log.e("###", "onStartScan") }
            override fun onStopScan() { Log.e("###", "onStopScan") }
        })
        service?.startScan()
    }

    override fun connect(dev: BleDevice): Boolean {
        return true
    }

    override fun bleVerifyPin(dev: BleDevice, pin: String): Boolean {
        return true
    }
}