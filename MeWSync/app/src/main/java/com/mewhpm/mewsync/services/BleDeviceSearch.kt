package com.mewhpm.mewsync.services

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.mewhpm.mewsync.data.BleDevice
import java.util.concurrent.CopyOnWriteArrayList

interface BleDeviceSearch {
    fun bleDiscoverStart(context: Context, list: CopyOnWriteArrayList<BleDevice>, listener: BleDiscoveryEvents)
    fun bleDiscoverStop()
    fun connect(dev: BleDevice): Boolean
    fun bleVerifyPin(dev: BleDevice, pin: String): Boolean
}