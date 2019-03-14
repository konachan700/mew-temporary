package com.mewhpm.mewsync.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Base64
import android.util.Log
import com.mewhpm.mewsync.dao.KnownDevicesDao
import com.mewhpm.mewsync.utils.Stm32HwUtils
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.Timeout
import io.reactivex.disposables.Disposable
import org.jetbrains.anko.toast
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.concurrent.TimeUnit

class BleService: Service() {
    companion object {
        const val EXTRA_ACTION = "action"
        const val EXTRA_ACTION_SEND_CMD = 1

        const val EXTRA_DATA_MAC = "mac"
        const val EXTRA_DATA_CMD = "cmd"
        const val EXTRA_DATA_DATA = "data_b64"
    }

    private var _rxBleClient : RxBleClient? = null
    private var lastDisposable : Disposable? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun rx(): RxBleClient {
        if (_rxBleClient == null) _rxBleClient = RxBleClient.create(this.applicationContext)
        return _rxBleClient!!
    }

    private fun writeToDevice(mac: String, packet: ByteArray) {
        val device = rx().getBleDevice(mac)
        if (lastDisposable != null && lastDisposable?.isDisposed == false) lastDisposable?.dispose()
        lastDisposable = device.establishConnection(false, Timeout(15, TimeUnit.SECONDS))
            .flatMap { rxBleConnection ->
                Thread.sleep(1000)
                rxBleConnection.createNewLongWriteBuilder()
                        .setCharacteristicUuid(UUID.fromString("0000FFE1-0000-1000-8000-00805F9B34FB"))
                        .setBytes(packet)
                        .build()
            }.subscribe(
                { byteArray ->
                    Log.d("writeToDevice", "written: ${byteArray.size}")
                },
                { throwable ->
                    Log.e("writeToDevice", "error: ${throwable.message}")
                    toast("error: ${throwable.message}")
                }
            )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) return START_STICKY

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
                writeToDevice(mac, packet)
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