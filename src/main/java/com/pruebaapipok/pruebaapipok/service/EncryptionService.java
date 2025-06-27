package com.pruebaapipok.pruebaapipok.service;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class EncryptionService {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String SECRET_KEY_STRING = "ThisIsASecretKey"; // 16 bytes for AES-128
    private static final String IV_STRING = "RandomInitVector";     // 16 bytes

    private SecretKeySpec secretKeySpec;
    private IvParameterSpec ivParameterSpec;

    public EncryptionService() throws Exception {
        byte[] keyBytes = SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8);
        byte[] ivBytes = IV_STRING.getBytes(StandardCharsets.UTF_8);

        if (keyBytes.length != 16 && keyBytes.length != 24 && keyBytes.length != 32) {
            throw new IllegalArgumentException("Key size must be 16, 24 or 32 bytes for AES");
        }
        if (ivBytes.length != 16) {
            throw new IllegalArgumentException("IV size must be 16 bytes for AES/CBC");
        }

        this.secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        this.ivParameterSpec = new IvParameterSpec(ivBytes);
    }

    public String encrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public String decrypt(String encryptedText) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}