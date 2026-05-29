package com.tantrus332.bankcards.service;

import com.tantrus332.bankcards.dto.UserDto;
import com.tantrus332.bankcards.dto.UserStoreDto;
import com.tantrus332.bankcards.dto.UserUpdateRequestDto;
import com.tantrus332.bankcards.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
  public User createUser(UserStoreDto userStoreDto);

  public User updateUser(UserUpdateRequestDto userStoreDto, Long id);

  public Page<UserDto> getUsers(Pageable pageable);

  public User getUser(Long id);

  public User getUserByUsername(String username);

  public void deleteUser(Long id);
}
