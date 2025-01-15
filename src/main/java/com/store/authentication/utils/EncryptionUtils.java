package com.store.authentication.utils;

import com.store.authentication.config.KeywordsAndConstants;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Base64;
import org.springframework.stereotype.Component;

@Component
public class EncryptionUtils {

    public static String encrypt(String data) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(
                getAESKeyFromSecretKey(KeywordsAndConstants.SECRET_KEY_FOR_PASSWORD_ENCRYPTION),
                KeywordsAndConstants.ALGORITHM
        );
        Cipher cipher = Cipher.getInstance(KeywordsAndConstants.ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encryptedData = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    public static String decrypt(String encryptedData) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(
                getAESKeyFromSecretKey(KeywordsAndConstants.SECRET_KEY_FOR_PASSWORD_ENCRYPTION),
                KeywordsAndConstants.ALGORITHM
        );
        Cipher cipher = Cipher.getInstance(KeywordsAndConstants.ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedData);
    }

    private static byte[] getAESKeyFromSecretKey(String secretKey) throws Exception {
        MessageDigest digest = MessageDigest.getInstance(KeywordsAndConstants.MESSAGE_DIGEST_ALGORITHM);
        return digest.digest(secretKey.getBytes(KeywordsAndConstants.UTF_8));
    }
}


