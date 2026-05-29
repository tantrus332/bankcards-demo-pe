package com.tantrus332.bankcards.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.YearMonth;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BankCardUpdateDto {
  private String cardNumber;

  @NotNull private YearMonth expirationDate;

  @NotNull private Long userId;

  @NotNull private BigDecimal balance;
}
