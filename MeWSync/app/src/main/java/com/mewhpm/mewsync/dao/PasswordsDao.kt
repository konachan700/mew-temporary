package com.mewhpm.mewsync.dao

import com.mewhpm.mewsync.utils.CryptoUtils.decryptRSA
import com.mewhpm.mewsync.utils.CryptoUtils.encryptRSA
import com.mewhpm.mewsync.data.PasswordFolder
import com.mewhpm.mewsync.data.PasswordRecord

class PasswordsDao {
    companion object {
        val encryptStrings = true
    }




    private fun encryptPasswordRecord(pr : PasswordRecord) : PasswordRecord {
        return if (encryptStrings) PasswordRecord(pr.id, encryptRSA(pr.url), encryptRSA(pr.name), encryptRSA(pr.login)) else pr
    }

    private fun decryptPasswordRecord(pr : PasswordRecord) : PasswordRecord {
        return if (encryptStrings) PasswordRecord(pr.id,  decryptRSA(pr.url), decryptRSA(pr.name), decryptRSA(pr.login)) else pr
    }

    private fun encryptPasswordFolder(pf : PasswordFolder) : PasswordFolder {
        return if (encryptStrings) PasswordFolder(pf.id, encryptRSA(pf.name), pf.device, pf.parent, pf.children) else pf
    }

    private fun decryptPasswordFolder(pf : PasswordFolder) : PasswordFolder {
        return if (encryptStrings) PasswordFolder(pf.id, decryptRSA(pf.name), pf.device, pf.parent, pf.children) else pf
    }
}