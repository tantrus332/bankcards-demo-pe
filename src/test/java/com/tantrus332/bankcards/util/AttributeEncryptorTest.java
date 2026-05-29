package com.tantrus332.bankcards.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.tantrus332.bankcards.config.SecurityProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AttributeEncryptorTest {
  private AttributeEncryptor encryptor;
  private SecurityProperties securityProperties;

  @BeforeEach
  void setUp() {
    securityProperties = new SecurityProperties();
    securityProperties.setEncryptionKey("1234567890123456");

    encryptor = new AttributeEncryptor(securityProperties);
  }

  @Test
  void convertToDatabaseColumn_ShouldEncryptData() {
    String originalData = "4444555566667777";

    String encryptedData = encryptor.convertToDatabaseColumn(originalData);

    assertNotNull(encryptedData);
    assertNotEquals(
        originalData, encryptedData, "Зашифрованные данные не должны совпадать с исходными");

    System.out.println("Encrypted output: " + encryptedData);
  }

  @Test
  void convertToEntityAttribute_ShouldDecryptData() {
    String originalData = "4444555566667777";

    String encryptedData = encryptor.convertToDatabaseColumn(originalData);

    String decryptedData = encryptor.convertToEntityAttribute(encryptedData);

    assertEquals(originalData, decryptedData, "Расшифрованные данные должны совпадать с исходными");
  }

  @Test
  void encryption_ShouldFail_WithWrongKeyLength() {
    securityProperties.setEncryptionKey("short");
    AttributeEncryptor badEncryptor = new AttributeEncryptor(securityProperties);

    assertThrows(
        RuntimeException.class,
        () -> {
          badEncryptor.convertToDatabaseColumn("data");
        });
  }
}
