package com.mewhpm.mewsync.utils;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.security.keystore.KeyProtection;
import android.util.Base64;
import android.util.Log;
import com.mewhpm.mewsync.data.BleDevice;
import kotlin.Pair;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.LongBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.interfaces.ECPublicKey;
import java.security.spec.*;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.security.keystore.KeyProperties.*;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Calendar.YEAR;

public class CryptoUtils {

    public static final String MEW_IV = "mew_iv";
    public static final String MEW_KEY_256 = "mew_key256";

    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    private static KeyStore keyStore;

    public interface KeygenEvent {
        void onGenerated();
    }

    private static class AsyncKeyGenerator implements Runnable {
        {
            try {
                keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
                keyStore.load(null);
            } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
                throw new IllegalStateException("Can't load android keystore!");
            }
        }

        private final AtomicBoolean keyGenerated = new AtomicBoolean(false);
        private KeygenEvent keygenEvent = null;

        public void checkGenerated(KeygenEvent ke) {
            synchronized (keyGenerated) {
                if (keyGenerated.get()) {
                    ke.onGenerated();
                    return;
                }
                keygenEvent = ke;
            }
        }

        public KeyStore getKeyStore() {
            if (!keyGenerated.get()) throw new IllegalStateException("getKeyStore NOT GENERATED");
            return keyStore;
        }

