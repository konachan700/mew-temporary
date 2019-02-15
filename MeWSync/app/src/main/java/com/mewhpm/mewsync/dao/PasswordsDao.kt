package com.mewhpm.mewsync.dao

import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils
import com.mewhpm.mewsync.data.PassRecord
import com.mewhpm.mewsync.utils.CryptoUtils

class PasswordsDao(val connectionSource : ConnectionSource) {
    private val dao : Dao<PassRecord, Long> = DaoManager.createDao(connectionSource, PassRecord::class.java)
    init {
        TableUtils.createTableIfNotExists(connectionSource, PassRecord::class.java)
    }

    fun getAllChild(parentId: Long, nodeType: Long) : List<PassRecord> {
        val unsorted = dao.queryForFieldValuesArgs(mapOf("parentId" to parentId, "nodeType" to nodeType))
        return decrypt(unsorted.sortedWith(compareBy(PassRecord::title)))
    }

    fun getAllChild(parentId: Long) : List<PassRecord> {
        val unsorted = dao.queryForFieldValuesArgs(mapOf("parentId" to parentId))
        return decrypt(unsorted.sortedWith(compareBy(PassRecord::nodeType, PassRecord::title)))
    }

    fun create(passRecord: PassRecord) {
        encrypt(passRecord)
        dao.create(passRecord)
    }

    fun remove(id: Long) {
        dao.deleteById(id)
    }

    fun remove(passRecord: PassRecord) {
        dao.delete(passRecord)
    }

    private fun encrypt(passRecords: List<PassRecord>) : List<PassRecord> {
        passRecords.forEach { encrypt(it) }
        return passRecords
    }

    private fun decrypt(passRecords: List<PassRecord>) : List<PassRecord> {
        passRecords.forEach { decrypt(it) }
        return passRecords
    }

    private fun encrypt(passRecord: PassRecord) : PassRecord {
        passRecord.metadataJson = CryptoUtils.encryptRSA(passRecord.metadataJson)
        passRecord.title = CryptoUtils.encryptRSA(passRecord.title)
        passRecord.text = CryptoUtils.encryptRSA(passRecord.text)
        return passRecord
    }

    private fun decrypt(passRecord: PassRecord) : PassRecord {
        passRecord.metadataJson = CryptoUtils.decryptRSA(passRecord.metadataJson)
        passRecord.title = CryptoUtils.decryptRSA(passRecord.title)
        passRecord.text = CryptoUtils.decryptRSA(passRecord.text)
        return passRecord
    }
}