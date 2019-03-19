package com.mewhpm.mewsync

import com.mewhpm.mewsync.utils.CryptoUtils
import com.mewhpm.mewsync.utils.toHexString
import org.junit.Assert
import org.junit.Test
import org.spongycastle.util.encoders.Hex
import java.math.BigInteger
import java.security.AlgorithmParameters
import java.security.KeyFactory
import java.security.NoSuchAlgorithmException
import java.security.Security
import java.security.interfaces.ECPublicKey
import java.security.spec.*
import java.security.SecureRandom.getSeed
import org.spongycastle.jcajce.provider.asymmetric.util.EC5Util.getCurve
import org.spongycastle.crypto.params.ECDomainParameters
import org.spongycastle.asn1.sec.SECNamedCurves
import org.spongycastle.asn1.x9.X9ECParameters




class CryptoTest {
    companion object {
        init {
            Security.insertProviderAt(org.spongycastle.jce.provider.BouncyCastleProvider(), 1)
        }
    }

    fun getECDHP256PublicKeyFromBinary(bytes: ByteArray): ECPublicKey {
        val x = ByteArray(32)
        val y = ByteArray(32)

        System.arraycopy(bytes, 1, x, 0, 32)
        System.arraycopy(bytes, 32, y, 0, 32)

        val kf = KeyFactory.getInstance("EC")
        val parameters = AlgorithmParameters.getInstance("EC")
        parameters.init(ECGenParameterSpec("secp256r1"))

        val ecParameterSpec = parameters.getParameterSpec(ECParameterSpec::class.java)
        val ecPublicKeySpec = ECPublicKeySpec(ECPoint(BigInteger(x), BigInteger(y)), ecParameterSpec)
        return kf.generatePublic(ecPublicKeySpec) as ECPublicKey
    }

    @Test
    fun testECDHGenPubKey() {
        Security.insertProviderAt(org.spongycastle.jce.provider.BouncyCastleProvider(), 1)

        val data = "bb08ab1c268f36aa22c1edf0f0637d11e6abee4e6ea25fc7c1e20b9bbeb030969c68f96a5a27bd26e62264c4f863b1c89581caec1ed11cbd5d3488bccc939125"
        val bytes = Hex.decode(data)
        Assert.assertEquals(bytes.size, 64)

        val key = getECDHP256PublicKeyFromBinary(bytes)
        System.out.println(key.encoded.toHexString())
        Assert.assertNotNull(key)
    }

    @Test
    fun ecdh2() {
        val pubkey = Hex.decode("5000e64b19196213be92412aaf3108e253ad6afb2c7e6da63721bec9147e4af2cdf2da620f17483587b401d40d445c5db2042fbe1fdea4e76c099446734678e2")
        val privkey = Hex.decode("3ccbcd4f50925977d4db2edf8cf8f4584930599138a59f7ac98f8e0d36fed706")
        //val privkey2 = Hex.decode("3ccbcd4f50925977d4db2edf8cf8f4584930599138a59f7ac98f8e0d36fed706")

        Assert.assertEquals(pubkey.size, 64)
        Assert.assertEquals(privkey.size, 32)

        val ecp = SECNamedCurves.getByName("secp256r1")
        val domainParams = ECDomainParameters(ecp.curve, ecp.g, ecp.n, ecp.h, ecp.seed)
        val Q = domainParams.g.multiply(BigInteger(1, privkey))

        System.err.println(pubkey.toHexString())
        System.err.print(Q.getEncoded().toHexString())
       // System.err.println(Q.affineYCoord.toBigInteger().toByteArray().toHexString())

        //Assert.assertArrayEquals(pubkey, Q.affineXCoord.toBigInteger().toByteArray())
    }
}
