package com.mewhpm.mewsync.services

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.mewhpm.mewsync.data.BleDevice
import java.util.concurrent.CopyOnWriteArrayList

class BleDeviceSearchDummyImpl: BleDeviceSearch {
    override fun bleDiscoverStart(context: Context,
                                  list: CopyOnWriteArrayList<BleDevice>,
                                  listener: BleDiscoveryEvents) {
        for (i in 1..5) {
            var dev = BleDevice(id = i.toLong(), mac = "00:00:00:00:00:0$i", name = "MeW HPM $i")
            list.add(dev)
            listener.onDeviceFound()
        }
    }

    override fun bleDiscoverStop() {}

    override fun connect(dev: BleDevice): Boolean {
        return dev.mac.endsWith(":01") || dev.mac.endsWith(":02") || dev.mac.endsWith(":03")
    }

    override fun bleVerifyPin(dev: BleDevice, pin: String): Boolean {
        return pin.contentEquals("1234")
    }
}