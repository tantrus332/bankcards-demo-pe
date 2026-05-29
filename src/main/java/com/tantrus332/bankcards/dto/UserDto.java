package com.tantrus332.bankcards.dto;

import com.tantrus332.bankcards.entity.User;
import com.tantrus332.bankcards.util.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class UserDto {
  private Long id;
  private String username;
  private String cardHolderName;
  private UserRole role;

  public UserDto(User user) {
    id = user.getId();
    username = user.getUsername();
    cardHolderName = user.getCardHolderName();
    role = user.getRole();
  }
}
