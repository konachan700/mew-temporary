package com.mewhpm.mewsync.dao

import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils
import com.mewhpm.mewsync.data.PassRecord

class PasswordsDao(val connectionSource : ConnectionSource) {
    private val dao : Dao<PassRecord, Long> = DaoManager.createDao(connectionSource, PassRecord::class.java)
    init {
        TableUtils.createTableIfNotExists(connectionSource, PassRecord::class.java)
    }

    fun getAllChild(parentId: Long, nodeType: Long) : List<PassRecord> {
        val unsorted = dao.queryForFieldValuesArgs(mapOf("parentId" to parentId, "nodeType" to nodeType))
        return unsorted.sortedWith(compareBy(PassRecord::title))
    }

    fun getAllChild(parentId: Long) : List<PassRecord> {
        val unsorted = dao.queryForFieldValuesArgs(mapOf("parentId" to parentId))
        return unsorted.sortedWith(compareBy(PassRecord::nodeType, PassRecord::title))
    }

    fun create(passRecord: PassRecord) {
        dao.create(passRecord)
    }

    fun remove(id: Long) {
        dao.deleteById(id)
    }

    fun remove(passRecord: PassRecord) {
        dao.delete(passRecord)
    }

}