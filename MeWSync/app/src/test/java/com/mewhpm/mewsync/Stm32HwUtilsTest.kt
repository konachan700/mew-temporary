package com.mewhpm.mewsync

import android.util.Base64
import com.mewhpm.mewsync.utils.Stm32HwUtils
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.*
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(Base64::class)
class Stm32HwUtilsTest {

    @Before
    fun init() {
        PowerMockito.mockStatic(Base64::class.java)
    }

    @ExperimentalUnsignedTypes
    @Test
    fun stm32CRC32() {
        val crc = Stm32HwUtils.checksum(ubyteArrayOf(0u, 0u, 0u, 0u, 0u, 0u, 0u, 0u))
        System.out.println("original:\t0x437700FF;\ngenerated:\t0x"+crc.toString(16).toUpperCase())
        Assert.assertEquals(0x437700FFu, crc)
    }

    @ExperimentalUnsignedTypes
    @Test
    fun createPacket() {
        PowerMockito.`when`(Base64.encode(any(), anyInt())).thenAnswer { invocation ->
            java.util.Base64.getEncoder().encode(invocation.arguments[0] as ByteArray)
        }

        PowerMockito.`when`(Base64.decode(anyString(), anyInt())).thenAnswer { invocation ->
            java.util.Base64.getMimeDecoder().decode(invocation.arguments[0] as String)
        }

        val expected: List<UByte> = listOf(
            0x43, 0x77, 0x00, 0x01, 0x00, 0x08, 0x43, 0x77, 0x00, 0xFF, 0, 0, 0, 0, 0, 0, 0, 0
        ).map { it.toUByte() }
        val packet = Stm32HwUtils.createPacket(0x01, java.util.Base64.getEncoder().encodeToString(
            byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0))).map { it.toUByte() }
        Assert.assertEquals(expected.size, packet.size)
        Assert.assertArrayEquals(expected.toTypedArray(), packet.toTypedArray())
    }
}