package com.mewhpm.mewsync.data

@ExperimentalUnsignedTypes
class BleServiceCommands {
    companion object {
        const val CMD_GET_DEVICE_SESSION_KEY = 0x4300u
        const val CMD_SEND_MY_SESSION_KEY = 0x4301u
    }
}