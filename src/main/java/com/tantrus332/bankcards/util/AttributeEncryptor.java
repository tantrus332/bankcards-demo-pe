package com.tantrus332.bankcards.util;

import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

import com.tantrus332.bankcards.config.SecurityProperties;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.AllArgsConstructor;

@Component
@Converter
@AllArgsConstructor
public class AttributeEncryptor implements AttributeConverter<String, String> {
    private static final String encryptionAlgorithm = "AES/GCM/NoPadding";
    private final SecurityProperties props;

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if(attribute == null) return null;
        try {
            Key key = new SecretKeySpec(props.getEncryptionKey().getBytes(), "AES");
            byte[] iv = new byte[12];
            new SecureRandom().nextBytes(iv);
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            Cipher cipher = Cipher.getInstance(encryptionAlgorithm);
            cipher.init(Cipher.ENCRYPT_MODE, key, spec);
            byte[] cipherText = cipher.doFinal(attribute.getBytes());
            byte[] encrypted = new byte[12 + cipherText.length];
            System.arraycopy(iv, 0, encrypted, 0, 12);
            System.arraycopy(cipherText, 0, encrypted, 12, cipherText.length);
            return Base64.getEncoder().encodeToString(encrypted);
        } catch(Exception e) {
            throw new RuntimeException("Error encrypting card number", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if(dbData == null) return null;
        try {
            byte[] decoded = Base64.getDecoder().decode(dbData);
            Key key = new SecretKeySpec(props.getEncryptionKey().getBytes(), "AES");
            byte[] iv = new byte[12];
            System.arraycopy(decoded, 0, iv, 0, 12);
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            Cipher cipher = Cipher.getInstance(encryptionAlgorithm);
            cipher.init(Cipher.DECRYPT_MODE, key, spec);
            return new String(cipher.doFinal(decoded, 12, decoded.length - 12));
        } catch(Exception e) {
            throw new RuntimeException("Error decrypting card number", e);
        }
    }
}
