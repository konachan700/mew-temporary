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
import java.security.interfaces.ECPrivateKey
import org.spongycastle.jcajce.provider.asymmetric.util.EC5Util
import org.spongycastle.jce.ECPointUtil
import java.security.SecureRandom.getSeed
import org.spongycastle.jcajce.provider.asymmetric.util.EC5Util.getCurve
import org.spongycastle.jce.ECNamedCurveTable
import org.spongycastle.math.ec.ECCurve
import org.spongycastle.jce.spec.ECNamedCurveParameterSpec
import org.spongycastle.util.BigIntegers


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
        val data = "bb08ab1c268f36aa22c1edf0f0637d11e6abee4e6ea25fc7c1e20b9bbeb030969c68f96a5a27bd26e62264c4f863b1c89581caec1ed11cbd5d3488bccc939125"
        val bytes = Hex.decode(data)
        Assert.assertEquals(bytes.size, 64)

        val key = getECDHP256PublicKeyFromBinary(bytes)
        System.out.println(key.encoded.toHexString())
        Assert.assertNotNull(key)
    }

    fun decodeKey(encoded: ByteArray): ECPublicKey {
        val params = ECNamedCurveTable.getParameterSpec("secp256k1")
        val fact = KeyFactory.getInstance("ECDSA", "BC")
        val curve = params.curve
        val ellipticCurve = EC5Util.convertCurve(curve, params.seed)
        val point = ECPointUtil.decodePoint(ellipticCurve, encoded)
        val params2 = EC5Util.convertSpec(ellipticCurve, params)
        val keySpec = java.security.spec.ECPublicKeySpec(point, params2)
        return fact.generatePublic(keySpec) as ECPublicKey
    }

    @Test
    fun ecdh2() {
        val pubkey = Hex.decode("E74E4A7070288F501BFF100D1918D73CA460437A47F5E4D1DB55E5B3252CD59B5ECD11A8ACAF68F910306EB8F671238E7E18E15C4A656454D4B2B49CE90D9672")
        val privkeyLE = Hex.decode("DE7DBBA05691FBAEC1FE39C2EA4302F59A876B491949BA33E20E766140E4FE9F")
        val privkeyBE1 = ByteArray(32)
        val privkeyBE2 = ByteArray(32)

        for (i in 0..31 step 4) {
            privkeyBE1[i+0] = privkeyLE[31-(i + 0)]
            privkeyBE1[i+1] = privkeyLE[31-(i + 1)]
            privkeyBE1[i+2] = privkeyLE[31-(i + 2)]
            privkeyBE1[i+3] = privkeyLE[31-(i + 3)]
        }

//        for (i in 0..31) {
//            privkeyBE1[i] = privkeyLE[31-i]
//        }


        val pk = BigIntegers.fromUnsignedByteArray(privkeyBE1)

//        val privkeyLEeyBE1 = ByteArray(32)
//        val privkeyBE2 = ByteArray(32)


//        for (i in 0..31 step 4) {
//            privkeyBE1[i+0] = privkeyLE[i + 3]
//            privkeyBE1[i+1] = privkeyLE[i + 2]
//            privkeyBE1[i+2] = privkeyLE[i + 1]
//            privkeyBE1[i+3] = privkeyLE[i + 0]
//        }

//        for (i in 0..31) {
//            privkeyBE2[i] = privkeyBE1[31-i]
//        }

        Assert.assertEquals(pubkey.size, 64)
        Assert.assertEquals(privkeyLE.size, 32)

        val parameters = AlgorithmParameters.getInstance("EC")
        parameters.init(ECGenParameterSpec("secp256r1"))
        val ecParameterSpec = parameters.getParameterSpec(ECParameterSpec::class.java)
        val ecPrivateKeySpec = ECPrivateKeySpec(pk, ecParameterSpec)
        val privateKey = KeyFactory.getInstance("EC").generatePrivate(ecPrivateKeySpec) as ECPrivateKey

        val ecp = SECNamedCurves.getByName("secp256r1")
        val domainParams = ECDomainParameters(ecp.curve, ecp.g, ecp.n, ecp.h, ecp.seed)
        val Q = domainParams.g.multiply(privateKey.s)

        //System.err.println(privkeyBE2.toHexString())
        System.err.println(pubkey.toHexString())
        System.err.print(Q.getEncoded(false).copyOfRange(1, 65).toHexString())
    }
}
