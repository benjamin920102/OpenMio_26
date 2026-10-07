package me.mioclient.network.crypto;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AesKeyStore {
    public static SecretKey secretKey;
    public static IvParameterSpec ivParameterSpec;

    public static void reset() {
        secretKey = null;
        ivParameterSpec = null;
    }

    public static SecretKey getSecretKey() {
        return secretKey;
    }

    public static IvParameterSpec getIv() {
        return ivParameterSpec;
    }

    public static void setKeys(byte[] bArr, byte[] bArr2) {
        secretKey = new SecretKeySpec(bArr, "AES");
        ivParameterSpec = new IvParameterSpec(bArr2);
    }

    public static boolean hasKeys() {
        return secretKey != null;
    }
}
