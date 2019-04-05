package com.mewhpm.mewsync.dao

import android.content.SharedPreferences
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils
import com.mewhpm.mewsync.dao.entity.DeviceEntity
import com.mewhpm.mewsync.data.device.*
import java.util.concurrent.atomic.AtomicReference

class DeviceDao private constructor (
    private val connectionSource : ConnectionSource,
    private val preferences: SharedPreferences
) {
    companion object {
        const val PREF_DEFAULT_DEV = "defaultDeviceId"
        const val PREF_DEV_PINCODE = "deviceEncryptedPincode"

        private val instance : AtomicReference<DeviceDao> = AtomicReference()
        fun init(cs : ConnectionSource, sp: SharedPreferences) = instance.compareAndSet(null, DeviceDao(cs, sp))
        fun getInstance() : DeviceDao = instance.get()!!
    }

    private val dao : Dao<DeviceEntity, Long> = DaoManager.createDao(connectionSource, DeviceEntity::class.java)
    init {
        TableUtils.createTableIfNotExists(connectionSource, DeviceEntity::class.java)
    }

    private fun getById(id: Long) : DeviceEntity? {
        return dao.queryForId(id)
    }

    private fun mapEntity(it: DeviceEntity) : Device {
        val type = DeviceType.valueOf(it.type!!)
        return when (type) {
            DeviceType.REAL -> BleDevice(it.id, it.name, it.mac!!)
            DeviceType.EMULATE -> LocalDevice(it.id, it.name)
            DeviceType.CLOUD -> CloudDevice(it.id, it.name)
        }
    }

    fun verifyPincode(dev: Device, pincode: String) : Boolean {
        val pinPref = preferences.getString(PREF_DEV_PINCODE, null) ?: return false


        return false
    }

    fun isDefault(dev: Device) : Boolean {
        return (preferences.getLong(PREF_DEFAULT_DEV, -1) == dev.id)
    }

    fun getDefault() : Device? {
        val devId = preferences.getLong(PREF_DEFAULT_DEV, -1)
        if (devId.compareTo(-1) == 0) {
            return null
        }
        val entity = getById(devId) ?: return null
        return mapEntity(entity)
    }

    fun setDefault(dev: Device) {
        preferences.edit()
            .putLong(PREF_DEFAULT_DEV, dev.id)
            .apply()
    }

    fun getAll() : List<Device> {
        val list = dao.queryForAll().sortedWith(compareBy(DeviceEntity::type, DeviceEntity::name))
        return list.map { mapEntity(it) }
    }

    fun createEmulated(dev: LocalDevice) {
        val d = DeviceEntity(0, null, dev.name, DeviceType.EMULATE.name, null)
        dao.create(d)
    }

    fun createReal(dev: BleDevice) {
        val d = DeviceEntity(0, dev.mac, dev.name, DeviceType.REAL.name, null)
        dao.create(d)
    }

    fun createCloud(dev: CloudDevice) {
        throw NotImplementedError()
    }

    fun isMacExist(mac: String) : Boolean {
        return !dao.queryForEq("mac", mac).isEmpty()
    }

    fun delete(dev: Device) {
        val d = getById(dev.id) ?: return
        dao.delete(d)
    }
}