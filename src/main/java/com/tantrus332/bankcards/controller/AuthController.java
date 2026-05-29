package com.tantrus332.bankcards.controller;

import com.tantrus332.bankcards.dto.AuthRequestDto;
import com.tantrus332.bankcards.dto.AuthResponseDto;
import com.tantrus332.bankcards.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Авторизация")
public class AuthController {
  private final AuthService authService;

  @PostMapping
  @Operation(summary = "Авторизация по JWT Bearer", security = @SecurityRequirement(name = "none"))
  public AuthResponseDto login(@RequestBody AuthRequestDto authRequestDto) {
    return authService.login(authRequestDto);
  }
}
