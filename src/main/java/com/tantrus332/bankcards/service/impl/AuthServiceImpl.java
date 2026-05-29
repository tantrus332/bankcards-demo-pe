package com.tantrus332.bankcards.service.impl;

import com.tantrus332.bankcards.dto.AuthRequestDto;
import com.tantrus332.bankcards.dto.AuthResponseDto;
import com.tantrus332.bankcards.repository.UserRepository;
import com.tantrus332.bankcards.service.AuthService;
import com.tantrus332.bankcards.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepository;

  @Override
  public AuthResponseDto login(AuthRequestDto requestDto) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            requestDto.getUsername(), requestDto.getPassword()));

    var user =
        userRepository
            .findByUsername(requestDto.getUsername())
            .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
    var accessToken = jwtService.generateToken(user);

    return AuthResponseDto.builder().accessToken(accessToken).build();
  }
}
