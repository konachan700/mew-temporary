package com.mewhpm.mewsync

import com.mewhpm.mewsync.dao.KnownDevicesDao
import org.junit.Assert
import org.junit.Test

class KnownDevicesDaoTest {
    @Test
    fun isDeviceZero() {
        Assert.assertTrue(KnownDevicesDao.isDeviceZero("00:00:00:00:00:00"))
        Assert.assertFalse(KnownDevicesDao.isDeviceZero("00:00:00:00:01:00"))
        Assert.assertFalse(KnownDevicesDao.isDeviceZero("FF:01:04:01:33:21"))
    }

    @Test
    fun isDeviceBroken() {
        Assert.assertTrue(KnownDevicesDao.isDeviceBroken("00:00:00:00:00:00"))
        Assert.assertTrue(KnownDevicesDao.isDeviceBroken("FF:FF:FF:FF:FF:FF"))
        Assert.assertTrue(KnownDevicesDao.isDeviceBroken("00:00:00:00:00:33"))
        Assert.assertTrue(KnownDevicesDao.isDeviceBroken("FF:FF:FF:FF:FF:77"))
        Assert.assertFalse(KnownDevicesDao.isDeviceBroken("FF:01:04:01:33:21"))
    }

    @Test
    fun zeroMac() {
        Assert.assertEquals(KnownDevicesDao.ZERO_MAC, "00:00:00:00:00:00")
    }
}