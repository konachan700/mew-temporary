package com.mewhpm.mewsync.data

import java.util.*

data class BleServiceWriteQueueElement (
    val mac: String,
    val bytes: ByteArray,
    var deadTime: Long = GregorianCalendar.getInstance().also {
        it.time = Date()
        it.add(Calendar.SECOND, 30)
    }.timeInMillis

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BleServiceWriteQueueElement

        if (mac != other.mac) return false
        if (!bytes.contentEquals(other.bytes)) return false
        if (deadTime != other.deadTime) return false

        return true
    }

    override fun hashCode(): Int {
        var result = mac.hashCode()
        result = 31 * result + bytes.contentHashCode()
        result = 31 * result + deadTime.hashCode()
        return result
    }
}