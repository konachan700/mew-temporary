package com.mewhpm.mewsync.Utils;

import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.util.Base64;
import com.mewhpm.mewsync.data.BleDevice;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import static android.security.keystore.KeyProperties.*;
import static java.util.Calendar.YEAR;

public class CryptoUtils {
    private final static String SALT = "qvh87349fv78b87vv8243f1873402vb87f6V^RDS^#$S@^#$A$@A%$DF&^Vob789b^^%*V^Rx5ex45X&%_HJfe";

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

    public static byte[] sha512(byte[] b) {
        final MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-512");
            md.update(SALT.getBytes());
            md.update(b);
            return md.digest();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-512 not allowed on this PC\nError: " + ex.getMessage());
        }
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

    public static String encryptRSA4k(String openData) throws KeyStoreException, UnrecoverableEntryException,
            NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IOException {
        final byte[] openDataBytes = openData.getBytes(StandardCharsets.UTF_8);
        final byte[] encrypted = encryptRSA4k(openDataBytes);
        return Base64.encodeToString(encrypted, Base64.DEFAULT);
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

    public static String decryptRSA4k(String encryptedData) throws KeyStoreException, UnrecoverableEntryException,
            NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IOException {
        final byte[] encrypted = Base64.decode(encryptedData, Base64.DEFAULT);
        final byte[] decrypted = decryptRSA4k(encrypted);
        return new String(decrypted, StandardCharsets.UTF_8);
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

    @SuppressWarnings("unused")
    private static String generatePrefName(BleDevice device) {
        return "MewHPMAuthData-" + device.getMac().replace(':','0');
    }

    @SuppressWarnings("unused")
    public static String bytesToHexString(byte[] bytes) {
        final StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static boolean verifyPinCode(SharedPreferences pref, String enteredPincode, BleDevice device) throws NoSuchPaddingException,
            InvalidKeyException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException, UnrecoverableEntryException, IOException {
        final String stored = pref.getString(generatePrefName(device), "NO-PIN");
        final String decoded = decryptRSA4k(stored);
        return decoded.contentEquals(enteredPincode);
    }

    public static void createPinCode(SharedPreferences pref, String enteredPincode, BleDevice device) throws NoSuchPaddingException,
            InvalidKeyException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException, UnrecoverableEntryException, IOException {
        final String base64EncryptedPin = encryptRSA4k(enteredPincode);
        final SharedPreferences.Editor editor = pref.edit();
        editor.putString(generatePrefName(device), base64EncryptedPin);
        editor.apply();
    }

    public static byte[] aes256Decrypt(byte[] value, String password) throws BadPaddingException, InvalidKeyException,
            NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException, InvalidAlgorithmParameterException {
        final byte[] hash = sha512(password.getBytes());
        final byte[] key = Arrays.copyOfRange(hash, 0, 32);
        final byte[] iv = Arrays.copyOfRange(hash, 32, 48);
        //System.out.println("aes256Decrypt " + Hex.encodeHexString(hash));
        return aes256Decrypt(value, key, iv);
    }

    public static byte[] aes256Encrypt(byte[] value, String password) throws BadPaddingException, InvalidKeyException,
            NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException, InvalidAlgorithmParameterException {
        final byte[] hash = sha512(password.getBytes());
        final byte[] key = Arrays.copyOfRange(hash, 0, 32);
        final byte[] iv = Arrays.copyOfRange(hash, 32, 48);
        //System.out.println("aes256Encrypt " + Hex.encodeHexString(hash));
        return aes256Encrypt(value, key, iv);
    }

    private static byte[] aes256Decrypt(byte[] value, byte[] key, byte[] iv) throws InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException {
            final SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(iv));
            return cipher.doFinal(value);

    }

    private static byte[] aes256Encrypt(byte[] value, byte[] key, byte[] iv)  throws InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException {
            final SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(iv));
            return cipher.doFinal(value);
    }
}
