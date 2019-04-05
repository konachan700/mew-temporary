package com.mewhpm.mewsync.data

import java.util.*
import java.util.concurrent.atomic.AtomicInteger

data class BleDeviceSessionData (
    val createDate : Date = Date(),
    val inactiveCounter : AtomicInteger = AtomicInteger(0),
    val device : BleDevice,

    var lastConnected : Date
    )