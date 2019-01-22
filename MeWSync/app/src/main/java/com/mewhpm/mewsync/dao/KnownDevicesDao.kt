package com.mewhpm.mewsync.dao

import com.mewhpm.mewsync.data.BleDevice
import com.mewhpm.mewsync.services.AppDatabaseOpenHelper
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.delete
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select

class KnownDevicesDao {
    private val TABLE = "BleDevices"

    fun getAll(db: AppDatabaseOpenHelper?) : List<BleDevice> {
        return db?.use {
            select(TABLE).parseList(classParser<BleDevice>())
        } ?: ArrayList()
    }

    fun isExist(db: AppDatabaseOpenHelper?, dev: BleDevice): Boolean {
        val x = db?.use {
            select(TABLE)
                .whereArgs("mac = {mac}", "mac" to dev.mac)
                .limit(1)
                .parseOpt(classParser<BleDevice>())
        }
        return x != null
    }

    fun addNew(db: AppDatabaseOpenHelper?, dev: BleDevice): Long {
        return db?.use {
            insert(TABLE,
                "mac" to dev.mac, "name" to dev.name)
        } ?: 0
    }

    fun remove(db: AppDatabaseOpenHelper?, dev: BleDevice) {
        db?.use {
            delete(TABLE, "id = {devid}", "devid" to dev.id)
        }
    }
}