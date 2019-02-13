package com.mewhpm.mewsync.dao

import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils
import com.mewhpm.mewsync.data.BleDevice

class KnownDevicesDao(val connectionSource : ConnectionSource) {
    private val dao : Dao<BleDevice, Long> = DaoManager.createDao(connectionSource, BleDevice::class.java)
    init {
        TableUtils.createTableIfNotExists(connectionSource, BleDevice::class.java)
    }

    fun getAll() : List<BleDevice> {
        return dao.queryForAll()
    }

    fun isExist(dev: BleDevice): Boolean {
        return !dao.queryForEq("mac", dev.mac).isEmpty()
    }

    fun addNew(dev: BleDevice) {
        dao.create(dev)
    }

    fun remove(dev: BleDevice) {
        dao.delete(dev)
    }
}