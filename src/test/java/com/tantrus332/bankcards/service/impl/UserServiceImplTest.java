package com.tantrus332.bankcards.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tantrus332.bankcards.dto.UserStoreDto;
import com.tantrus332.bankcards.entity.User;
import com.tantrus332.bankcards.exception.BusinessLogicException;
import com.tantrus332.bankcards.repository.UserRepository;
import com.tantrus332.bankcards.util.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
  @Mock private UserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private UserServiceImpl userService;

  @Test
  void createUser_Success() {
    UserStoreDto dto =
        UserStoreDto.builder()
            .username("newuser")
            .password("secret123")
            .cardHolderName("Ivanov Ivan")
            .role(UserRole.USER)
            .build();

    when(userRepository.existsByUsername("newuser")).thenReturn(false);
    when(passwordEncoder.encode("secret123")).thenReturn("encoded_secret_hash");
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    User createdUser = userService.createUser(dto);

    // Проверки
    assertEquals("encoded_secret_hash", createdUser.getPassword());
    assertEquals("newuser", createdUser.getUsername());
    verify(userRepository).save(any(User.class));
  }

  @Test
  void createUser_DuplicateUsername_ThrowsException() {
    UserStoreDto dto = UserStoreDto.builder().username("existingUser").build();

    when(userRepository.existsByUsername("existingUser")).thenReturn(true);

    assertThrows(BusinessLogicException.class, () -> userService.createUser(dto));

    verify(userRepository, times(0)).save(any());
  }
}
