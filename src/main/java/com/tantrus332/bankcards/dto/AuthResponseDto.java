package com.tantrus332.bankcards.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AuthResponseDto {
  private String accessToken;
}
