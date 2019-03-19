package com.mewhpm.mewsync.data

import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey

data class BleServiceKeystore (
    val mac: String,
    val devicePublicKey : ECPublicKey,
    val myPublicKey : ECPublicKey,
    val myPrivateKey : ECPrivateKey,
    var active: Boolean = false
)