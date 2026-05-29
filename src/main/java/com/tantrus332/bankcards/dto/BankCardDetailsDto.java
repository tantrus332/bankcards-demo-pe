package com.tantrus332.bankcards.dto;

import com.tantrus332.bankcards.entity.BankCard;
import com.tantrus332.bankcards.util.BankCardStatus;
import java.math.BigDecimal;
import java.time.YearMonth;
import lombok.Data;

@Data
public class BankCardDetailsDto {
  private Long id;
  private String cardNumber;
  private YearMonth expirationDate;
  private BankCardStatus status;
  private BigDecimal balance;
  private String cardHolderName;

  public BankCardDetailsDto(BankCard bankCard) {
    id = bankCard.getId();
    cardNumber = bankCard.getCardNumber();
    expirationDate = bankCard.getExpirationYearMonth();
    status = bankCard.getStatus();
    balance = bankCard.getBalance();
    cardHolderName = bankCard.getUser().getCardHolderName();
  }
}
