package com.mewhpm.mewsync.data

import java.io.Serializable

data class PassRecordMetadata (
    var url: String = "",
    var login: String = ""
) : Serializable