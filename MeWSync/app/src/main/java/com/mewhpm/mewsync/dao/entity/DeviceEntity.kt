package com.mewhpm.mewsync.dao.entity

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "DeviceEntity")
class DeviceEntity {
    constructor()
    constructor(id: Long, mac: String?, name: String, type: String?, url: String?) {
        this.id = id
        this.mac = mac
        this.name = name
        this.type = type
        this.url = url
    }

    @DatabaseField(columnName = "id", generatedId = true)
    var id: Long = 0

    @DatabaseField(canBeNull = true)
    var mac: String? = null

    @DatabaseField(canBeNull = false, throwIfNull = true)
    var name: String = ""

    @DatabaseField(canBeNull = true)
    var type: String? = null

    @DatabaseField(canBeNull = true)
    var url: String? = null
}