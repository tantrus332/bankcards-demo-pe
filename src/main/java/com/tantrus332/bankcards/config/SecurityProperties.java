package com.tantrus332.bankcards.config;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "application.security")
@Validated
@Data
public class SecurityProperties {
  @NotNull private String encryptionKey;

  @AssertTrue(message = "application.security.encryption-key must be 16, 24 or 32 length")
  public boolean isKeyLengthValid() {
    if (encryptionKey == null) return false;
    var len = encryptionKey.length();
    return len == 16 || len == 24 || len == 32;
  }
}
