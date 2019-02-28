package com.mewhpm.mewsync

import android.util.Log
import com.mewhpm.mewsync.dao.KnownDevicesDao
import com.mewhpm.mewsync.security.LocalPasswordProviderImpl
import com.mewhpm.mewsync.security.PasswordProvider
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertFalse
import org.junit.Assert
import org.junit.Test

class PasswordProviderTest {
    @Test
    fun testGenerate() {
        val p : PasswordProvider = LocalPasswordProviderImpl()
        val pass1 = p.generatePassword(0, KnownDevicesDao.ZERO_MAC)
        System.err.println("password[${pass1.length}] = $pass1")
        val pass2 = p.generatePassword(0, KnownDevicesDao.ZERO_MAC)
        System.err.println("password[${pass2.length}] = $pass2")
        val pass3 = p.generatePassword(7657658, KnownDevicesDao.ZERO_MAC)
        System.err.println("password[${pass3.length}] = $pass3")
        val pass4 = p.generatePassword(3587464, KnownDevicesDao.ZERO_MAC)
        System.err.println("password[${pass4.length}] = $pass4")
        assertEquals(pass1, pass2)
        assertFalse(pass2.contentEquals(pass3))
    }

    @Test
    fun testGenerateSize() {
        val p = LocalPasswordProviderImpl()
        for (i in 0..10000) {
            val pass = p.generatePassword(i.toLong(), KnownDevicesDao.ZERO_MAC)
            assertFalse(pass.length < p.minLen)
            assertFalse(pass.length > p.maxLen)
        }
    }
}