package com.mewhpm.mewsync.Utils;

import android.security.keystore.KeyGenParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.x500.X500Principal;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import static android.security.keystore.KeyProperties.*;
import static java.util.Calendar.YEAR;

public class CryptoUtils {
    private static final String KEYSTORE_ALIAS = "localhost-mew";
    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String RSA_MODE = "RSA/ECB/PKCS1Padding";
    private static final String CIPHER_PROVIDER = "AndroidKeyStoreBCWorkaround";
    private static final int PURPOSES = PURPOSE_DECRYPT | PURPOSE_ENCRYPT | PURPOSE_SIGN | PURPOSE_VERIFY;

    private static final KeyStore keyStore;
    static {
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
            keyStore.load(null);
            //keyStore.deleteEntry(KEYSTORE_ALIAS); // COMMENT THIS LINE AFTER DEBUG
            if (!keyStore.containsAlias(KEYSTORE_ALIAS)) {
                final KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM_RSA, ANDROID_KEYSTORE);
                final Calendar endDate = Calendar.getInstance();
                endDate.add(YEAR, 8);

                keyGenerator.initialize(
                        new KeyGenParameterSpec.Builder(KEYSTORE_ALIAS, PURPOSES)
                                .setKeyValidityStart(new Date())
                                .setKeyValidityEnd(endDate.getTime())
                                .setCertificateSubject(new X500Principal("CN=Android, O=Android Authority"))
                                .setCertificateSerialNumber(BigInteger.valueOf(new Random().nextLong()))
                                .setDigests(DIGEST_SHA256, DIGEST_SHA512)
                                .setKeySize(4096)
                                .setEncryptionPaddings(ENCRYPTION_PADDING_RSA_PKCS1)
                                .setSignaturePaddings(SIGNATURE_PADDING_RSA_PKCS1)
                                .build());
                keyGenerator.genKeyPair();
            }
        } catch (KeyStoreException | CertificateException | NoSuchProviderException |
                NoSuchAlgorithmException | IOException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static byte[] streamToByteArray(Cipher cipher, byte[] data) throws IOException {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        final CipherOutputStream cipherStream = new CipherOutputStream(byteStream, cipher);
        cipherStream.write(data);
        cipherStream.close();
        return byteStream.toByteArray();
    }

    public static String sha256(String data) throws NoSuchAlgorithmException {
        return sha256(data.getBytes());
    }

    public static String sha256(byte[] data) throws NoSuchAlgorithmException {
        final MessageDigest md = MessageDigest.getInstance("SHA-256");
        final byte[] digest = md.digest(data);
        final StringBuilder sb = new StringBuilder();
        for (byte aDigest : digest) {
            sb.append(String.format("%02x", aDigest));
        }
        return sb.toString();
    }

    public static byte[] encryptRSA4k(byte[] openData) throws KeyStoreException, UnrecoverableEntryException,
            NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IOException {
        if (!keyStore.containsAlias(KEYSTORE_ALIAS))
            throw new IllegalStateException("Keystore not contain a keypair with alias " + KEYSTORE_ALIAS);

        final KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(KEYSTORE_ALIAS, null);
        final Cipher cipher = Cipher.getInstance(RSA_MODE, CIPHER_PROVIDER);
        cipher.init(Cipher.ENCRYPT_MODE, privateKeyEntry.getCertificate().getPublicKey());

        return streamToByteArray(cipher, openData);
    }

    public static byte[] decryptRSA4k(byte[] encryptedData) throws KeyStoreException, UnrecoverableEntryException,
            NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IOException {
        if (!keyStore.containsAlias(KEYSTORE_ALIAS))
            throw new IllegalStateException("Keystore not contain a keypair with alias $KEYSTORE_ALIAS");

        final KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(KEYSTORE_ALIAS, null);
        final Cipher cipher = Cipher.getInstance(RSA_MODE, CIPHER_PROVIDER);
        cipher.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());

        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        final CipherInputStream cipherStream = new CipherInputStream(new ByteArrayInputStream(encryptedData), cipher);
        while (true) {
            int b = cipherStream.read();
            if (b == -1) break;
            buffer.write(b);
        }
        return buffer.toByteArray();
    }

    public static String getUniqueSalt() throws NoSuchAlgorithmException,
            KeyStoreException, UnrecoverableEntryException, CertificateEncodingException {
        if (!keyStore.containsAlias(KEYSTORE_ALIAS))
            throw new IllegalStateException("Keystore not contain a keypair with alias $KEYSTORE_ALIAS");
        final KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(KEYSTORE_ALIAS, null);
        final String certHash = sha256(privateKeyEntry.getCertificate().getEncoded());
        final String keyHash = sha256(privateKeyEntry.getCertificate().getPublicKey().getEncoded());
        return sha256(certHash + keyHash);
    }
}
