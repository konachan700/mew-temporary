package com.mewhpm.mewsync.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.mewhpm.mewsync.dao.KnownDevicesDao
import com.mewhpm.mewsync.data.BleServiceDeviceInfo
import com.mewhpm.mewsync.data.BleServiceWriteQueueElement
import com.mewhpm.mewsync.utils.Stm32HwUtils
import com.mewhpm.mewsync.utils.toHexString
import com.polidea.rxandroidble2.*
import com.polidea.rxandroidble2.exceptions.BleException
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class BleService: Service() {
    companion object {
        const val EXTRA_ACTION = "action"
        const val EXTRA_ACTION_SEND_CMD = 1

        const val EXTRA_DATA_MAC = "mac"
        const val EXTRA_DATA_CMD = "cmd"
        const val EXTRA_DATA_DATA = "data_b64"

        private val UUID_SERIAL = UUID.fromString("0000FFE1-0000-1000-8000-00805F9B34FB")

        init {
            RxJavaPlugins.setErrorHandler { error ->
                if (error is UndeliverableException && error.cause is BleException) {
                    return@setErrorHandler // ignore BleExceptions as they were surely delivered at least once
                }
                Log.e("!! RxJavaPlugins", "UndeliverableException: ${error.message}")
                throw error
            }
        }
    }

    private val queue = LinkedBlockingDeque<BleServiceWriteQueueElement>()
    inner class LinkedBlockingDequeWorker : Runnable {
        override fun run() {
            while (true) {
                try {
                    val queueElement = queue.pollLast(9999, TimeUnit.DAYS) ?: continue
                    if (queueElement.deadTime < Date().time) continue

                    if ((!_devMap.containsKey(queueElement.mac)) || _devMap[queueElement.mac]?.isReady != true) {
                        queue.putFirst(queueElement)
                        Thread.yield()
                        continue
                    }

                    val conn = _devMap[queueElement.mac]!!.connection ?: continue
                    val unused = conn.createNewLongWriteBuilder()
                        .setCharacteristicUuid(UUID_SERIAL)
                        .setBytes(queueElement.bytes)
                        .build()
                        .subscribe(
                            { byteArray ->
                                Log.d("writeToDevice", "written: ${byteArray.size}")
                            },
                            { throwable ->
                                Log.e("writeToDevice", "error: ${throwable.message}")
                            }
                        )
                } catch (e: InterruptedException) {
                    return
                } catch (t : Throwable) {
                    Log.e("Worker", "error detected: ${t.message}")
                    continue
                }
            }
        }
    }

    private val _worker = LinkedBlockingDequeWorker()
    private val _workerThread = Thread(_worker)
    private val _started = AtomicBoolean(false)

    private var _rxBleClient : RxBleClient? = null
    private val _devMap = ConcurrentHashMap<String, BleServiceDeviceInfo>()

    private fun disconnect(mac: String) {
        if (_devMap.containsKey(mac)) {
            _devMap[mac]?.connectedDeviceStateDisposable?.dispose()
            _devMap[mac]?.connectedDeviceDisposable?.dispose()
            _devMap.remove(mac)
        }
    }

    private fun connectToBleDevice(mac: String): Boolean {
        try {
            if (_devMap.containsKey(mac)) {
                return when (_devMap[mac]!!.connectedDevice.connectionState) {
                    RxBleConnection.RxBleConnectionState.CONNECTED -> true
                    else -> {
                        _devMap.remove(mac)
                        connectToBleDevice(mac)
                    }
                }
            }

            if (_rxBleClient == null) {
                _rxBleClient = RxBleClient.create(this.applicationContext)
            }

            val connectedDevice = _rxBleClient!!.getBleDevice(mac)
            val connectedDeviceStateDisposable = connectedDevice!!.observeConnectionStateChanges().subscribe(
                {
                    when (it) {
                        RxBleConnection.RxBleConnectionState.CONNECTED -> {
                            _devMap[mac]?.isReady = true

                        }
                        RxBleConnection.RxBleConnectionState.DISCONNECTED -> {
                            disconnect(mac)
                        }
                        else -> {
                            _devMap[mac]?.isReady = false
                        }
                    }
                    Log.d("!!! ConnState", "connection state changed to ${it.name}")
                },
                { throwable ->
                    Log.e("!!! ConnState", "connect state error: ${throwable.message}")

                }
            )
            val connectedDeviceDisposable = connectedDevice!!
                .establishConnection(false)
                .delay(1, TimeUnit.SECONDS)
                .subscribe(
                    { rxBleConnection ->
                        rxBleConnection
                            .setupNotification(UUID_SERIAL, NotificationSetupMode.QUICK_SETUP)
                            .doOnNext {  }
                            .flatMap { it }
                            .subscribe(
                                { bytes ->
                                    Log.d("!!! READ CHR", "Bytes receive: ${bytes.size}")
                                    Log.d("!!! READ CHR", "Data:\t\t${bytes.toHexString()}")
                                },
                                { t: Throwable? ->
                                    Log.e("!!! READ CHR", "read error: ${t?.message}")
                                }
                            )
                        _devMap[mac]!!.connection = rxBleConnection
                    },
                    { t ->
                        Log.e("connect state", "error while reading data from device: ${t.message}")
                    }
                )

            val element = BleServiceDeviceInfo(connectedDevice, connectedDeviceStateDisposable, connectedDeviceDisposable, mac)
            _devMap[mac] = element

            return true
        } catch (t : Throwable) {
            Log.e("connect state", "error while connecting to device: ${t.message}")
        }
        return false
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) return START_STICKY
        if (!_started.get()) {
            _workerThread.start()
            _started.set(true)
        }

        when (intent.getIntExtra(EXTRA_ACTION, 0)) {
            EXTRA_ACTION_SEND_CMD -> {
                val mac = intent.getStringExtra(EXTRA_DATA_MAC)
                if (mac == null || KnownDevicesDao.isDeviceBroken(mac)) {
                    Log.e("EXTRA_ACTION_SEND_CMD", "Bad MAC address")
                    return START_STICKY
                }

                val cmd = intent.getIntExtra(EXTRA_DATA_CMD, 0)
                val data = intent.getStringExtra(EXTRA_DATA_DATA) ?: return START_STICKY
                val packet = Stm32HwUtils.createPacket(cmd, data)

                Log.d("!!! onStartCommand", "connectToBleDevice")
                if (connectToBleDevice(mac)) {
                    val element = BleServiceWriteQueueElement(mac, packet)
                    queue.putFirst(element)
                }
            }
        }
        return START_STICKY
    }

//    private fun sendOkIntent(code: Int = 0) {
//        val intentAnswer = Intent()
//        intentAnswer.putExtra(EXTRA_RESULT_CODE, EXTRA_RESULT_CODE_OK)
//        intentAnswer.putExtra(BLE_INTENT_ERR_CODE, code)
//        sendBroadcast(intentAnswer)
//    }
//
//    private fun sendErrorIntent(code: Int, msg: String) {
//        val intentAnswer = Intent()
//        intentAnswer.putExtra(EXTRA_RESULT_CODE, EXTRA_RESULT_CODE_ERROR)
//        intentAnswer.putExtra(BLE_INTENT_ERR_MESSAGE, msg)
//        intentAnswer.putExtra(BLE_INTENT_ERR_CODE, code)
//        sendBroadcast(intentAnswer)
//    }
}