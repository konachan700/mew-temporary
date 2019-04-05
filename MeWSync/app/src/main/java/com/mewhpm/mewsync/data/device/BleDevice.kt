package com.mewhpm.mewsync.data.device

class BleDevice(id: Long, name: String, val mac: String) : Device(id, name, DeviceType.REAL)