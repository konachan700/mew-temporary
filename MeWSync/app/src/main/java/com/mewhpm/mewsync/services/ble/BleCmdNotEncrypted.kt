package com.mewhpm.mewsync.services.ble

import com.mewhpm.mewsync.utils.Stm32HwUtils.Companion.createPacket
import java.security.PublicKey

@ExperimentalUnsignedTypes
class BleCmdNotEncrypted private constructor() : BleCmd() {
    companion object {
        private var instance: BleCmdNotEncrypted? = null
        fun getInstance(): BleCmdNotEncrypted {
            if (instance == null) instance = BleCmdNotEncrypted()
            return instance!!
        }
    }

    fun writeTemporaryKeys(): ByteArray {
        return createPacket(0xFF00, zeroPayload())
    }

    fun sendAppRSAKey(key: PublicKey): ByteArray {
        return createPacket(0xFF01, key.encoded)
    }

    fun getDeviceVersions(): ByteArray {
        return createPacket(0xFF02, zeroPayload())
    }
}