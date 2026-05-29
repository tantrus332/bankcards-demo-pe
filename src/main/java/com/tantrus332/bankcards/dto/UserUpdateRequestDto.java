package com.tantrus332.bankcards.dto;

import com.tantrus332.bankcards.util.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserUpdateRequestDto {
  @NotBlank private String username;

  private String password;

  @NotNull private UserRole role;

  @NotBlank private String cardHolderName;
}
