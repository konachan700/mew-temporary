package com.mewhpm.mewsync.security

import java.io.ByteArrayOutputStream
import java.lang.StringBuilder
import java.security.MessageDigest
import java.security.SecureRandom
import kotlin.math.min

class LocalPasswordProviderImpl : PasswordProvider {
    companion object {
        const val ITERATION_COUNT = 64
        const val SALT = "wegr8h7^^%FR&%C5786rtv8i7o8b79yuvbwevf723v86fc5e5dx%$#@AZ@#%AXCTUVB*)N"

        private val numbers = arrayListOf('1', '2', '3', '4', '5', '6', '7', '8', '9', '0')
        private val symbols = arrayListOf('~', '`', '!', '@', '#', '$', '%', '^', '&', '*', '<', '>',
            '(', ')', '-', '_', '+', '=', '.', ',', '"', '\'', '|', '/', '?', ' ', ':', ';')
        private val alphabetLower = arrayListOf(
            'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o',
            'p', 'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'z', 'x', 'c', 'v', 'b', 'n', 'm')
        private val alphabetUpper = alphabetLower.map { it.toUpperCase() }
    }

    public var minLen = 16
    public var maxLen = 24
    public var containDigits = true
    public var containSymbols = true
    public var containLowercaseAlphabet = true
    public var containUppercaseAlphabet = true

    private fun hash(prev: ByteArray?, passId: Long, deviceId: String, counter: Int): ByteArray {
        val md = MessageDigest.getInstance("SHA-512")
        if (prev != null) md.update(prev)
        md.update(SALT.toByteArray())
        md.update("passID:$passId;counter:$counter/${counter % 11}/${counter % 3}".toByteArray())
        md.update(deviceId.toByteArray())
        md.update(SALT.toByteArray())
        return md.digest()
    }

    private fun dia(value : Byte, max : Int) : Byte {
        return (value.toDouble().minus(Byte.MIN_VALUE.toDouble()))
            .div(Byte.MAX_VALUE.toDouble() - Byte.MIN_VALUE.toDouble()).times(max.minus(1).toDouble()).toByte()
    }

    override fun generatePassword(passId: Long, deviceId: String) : String {
        val baos = ByteArrayOutputStream()
        var tmpHash: ByteArray? = null
        for (i in 0..ITERATION_COUNT) {
            tmpHash = hash(tmpHash, passId, deviceId, i)
            baos.write(tmpHash)
        }

        val list = ArrayList<Char>()
        if (containDigits) list.addAll(numbers)
        if (containSymbols) list.addAll(symbols)
        if (containLowercaseAlphabet) list.addAll(alphabetLower)
        if (containUppercaseAlphabet) list.addAll(alphabetUpper)
        if (list.size > Byte.MAX_VALUE) throw IllegalStateException("Password may contain maximum ${Byte.MAX_VALUE} different chars!")

        val normIndexArray = baos.toByteArray().map { dia(it, list.size) }
        val count = dia(tmpHash!![0], (maxLen - minLen)).plus(minLen)

        val sb = StringBuilder()
        for (i in 0..count) {
            sb.append(list[normIndexArray[i % normIndexArray.size].toInt()])
        }

        return sb.toString()
    }
}