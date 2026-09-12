package me.mioclient.network.crypto;

import javax.crypto.Cipher;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/AesPayloadCipher.class */
public class AesPayloadCipher {
    public static byte[] encrypt(byte[] bArr) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(1, AesKeyStore.getSecretKey(), AesKeyStore.getIv());
            byte[] doFinal = cipher.doFinal(bArr);
            reverseInPlace(doFinal);
            return doFinal;
        } catch (Exception e) { throw new java.lang.RuntimeException(e); }
    }

    public static byte[] decrypt(byte[] bArr) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(2, AesKeyStore.getSecretKey(), AesKeyStore.getIv());
            reverseInPlace(bArr);
            return cipher.doFinal(bArr);
        } catch (Exception e) { throw new java.lang.RuntimeException(e); }
    }

    public static void reverseInPlace(byte[] bArr) {
        if (bArr != null) {
            int length = bArr.length - 1;
            for (int i = 0; length > i; i++) {
                byte b = bArr[length];
                bArr[length] = bArr[i];
                bArr[i] = b;
                length--;
            }
        }
    }
}
