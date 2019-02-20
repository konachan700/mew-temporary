package com.mewhpm.mewsync.data

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import java.util.*

@DatabaseTable(tableName = "PassRecord")
class PassRecord  {
    constructor(nodeType: Long) {
        this.nodeType = nodeType
    }

    constructor()

    companion object {
        const val TYPE_GO_TO_PARENT = 0L
        const val TYPE_FOLDER = 100L
        const val TYPE_RECORD = 200L
        const val TYPE_BROKEN = 9999999L

        private const val EMPTY = ""
    }

    @DatabaseField(columnName = "id", generatedId = true)
    var id: Long = 0

    @DatabaseField
    var parentId: Long = 0

    @DatabaseField
    var nodeType: Long = TYPE_FOLDER

    @DatabaseField
    var title: String = EMPTY

    @DatabaseField
    var text: String = EMPTY

    @DatabaseField
    var timestamp: Long = Date().time

    @DatabaseField
    var metadataJson: String = EMPTY

    @DatabaseField
    var hwUID: Long = 0L

    @DatabaseField
    var deviceAddr: String = EMPTY
}