package com.mewhpm.mewsync.data

import com.polidea.rxandroidble2.RxBleConnection
import com.polidea.rxandroidble2.RxBleDevice
import io.reactivex.disposables.Disposable
import java.util.*

data class BleServiceDeviceInfo (
    val connectedDevice: RxBleDevice,
    val connectedDeviceStateDisposable: Disposable,
    val connectedDeviceDisposable: Disposable,
    val connectedDeviceMAC : String,

    var isReady: Boolean = false,
    var connection: RxBleConnection? = null
)