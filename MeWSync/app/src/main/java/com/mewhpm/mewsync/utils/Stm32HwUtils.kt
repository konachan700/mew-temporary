package com.mewhpm.mewsync.utils

import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream

@ExperimentalUnsignedTypes
class Stm32HwUtils {
    companion object {
        fun checksum(data: UByteArray) : UInt {
            var checksum = 0x437700FFu
            for (i in 0..(data.size - 1)) checksum = checksum.xor(data[i].toUInt().shl(i % 24))
            return checksum
        }

        fun createPacket(cmd: Int, dataBase64: String) : ByteArray {
            val dataArray = ByteArrayOutputStream()
            val data = Base64.decode(dataBase64, Base64.DEFAULT)
            val len = data.size
            val checksum = checksum(data.toUByteArray())

            // MAGIC NUMBER (2 Bytes)
            dataArray.write(0x43)
            dataArray.write(0x77)
            // COMMAND (2 Bytes)
            dataArray.write((cmd shr 8) and 0xFF)
            dataArray.write(cmd and 0xFF)
            // PAYLOAD SIZE (2 Bytes)
            dataArray.write((len shr 8) and 0xFF)
            dataArray.write(len and 0xFF)
            // PAYLOAD CRC32 (4 Bytes)
            dataArray.write(((checksum shr 24) and 0xFFu).toInt())
            dataArray.write(((checksum shr 16) and 0xFFu).toInt())
            dataArray.write(((checksum shr 8) and 0xFFu).toInt())
            dataArray.write((checksum and 0xFFu).toInt())
            // PAYLOAD
            dataArray.write(data)

            return dataArray.toByteArray()
        }
    }
}