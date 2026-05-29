package com.tantrus332.bankcards.dto;

import com.tantrus332.bankcards.util.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStoreDto {
  @NotBlank(message = "Username is required")
  private String username;

  @NotBlank(message = "Card holder name is required")
  private String cardHolderName;

  @NotNull(message = "Role is required")
  private UserRole role;

  @NotBlank(message = "Password is required")
  private String password;
}