        @Override
        public void run() {
            try {
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
                                    .setKeySize(2048)
                                    .setEncryptionPaddings(ENCRYPTION_PADDING_RSA_PKCS1)
                                    .setSignaturePaddings(SIGNATURE_PADDING_RSA_PKCS1)
                                    .build());
                    keyGenerator.genKeyPair();
                }
            } catch (KeyStoreException | NoSuchProviderException | NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
                throw new RuntimeException(e.getMessage());
            }

            synchronized (keyGenerated) {
                keyGenerated.set(true);
                if (keygenEvent != null) keygenEvent.onGenerated();
            }
        }
    }

    private final static String SALT = "qvh87349fv78b87vv8243f1873402vb87f6V^RDS^#$S@^#$A$@A%$DF&^Vob789b^^%*V^Rx5ex45X&%_HJfe";

    private static final String KEYSTORE_ALIAS = "localhost-mew";
    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String RSA_MODE = "RSA/ECB/PKCS1Padding";
    private static final String CIPHER_PROVIDER = "AndroidKeyStoreBCWorkaround";
    private static final int PURPOSES = PURPOSE_DECRYPT | PURPOSE_ENCRYPT | PURPOSE_SIGN | PURPOSE_VERIFY;

    private static final AsyncKeyGenerator generator = new AsyncKeyGenerator();
    private static final Thread generatorThread = new Thread(generator);
    private static String salt = null;

    static {
        generatorThread.start();
    }

    public static void checkGenerated(KeygenEvent ke) {
        if (ke == null) throw new NullPointerException("KeygenEvent cannot be null");
        generator.checkGenerated(ke);
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

    public static String encryptRSA(String openData) throws KeyStoreException, UnrecoverableEntryException,
            NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IOException {
        final byte[] openDataBytes = openData.getBytes(StandardCharsets.UTF_8);
        final byte[] encrypted = encryptRSA(openDataBytes);
        return Base64.encodeToString(encrypted, Base64.DEFAULT);
    }

    public static byte[] encryptRSA(byte[] openData) throws KeyStoreException, UnrecoverableEntryException,
            NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IOException {
        if (!generator.getKeyStore().containsAlias(KEYSTORE_ALIAS))
            throw new IllegalStateException("Keystore not contain a keypair with alias " + KEYSTORE_ALIAS);

        final KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) generator.getKeyStore().getEntry(KEYSTORE_ALIAS, null);
        final Cipher cipher = Cipher.getInstance(RSA_MODE, CIPHER_PROVIDER);
        cipher.init(Cipher.ENCRYPT_MODE, privateKeyEntry.getCertificate().getPublicKey());

        return streamToByteArray(cipher, openData);
    }

    public static String decryptRSA(String encryptedData) throws KeyStoreException, UnrecoverableEntryException,
            NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IOException {
        final byte[] encrypted = Base64.decode(encryptedData, Base64.DEFAULT);
        final byte[] decrypted = decryptRSA(encrypted);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    public static byte[] decryptRSA(byte[] encryptedData) throws KeyStoreException, UnrecoverableEntryException,
            NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IOException {
        if (!generator.getKeyStore().containsAlias(KEYSTORE_ALIAS))
            throw new IllegalStateException("Keystore not contain a keypair with alias $KEYSTORE_ALIAS");

        final KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) generator.getKeyStore().getEntry(KEYSTORE_ALIAS, null);
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

    public static String getUniqueSalt() throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableEntryException, CertificateEncodingException {
        if (salt == null) {
            if (!generator.getKeyStore().containsAlias(KEYSTORE_ALIAS))
                throw new IllegalStateException("Keystore not contain a keypair with alias $KEYSTORE_ALIAS");
            final KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) generator.getKeyStore().getEntry(KEYSTORE_ALIAS, null);
            final String certHash = sha256(privateKeyEntry.getCertificate().getEncoded());
            final String keyHash = sha256(privateKeyEntry.getCertificate().getPublicKey().getEncoded());
            salt = sha256(certHash + keyHash);
        }
        return salt;
    }

    @SuppressWarnings("unused")
    private static String generatePrefName(BleDevice device) {
        return generatePrefName(device.getMac());
    }

    @SuppressWarnings("unused")
    private static String generatePrefName(String mac) {
        return "MewHPMAuthData-" + mac.replace(':','0');
    }

    @SuppressWarnings("unused")
    public static String bytesToHexString(byte[] bytes) {
        final StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static boolean isPinPresent(SharedPreferences pref, String devMac) {
        return pref.getString(generatePrefName(devMac), null) != null;
    }

    public static boolean verifyPinCode(SharedPreferences pref, String enteredPincode, String devMac) throws NoSuchPaddingException,
            InvalidKeyException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException, UnrecoverableEntryException, IOException {
        final String stored = pref.getString(generatePrefName(devMac), null);
        if (stored == null) throw new IllegalStateException("Pin not present");

        final String decoded = decryptRSA(stored);
        return decoded.contentEquals(enteredPincode);
    }

    public static boolean verifyPinCode(SharedPreferences pref, String enteredPincode, BleDevice device) throws NoSuchPaddingException,
            InvalidKeyException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException, UnrecoverableEntryException, IOException {
        final String stored = pref.getString(generatePrefName(device), null);
        if (stored == null) throw new IllegalStateException("Pin not present");

        final String decoded = decryptRSA(stored);
        return decoded.contentEquals(enteredPincode);
    }

    public static void createPinCode(SharedPreferences pref, String enteredPincode, BleDevice device) throws NoSuchPaddingException,
            InvalidKeyException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException, UnrecoverableEntryException, IOException {
        final String base64EncryptedPin = encryptRSA(enteredPincode);
        final SharedPreferences.Editor editor = pref.edit();
        editor.putString(generatePrefName(device), base64EncryptedPin);
        editor.apply();
    }

    @SuppressLint("ApplySharedPref")
    public static void pinCodeRemove(BleDevice device, SharedPreferences sp) {
        sp.edit()
                .remove(generatePrefName(device))
                .commit();
    }

    public static boolean isPinCodePresent(String mac, SharedPreferences sp) {
        return sp.contains(generatePrefName(mac));
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

    public static KeyPair generateECDHP256Keypair() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        final KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
        final ECGenParameterSpec genParams = new ECGenParameterSpec("secp256r1");
        kpg.initialize(genParams);
        final KeyPair kp = kpg.generateKeyPair();
        return kp;
    }

    private static String saveAesKeyToKeystoreGenName(String name, String mac) {
        return name + "_" + mac.replace(':','X');
    }

    @SuppressLint("ApplySharedPref")
    public static void saveAesKeyAndIVToKeystore(byte[] rawKey, byte[] rawIv, String mac, SharedPreferences sharedPreferences)
            throws NoSuchAlgorithmException, KeyStoreException, NoSuchPaddingException,
            UnrecoverableEntryException, InvalidKeyException, NoSuchProviderException, IOException {
        final String base64EncryptedIV = Base64.encodeToString(encryptRSA(rawIv), Base64.DEFAULT);
        final String base64EncryptedKey = Base64.encodeToString(encryptRSA(rawKey), Base64.DEFAULT);
        sharedPreferences
                .edit()
                .putString(saveAesKeyToKeystoreGenName(MEW_IV, mac), base64EncryptedIV)
                .putString(saveAesKeyToKeystoreGenName(MEW_KEY_256, mac), base64EncryptedKey)
                .commit();
    }

    private static byte[] aes256DevCrypt(String mac, SharedPreferences sp, byte[] value, int dir) throws NoSuchPaddingException,
            InvalidKeyException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException, UnrecoverableEntryException,
            IOException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException {
        if (sp.contains(saveAesKeyToKeystoreGenName(MEW_IV, mac)) &&
                sp.contains(saveAesKeyToKeystoreGenName(MEW_KEY_256, mac))) {
            final String ivB64 = sp.getString(saveAesKeyToKeystoreGenName(MEW_IV, mac), null);
            final String keyB64 = sp.getString(saveAesKeyToKeystoreGenName(MEW_KEY_256, mac), null);
            if (ivB64 == null || keyB64 == null) throw new IllegalStateException("Device not found");

            final byte[] ivEnc = Base64.decode(ivB64, Base64.DEFAULT);
            final byte[] keyEnc = Base64.decode(keyB64, Base64.DEFAULT);
            if (ivEnc == null || keyEnc == null) throw new IllegalStateException("Device not found");

            final byte[] iv = decryptRSA(ivEnc);
            final byte[] key = decryptRSA(keyEnc);

            return (dir == 0) ? aes256Decrypt(value, key, iv) : aes256Encrypt(value, key, iv);
        } else {
            throw new IllegalStateException("Device not found");
        }
    }

    public static boolean isAes256DevKeyPresent(String mac, SharedPreferences sp) {
        return (sp.contains(saveAesKeyToKeystoreGenName(MEW_IV, mac)) &&
                sp.contains(saveAesKeyToKeystoreGenName(MEW_KEY_256, mac)));
    }

    public static void aes256DevKeyRemove(String mac, SharedPreferences sp) {
        sp.edit()
                .remove(saveAesKeyToKeystoreGenName(MEW_IV, mac))
                .remove(saveAesKeyToKeystoreGenName(MEW_KEY_256, mac))
                .apply();
    }

    public static byte[] aes256DevEncrypt(String mac, SharedPreferences sp, byte[] value) throws NoSuchPaddingException,
            InvalidKeyException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException, UnrecoverableEntryException,
            IOException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException {
        return aes256DevCrypt(mac, sp, value, 1);
    }

    public static byte[] aes256DevDecrypt(String mac, SharedPreferences sp, byte[] value) throws NoSuchPaddingException,
            InvalidKeyException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException, UnrecoverableEntryException,
            IOException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException {
        return aes256DevCrypt(mac, sp, value, 0);
    }




//    public static ECPublicKey getECDHP256PublicKeyFromBinary(byte[] bytes) throws NoSuchAlgorithmException, InvalidParameterSpecException, InvalidKeySpecException {
//        byte[] x = new byte[32];
//        byte[] y = new byte[32];
//
//        System.arraycopy(bytes, 1, x, 0, 32);
//        System.arraycopy(bytes, 32, y, 0, 32);
//
//        final KeyFactory kf = KeyFactory.getInstance("EC");
//        final AlgorithmParameters parameters = AlgorithmParameters.getInstance("EC");
//        parameters.init(new ECGenParameterSpec("prime256v1"));
//
//        final ECParameterSpec ecParameterSpec = parameters.getParameterSpec(ECParameterSpec.class);
//        final ECPublicKeySpec ecPublicKeySpec = new ECPublicKeySpec(
//                new ECPoint(
//                        new BigInteger(1, x), new BigInteger(1, y)), ecParameterSpec);
//        return (ECPublicKey) kf.generatePublic(ecPublicKeySpec);
//    }
//
//    public static byte[] getBytesFromECPublicKey(ECPublicKey key) {
//        final ECPoint point = key.getW();
//        final byte[] binaryKey = new byte[64];
//        for (int i=0; i<binaryKey.length; i++) binaryKey[i] = 0;
//
//        final byte[] x = point.getAffineX().toByteArray();
//        System.arraycopy(x, max(0, x.length - 32), binaryKey, 32 - min(x.length, 32), min(x.length, 32));
//
//        final byte[] y = point.getAffineY().toByteArray();
//        System.arraycopy(y, max(0, y.length - 32), binaryKey, 64 - min(y.length, 32), min(y.length, 32));
//
//        return binaryKey;
//    }
}
