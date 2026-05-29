package com.tantrus332.bankcards.service;

import com.tantrus332.bankcards.dto.AuthRequestDto;
import com.tantrus332.bankcards.dto.AuthResponseDto;

public interface AuthService {
  AuthResponseDto login(AuthRequestDto requestDto);
}
