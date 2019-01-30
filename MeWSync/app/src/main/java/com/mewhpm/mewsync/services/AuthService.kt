package com.mewhpm.mewsync.services

import android.app.IntentService
import android.content.Intent
import java.io.File
import java.util.*

class AuthService : IntentService(AuthService::class.java.simpleName) {
    companion object {
        const val AUTH_LOGIN = 1
        const val AUTH_LOGOUT = 2
        const val AUTH_VERIFY = 3

        const val AUTH_OP_TYPE = "auth_op_type"
        const val AUTH_FIELD_PASSWORD = "pc"

        const val AUTH_RESULT_CODE = "result_code"
        const val AUTH_VERIFY_OK = 1
        const val AUTH_VERIFY_ERROR = 2

        const val TIMEOUT: Long = 1000 * 60 * 10 // TODO: add setting field for this
    }

    var _hash = ""
    var _lastAccess: Long = 0

    override fun onHandleIntent(intent: Intent?) {
        if (intent == null) return
        if (!intent.hasExtra(AUTH_OP_TYPE)) return

        when (intent.getIntExtra(AUTH_OP_TYPE, 0)) {
            AUTH_LOGIN -> {
                if (!intent.hasExtra(AUTH_FIELD_PASSWORD)) return
                val hashFromUser = intent.getStringExtra(AUTH_FIELD_PASSWORD)






                _lastAccess = Date().time + TIMEOUT

            }
            AUTH_LOGOUT -> {
                _lastAccess = 0
                _hash = ""
            }
            AUTH_VERIFY -> {
                val date = Date().time
                if (date > _lastAccess || _hash.isBlank()) {
                    _hash = ""
                    sendVerify(AUTH_VERIFY_ERROR)
                }
                _lastAccess = date + TIMEOUT
                sendVerify(AUTH_VERIFY_OK)
            }
        }
    }

    private fun generateTestFile() {
        val hashFromDisk = File(this.applicationContext.filesDir.absolutePath + File.separator + "data.mew").readText()
    }

    private fun sendVerify(result: Int) {
        val intentAnswer = Intent()
        intentAnswer.action = this::class.java.simpleName
        intentAnswer.addCategory(Intent.CATEGORY_DEFAULT)
        intentAnswer.putExtra(AUTH_RESULT_CODE, result)
        sendBroadcast(intentAnswer)
    }
}