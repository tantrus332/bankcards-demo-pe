package com.tantrus332.bankcards.dto;

import com.tantrus332.bankcards.entity.BankCard;
import lombok.Data;

@Data
public class TransferResponseDto {
  private BankCardDto fromCard;
  private BankCardDto toCard;

  public TransferResponseDto(BankCard fromCard, BankCard toCard) {
    this.fromCard = new BankCardDto(fromCard);
    this.toCard = new BankCardDto(toCard);
  }
}
