package com.mewhpm.mewsync.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.mewhpm.mewsync.dao.KnownDevicesDao
import com.mewhpm.mewsync.data.BleServiceCommands.Companion.CMD_GET_DEVICE_SESSION_KEY
import com.mewhpm.mewsync.data.BleServiceCommands.Companion.CMD_SEND_MY_SESSION_KEY
import com.mewhpm.mewsync.data.BleServiceDeviceInfo
import com.mewhpm.mewsync.data.BleServiceKeystore
import com.mewhpm.mewsync.data.BleServiceWriteQueueElement
import com.mewhpm.mewsync.utils.*
import com.polidea.rxandroidble2.*
import com.polidea.rxandroidble2.exceptions.BleException
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import java.io.ByteArrayOutputStream
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

@ExperimentalUnsignedTypes
class BleService: Service() {
    companion object {
        const val EXTRA_ACTION = "action"
        const val EXTRA_ACTION_SEND_CMD = 1

        const val EXTRA_DATA_MAC = "mac"
        const val EXTRA_DATA_CMD = "cmd"
        const val EXTRA_DATA_DATA = "data_b64"

        private val UUID_SERIAL = UUID.fromString("0000FFE1-0000-1000-8000-00805F9B34FB")

        const val DATA_TTL_MSEC = 300

        private val deviceSessionKeyMap = ConcurrentHashMap<String, BleServiceKeystore>()

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
                    if (queueElement.deadTime < Date().time) {
                        Log.d("queueElement", "Queue element dead time. MAC: ${queueElement.mac}; DATA: ${queueElement.bytes.toHexString()};")
                        continue
                    }

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

    private val _readedBytes = ByteArrayOutputStream()
    private val _readedBytesTTL = AtomicLong(0)

    private fun disconnect(mac: String) {
        if (_devMap.containsKey(mac)) {
            _devMap[mac]?.connectedDeviceStateDisposable?.dispose()
            _devMap[mac]?.connectedDeviceDisposable?.dispose()
            _devMap.remove(mac)
        }
        deviceSessionKeyMap.remove(mac)
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
                            .setupNotification(UUID_SERIAL)
                            .doOnNext {  }
                            .flatMap { it }
                            .subscribe(
                                { bytes ->
                                    if (_readedBytesTTL.get() < Date().time) {
                                        _readedBytes.reset()
                                    }

                                    _readedBytes.write(bytes)
                                    _readedBytesTTL.set(Date().time + DATA_TTL_MSEC)

                                    parser(_readedBytes.toByteArray().toUByteArray(), mac)
                                },
                                { t: Throwable? ->
                                    Log.e("!!! READ CHR", "read error: ${t?.message}")
                                    t?.printStackTrace()
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

        if (_readedBytes.size() > 0) {
            if (_readedBytesTTL.get() < Date().time) {
                _readedBytes.reset()
            } else {
                // TODO: SEND ERROR
                return START_STICKY
            }
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

    private fun parser(bytes: UByteArray, mac: String) {
        if (bytes.size < 10) return
        if (bytes[0] neq 0x43u || bytes[1] neq 0x77u) {
            Log.e("### PARSER", _readedBytes.toByteArray().toHexString())
            Log.e("### PARSER", "Bad packet magic.")
            _readedBytes.reset()
            return
        }

        val packetLen = bytes.extractInt16(4, 5)
        if (packetLen > 2048u) {
            Log.e("### PARSER", _readedBytes.toByteArray().toHexString())
            Log.e("### PARSER", "Bad packet size. Max packet size is 2048, but in package size = $packetLen.")
            _readedBytes.reset()
            return
        }

        if (bytes.size >= (10 + packetLen.toInt())) {
            val packetChecksum = bytes.extractInt32(6, 7, 8, 9)
            val payload = bytes.copyOfRange(10, bytes.size)
            val generatedChecksum = Stm32HwUtils.checksum(payload).toULong()
            if (packetChecksum != generatedChecksum) {
                Log.e("### PARSER", payload.toByteArray().toHexString())
                Log.e("### PARSER", _readedBytes.toByteArray().toHexString())
                Log.e("### PARSER", "Bad packet checksum. Generated: 0x${generatedChecksum.toString(16)}, in packet: 0x${packetChecksum.toString(16)}.")
                _readedBytes.reset()
                return
            }
            val cmd = bytes.extractInt16(2, 3)
            parserHandle(cmd, payload, mac)
        }

    }

    private fun parserHandle(cmd: UInt, bytes: UByteArray, mac: String) {
        Log.d("parserHandle", "MAC: $mac; LEN: ${bytes.size}; DATA: ${bytes.toByteArray().toHexString()};")

        try {
            when (cmd) {
                CMD_GET_DEVICE_SESSION_KEY -> {
                    val keypair = CryptoUtils.generateECDHP256Keypair()
                    val keystore = BleServiceKeystore(
                        mac, CryptoUtils.getECDHP256PublicKeyFromBinary(bytes.toByteArray()),
                        keypair.public as ECPublicKey, keypair.private as ECPrivateKey
                    )
                    deviceSessionKeyMap[mac] = keystore

                    val key = CryptoUtils.getBytesFromECPublicKey(keystore.myPublicKey)
                    Log.e("--keystore", "key.x = ${keystore.myPublicKey.w.affineX.toByteArray().toHexString()}")
                    Log.e("--keystore", "key.y = ${keystore.myPublicKey.w.affineY.toByteArray().toHexString()}")

                    val packet = Stm32HwUtils.createPacket(CMD_SEND_MY_SESSION_KEY, key)

                    if (connectToBleDevice(mac)) {
                        val element = BleServiceWriteQueueElement(mac, packet)
                        queue.putFirst(element)
                    }
                }
                CMD_SEND_MY_SESSION_KEY -> {
                    if ((bytes.size == 2) && (bytes[0] eq 0u) && (bytes[1] eq 0u)) {
                        deviceSessionKeyMap[mac]?.active = true
                    } else {
                        Log.e("### PARSER", "Key exchange fail.")
                    }
                }
                else -> {
                    Log.e("### PARSER", "Unknown command 0x${cmd.toString(16)}.")
                }
            }
        } /*catch (t : Throwable) {
            Log.e("### PARSER", "Error. ${t.message}")
        } */finally {
            _readedBytes.reset()
        }
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