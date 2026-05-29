package com.tantrus332.bankcards.service.impl;

import com.tantrus332.bankcards.dto.UserDto;
import com.tantrus332.bankcards.dto.UserStoreDto;
import com.tantrus332.bankcards.dto.UserUpdateRequestDto;
import com.tantrus332.bankcards.entity.User;
import com.tantrus332.bankcards.exception.BusinessLogicException;
import com.tantrus332.bankcards.exception.ResourceNotFoundException;
import com.tantrus332.bankcards.repository.UserRepository;
import com.tantrus332.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public User createUser(UserStoreDto userStoreDto) {
    if (userRepository.existsByUsername(userStoreDto.getUsername())) {
      throw new BusinessLogicException(
          "User with username " + userStoreDto.getUsername() + " already exists");
    }

    User user =
        User.builder()
            .username(userStoreDto.getUsername())
            .cardHolderName(userStoreDto.getCardHolderName())
            .password(passwordEncoder.encode(userStoreDto.getPassword()))
            .role(userStoreDto.getRole())
            .build();
    return userRepository.save(user);
  }

  @Override
  public Page<UserDto> getUsers(Pageable pageable) {
    Page<User> userPage = userRepository.findAll(pageable);
    return userPage.map(UserDto::new);
  }

  @Override
  public User updateUser(UserUpdateRequestDto userDto, Long id) {
    User user = userRepository.findById(id).orElseThrow();
    user.setCardHolderName(userDto.getCardHolderName());
    user.setRole(userDto.getRole());

    String requestUsername = userDto.getUsername();
    if (!user.getUsername().equals(requestUsername)) {
      if (userRepository.existsByUsername(requestUsername)) {
        throw new BusinessLogicException("Username taken " + requestUsername);
      }

      user.setUsername(requestUsername);
    }

    if (userDto.getPassword() != null) {
      user.setPassword(passwordEncoder.encode(userDto.getPassword()));
    }

    return userRepository.save(user);
  }

  @Override
  public User getUser(Long id) {
    return userRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
  }

  @Override
  public void deleteUser(Long id) {
    userRepository.deleteById(id);
  }

  @Override
  public User getUserByUsername(String username) {
    return userRepository
        .findByUsername(username)
        .orElseThrow(
            () -> new ResourceNotFoundException("User with username " + username + " not found"));
  }
}
