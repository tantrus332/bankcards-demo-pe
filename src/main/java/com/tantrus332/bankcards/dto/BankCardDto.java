package com.tantrus332.bankcards.dto;

import com.tantrus332.bankcards.entity.BankCard;
import com.tantrus332.bankcards.util.BankCardStatus;
import java.math.BigDecimal;
import java.time.YearMonth;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class BankCardDto {
  private Long id;
  private String cardNumber;
  private YearMonth expirationDate;
  private BankCardStatus status;
  private BigDecimal balance;
  private String cardHolderName;

  public BankCardDto(BankCard bankCard) {
    id = bankCard.getId();
    cardNumber = "**** **** **** " + bankCard.getCardNumberLast4();
    expirationDate = YearMonth.of(bankCard.getExpirationYear(), bankCard.getExpirationMonth());
    status = bankCard.getStatus();
    balance = bankCard.getBalance();
    cardHolderName = bankCard.getUser().getCardHolderName();
  }
}
