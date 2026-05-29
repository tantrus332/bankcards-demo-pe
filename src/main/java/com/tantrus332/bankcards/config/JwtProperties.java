package com.tantrus332.bankcards.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "application.jwt")
@Data
public class JwtProperties {
  private String secretKey;
  private long expiration;
}
