package com.mewhpm.mewsync.Utils

import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.util.*
import kotlin.random.Random
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.security.KeyStore
import java.util.Calendar.YEAR
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.security.auth.x500.X500Principal


class Crypto {
    companion object {
        const val KEYSTORE_ALIAS = "localhost-mew"
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
        const val RSA_MODE = "RSA/ECB/PKCS1Padding"
        const val CIPHER_PROVIDER = "AndroidKeyStoreBCWorkaround"
        const val PURPOSES = PURPOSE_DECRYPT or PURPOSE_ENCRYPT or PURPOSE_SIGN or PURPOSE_VERIFY

        var testMode = false

        private var INSTANCE: Crypto? = null
        fun getInstance() : Crypto {
            if (INSTANCE == null) INSTANCE = Crypto()
            return INSTANCE!!
        }
    }

    private val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)!!
    init {
        keyStore.load(null)
    }

    fun sha256(data: String): String {
        val bytes = data.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    private fun createRSAKeys() {
        if (testMode) keyStore.deleteEntry(KEYSTORE_ALIAS)
        if (!keyStore.containsAlias(KEYSTORE_ALIAS)) {
            val keyGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM_RSA, ANDROID_KEYSTORE)
            val endDate = Calendar.getInstance()
            endDate.add(YEAR, 8)

            keyGenerator.initialize(
                KeyGenParameterSpec.Builder(KEYSTORE_ALIAS, PURPOSES)
                    .setKeyValidityStart(Date())
                    .setKeyValidityEnd(endDate.time)
                    .setCertificateSubject(X500Principal("CN=Android, O=Android Authority"))
                    .setCertificateSerialNumber(Random.nextLong().toBigInteger())
                    .setDigests(DIGEST_SHA256, DIGEST_SHA512)
                    .setKeySize(4096)
                    .setEncryptionPaddings(ENCRYPTION_PADDING_RSA_PKCS1)
                    .setSignaturePaddings(SIGNATURE_PADDING_RSA_PKCS1)
                    //.setRandomizedEncryptionRequired(false)
                    .build())
            keyGenerator.genKeyPair()
        }
    }

    fun encryptRSA4k(openData: ByteArray): ByteArray {
        createRSAKeys()

        val privateKeyEntry = keyStore.getEntry(KEYSTORE_ALIAS, null) as KeyStore.PrivateKeyEntry
        val cipher = Cipher.getInstance(RSA_MODE, CIPHER_PROVIDER)
        cipher.init(Cipher.ENCRYPT_MODE, privateKeyEntry.certificate.publicKey)

        val byteStream = ByteArrayOutputStream()
        val cipherStream = CipherOutputStream(byteStream, cipher)
        cipherStream.write(openData)
        cipherStream.close()

        return byteStream.toByteArray()
    }

    fun decryptRSA4k(encryptedData: ByteArray): ByteArray {
        if (!keyStore.containsAlias(KEYSTORE_ALIAS))
            throw IllegalStateException("Keystore not contain a keypair with alias $KEYSTORE_ALIAS")

        val privateKeyEntry = keyStore.getEntry(KEYSTORE_ALIAS, null) as KeyStore.PrivateKeyEntry
        val cipher = Cipher.getInstance(RSA_MODE, CIPHER_PROVIDER)
        cipher.init(Cipher.DECRYPT_MODE, privateKeyEntry.privateKey)

        val cipherStream = CipherInputStream(ByteArrayInputStream(encryptedData), cipher)
        return cipherStream.readBytes()
    }
}