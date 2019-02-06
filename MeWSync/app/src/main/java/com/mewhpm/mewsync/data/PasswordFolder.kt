package com.mewhpm.mewsync.data

data class PasswordFolder (
    val id: Long,
    var name: String,
    val device: BleDevice,
    var parent: PasswordFolder?,
    val children: ArrayList<PasswordFolder> = ArrayList(),
    val records: ArrayList<PasswordRecord> = ArrayList()
)