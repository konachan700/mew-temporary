package com.mewhpm.mewsync.data.device

abstract class Device(val id: Long, var name: String, val type: DeviceType) {
    fun <T : Device> getImpl(clazz: Class<T>) : T {
        return this as T
    }
}