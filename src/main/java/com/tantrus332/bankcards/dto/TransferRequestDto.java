package com.tantrus332.bankcards.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class TransferRequestDto {
  @NotBlank private String fromCardNumber;

  @NotBlank private String toCardNumber;

  @NotNull
  @DecimalMin("0.01")
  private BigDecimal amount;
}
