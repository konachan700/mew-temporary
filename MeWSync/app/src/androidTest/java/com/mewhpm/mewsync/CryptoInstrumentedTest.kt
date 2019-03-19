package com.mewhpm.mewsync

import android.util.Log
import androidx.test.runner.AndroidJUnit4
import com.mewhpm.mewsync.utils.CryptoUtils
import com.mewhpm.mewsync.utils.toHexString
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.spongycastle.jce.interfaces.ECPrivateKey
import org.spongycastle.math.ec.custom.sec.SecP256R1Curve
import org.spongycastle.util.encoders.Hex
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.AlgorithmParameters
import java.security.KeyFactory
import java.security.Security
import java.security.interfaces.ECPublicKey
import java.security.spec.*

@RunWith(AndroidJUnit4::class)
class CryptoInstrumentedTest {
    companion object {
        init {
            Security.insertProviderAt(org.spongycastle.jce.provider.BouncyCastleProvider(), 1)
        }
    }

    @Test
    fun rsa4k_test() {
        val testString = "Test 12345 Test 12345 Test 12345 Test 12345 Test 12345 Test 12345 Test 12345 Test 12345"
        val encrypted = CryptoUtils.encryptRSA(testString.toByteArray())
        val decrypted = CryptoUtils.decryptRSA(encrypted)

        assertEquals(testString, decrypted.toString(StandardCharsets.UTF_8))
    }

    @Test
    fun sha256_test() {
        val originalHash = "9822b4afe3b5e3e15aa3b2145926f937dedb26c849414982b7d7b17a920bc2ac"
        val hash = CryptoUtils.sha256("83741623874782548235453284567354715635223457354234534")

        assertEquals(originalHash, hash)
    }

    @Test
    fun getUniqueSalt_test() {
        val salt1 = CryptoUtils.getUniqueSalt()
        val salt2 = CryptoUtils.getUniqueSalt()

        assertNotNull(salt1)
        assertTrue(salt1.isNotBlank())
        assertEquals(salt1, salt2)
    }

    @Test
    fun ecdhp256_2_test() {
        val pubkey = Hex.decode("5000e64b19196213be92412aaf3108e253ad6afb2c7e6da63721bec9147e4af2cdf2da620f17483587b401d40d445c5db2042fbe1fdea4e76c099446734678e2")
        val privkey = Hex.decode("3ccbcd4f50925977d4db2edf8cf8f4584930599138a59f7ac98f8e0d36fed706")

        Assert.assertEquals(pubkey.size, 64)
        Assert.assertEquals(privkey.size, 32)

        val x = ByteArray(32)
        val y = ByteArray(32)

        System.arraycopy(pubkey, 1, x, 0, 32)
        System.arraycopy(pubkey, 32, y, 0, 32)

        val kf = KeyFactory.getInstance("EC")
        val parameters = AlgorithmParameters.getInstance("EC")
        parameters.init(ECGenParameterSpec("prime256v1"))

        val bx = BigInteger(1, x)
        val by = BigInteger(1, y)

        val privb = BigInteger(1, privkey)

        val ecParameterSpec = parameters.getParameterSpec(ECParameterSpec::class.java)
        val ecPublicKeySpec = ECPublicKeySpec(ECPoint(bx, by), ecParameterSpec)
        val ecpriv = ECPrivateKeySpec(privb, ecParameterSpec)
        val pubkey1 =  kf.generatePublic(ecPublicKeySpec) as ECPublicKey
        val privKey = kf.generatePrivate(ecpriv) as ECPrivateKey

        Assert.assertNotNull(pubkey1)
        Assert.assertNotNull(privKey)


    }

    @Test
    fun ecdhp256_test() {
        val data = "bb08ab1c268f36aa22c1edf0f0637d11e6abee4e6ea25fc7c1e20b9bbeb030969c68f96a5a27bd26e62264c4f863b1c89581caec1ed11cbd5d3488bccc939125"
        val bytes = Hex.decode(data)
        Assert.assertEquals(bytes.size, 64)

        val x = ByteArray(32)
        val y = ByteArray(32)

        System.arraycopy(bytes, 1, x, 0, 32)
        System.arraycopy(bytes, 32, y, 0, 32)

        Log.e("ecdhp256_test", "x = " + x.toHexString())
        Log.e("ecdhp256_test", "y = " + y.toHexString())

        val kf = KeyFactory.getInstance("EC")
        val parameters = AlgorithmParameters.getInstance("EC")
        parameters.init(ECGenParameterSpec("prime256v1"))

        val bx = BigInteger(1, x)
        val by = BigInteger(1, y)

        Log.e("ecdhp256_test", "bxcompareTo = " + bx.compareTo(SecP256R1Curve.q))
//        Log.e("ecdhp256_test", "by = " + y.toHexString())

        val ecParameterSpec = parameters.getParameterSpec(ECParameterSpec::class.java)
        val ecPublicKeySpec = ECPublicKeySpec(ECPoint(bx, by), ecParameterSpec)
        val pubkey =  kf.generatePublic(ecPublicKeySpec) as ECPublicKey

        Assert.assertNotNull(pubkey)








//        val key = CryptoUtils.getECDHP256PublicKeyFromBinary(bytes)
//        System.out.println(key.encoded.toHexString())
//        Assert.assertNotNull(key)
    }
}
