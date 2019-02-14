package com.mewhpm.mewsync.dao

import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils
import com.mewhpm.mewsync.data.BleDevice

class KnownDevicesDao private constructor (val connectionSource : ConnectionSource) {
    companion object {
        private var instance: KnownDevicesDao? = null
        fun getInstance(_connectionSource : ConnectionSource) : KnownDevicesDao {
            if (instance == null) instance = KnownDevicesDao(_connectionSource)
            return instance!!
        }
    }

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

    fun save(dev: BleDevice) {
        dao.update(dev)
    }

    fun setDefault(dev: BleDevice) {
        dao.updateBuilder().updateColumnValue("default", false).update()
        dev.default = true
        dao.update(dev)
    }

    fun getDefault() : BleDevice? {
        val list = dao.queryForEq("default", true)
        return if (list.isNotEmpty()) list[0] else null
    }
}