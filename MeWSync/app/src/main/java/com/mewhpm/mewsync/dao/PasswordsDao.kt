package com.mewhpm.mewsync.dao

import com.mewhpm.mewsync.Utils.CryptoUtils.decryptRSA4k
import com.mewhpm.mewsync.Utils.CryptoUtils.encryptRSA4k
import com.mewhpm.mewsync.data.PasswordFolder
import com.mewhpm.mewsync.data.PasswordRecord

class PasswordsDao {
    companion object {
        val encryptStrings = true
    }




    private fun encryptPasswordRecord(pr : PasswordRecord) : PasswordRecord {
        return if (encryptStrings) PasswordRecord(pr.id, encryptRSA4k(pr.url), encryptRSA4k(pr.name), encryptRSA4k(pr.login)) else pr
    }

    private fun decryptPasswordRecord(pr : PasswordRecord) : PasswordRecord {
        return if (encryptStrings) PasswordRecord(pr.id,  decryptRSA4k(pr.url), decryptRSA4k(pr.name), decryptRSA4k(pr.login)) else pr
    }

    private fun encryptPasswordFolder(pf : PasswordFolder) : PasswordFolder {
        return if (encryptStrings) PasswordFolder(pf.id, encryptRSA4k(pf.name), pf.device, pf.parent, pf.children) else pf
    }

    private fun decryptPasswordFolder(pf : PasswordFolder) : PasswordFolder {
        return if (encryptStrings) PasswordFolder(pf.id, decryptRSA4k(pf.name), pf.device, pf.parent, pf.children) else pf
    }
}