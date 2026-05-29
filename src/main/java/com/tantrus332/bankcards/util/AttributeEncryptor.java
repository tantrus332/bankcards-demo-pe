package com.tantrus332.bankcards.util;

import com.tantrus332.bankcards.config.SecurityProperties;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.security.Key;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Converter
@AllArgsConstructor
public class AttributeEncryptor implements AttributeConverter<String, String> {
  private static final String encryptionAlgorithm = "AES";
  private final SecurityProperties props;

  @Override
  public String convertToDatabaseColumn(String attribute) {
    if (attribute == null) return null;
    try {
      Key key = new SecretKeySpec(props.getEncryptionKey().getBytes(), encryptionAlgorithm);
      Cipher cipher = Cipher.getInstance(encryptionAlgorithm);
      cipher.init(Cipher.ENCRYPT_MODE, key);
      return Base64.getEncoder().encodeToString(cipher.doFinal(attribute.getBytes()));
    } catch (Exception e) {
      throw new RuntimeException("Error encrypting card number", e);
    }
  }

  @Override
  public String convertToEntityAttribute(String dbData) {
    if (dbData == null) return null;
    try {
      Key key = new SecretKeySpec(props.getEncryptionKey().getBytes(), encryptionAlgorithm);
      Cipher cipher = Cipher.getInstance(encryptionAlgorithm);
      cipher.init(Cipher.DECRYPT_MODE, key);
      return new String(cipher.doFinal(Base64.getDecoder().decode(dbData)));
    } catch (Exception e) {
      throw new RuntimeException("Error decrypting card number", e);
    }
  }
}
