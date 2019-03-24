package com.mewhpm.mewsync.services.ble

abstract class BleCmd {
    fun zeroPayload() : ByteArray {
        return ByteArray(2) { 0 }
    }


}