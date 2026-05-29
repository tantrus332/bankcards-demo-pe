package com.tantrus332.bankcards.service.impl;

import com.tantrus332.bankcards.dto.BankCardDetailsDto;
import com.tantrus332.bankcards.dto.BankCardDto;
import com.tantrus332.bankcards.dto.BankCardUpdateDto;
import com.tantrus332.bankcards.dto.TransferResponseDto;
import com.tantrus332.bankcards.entity.BankCard;
import com.tantrus332.bankcards.entity.User;
import com.tantrus332.bankcards.exception.BusinessLogicException;
import com.tantrus332.bankcards.exception.ResourceNotFoundException;
import com.tantrus332.bankcards.repository.BankCardRepository;
import com.tantrus332.bankcards.service.BankCardService;
import com.tantrus332.bankcards.service.UserService;
import com.tantrus332.bankcards.util.BankCardStatus;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BankCardServiceImpl implements BankCardService {
  private final BankCardRepository bankCardRepository;
  private final UserService userService;

  @Override
  public BankCardDto get(Long id) {
    BankCard bankCard =
        bankCardRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Bank card not found"));

    return new BankCardDto(bankCard);
  }

  @Override
  @Transactional
  public BankCardDto create(Long ownerUserId) {
    User owner = userService.getUser(ownerUserId);
    BankCard bankCard = new BankCard();

    bankCard.setBalance(BigDecimal.ZERO);
    bankCard.setActivatedAt(OffsetDateTime.now());
    bankCard.setUser(owner);

    YearMonth yearMonth = YearMonth.now().plusYears(3);
    bankCard.setExpirationMonth(yearMonth.getMonthValue());
    bankCard.setExpirationYear(yearMonth.getYear());
    bankCard.setFullCardNumber(generateRandomCardNumber());

    bankCard = bankCardRepository.save(bankCard);
    return new BankCardDto(bankCard);
  }

  protected String generateRandomCardNumber() {
    String cardNumber;
    do {
      cardNumber = "4000" + String.format("%012d", new java.util.Random().nextLong(100000000000L));
    } while (bankCardRepository.existsByCardNumber(cardNumber));
    return cardNumber;
  }

  @Override
  public Page<BankCardDto> getMyCards(Pageable pageable, String cardNumberLast4) {
    String username = currentUsername();
    Page<BankCard> result =
        bankCardRepository.findAllByUserUsernameAndCardNumberLast4Containing(
            pageable, username, cardNumberLast4);
    return result.map(BankCardDto::new);
  }

  @Override
  public Page<BankCardDto> getAll(Pageable pageable, String cardNumberLast4) {
    Page<BankCard> result =
        bankCardRepository.findAllByCardNumberLast4Containing(pageable, cardNumberLast4);
    return result.map(BankCardDto::new);
  }

  @Override
  @Transactional
  public BankCardDto update(Long cardId, BankCardUpdateDto dto) {
    BankCard bankCard =
        bankCardRepository
            .findById(cardId)
            .orElseThrow(() -> new ResourceNotFoundException("Bank card not found"));

    bankCard.setBalance(dto.getBalance());
    bankCard.setExpirationMonth(dto.getExpirationDate().getMonthValue());
    bankCard.setExpirationYear(dto.getExpirationDate().getYear());
    bankCard.setFullCardNumber(dto.getCardNumber());
    bankCard.setUser(userService.getUser(dto.getUserId()));

    bankCard = bankCardRepository.save(bankCard);

    return new BankCardDto(bankCard);
  }

  @Override
  @Transactional
  public void delete(Long id) {
    bankCardRepository.deleteById(id);
  }

  @Override
  @Transactional
  public BankCardDto requestBlock(Long cardId) {
    BankCard bankCard = findMyCard(cardId);

    if (bankCard.getStatus() != BankCardStatus.ACTIVE) {
      throw new BusinessLogicException("Card is not active");
    }

    if (bankCard.getBlockRequestedAt() != null) {
      throw new BusinessLogicException("Block request is already sent");
    }

    bankCard.setBlockRequestedAt(OffsetDateTime.now());
    bankCard = bankCardRepository.save(bankCard);

    return new BankCardDto(bankCard);
  }

  @Override
  @Transactional
  public BankCardDto confirmBlock(Long cardId) {
    BankCard bankCard =
        bankCardRepository
            .findById(cardId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Card with id " + cardId + " not found"));

    bankCard.setActivatedAt(null);
    bankCard.setBlockRequestedAt(null);
    bankCard = bankCardRepository.save(bankCard);

    return new BankCardDto(bankCard);
  }

  protected String currentUsername() {
    return SecurityContextHolder.getContext().getAuthentication().getName();
  }

  protected BankCard findMyCard(Long cardId) {
    String username = currentUsername();
    return bankCardRepository
        .findByUserUsernameAndId(username, cardId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Your card with id " + cardId + " not found"));
  }

  protected BankCard findMyCardByCardNumber(String cardNumber) {
    String username = currentUsername();
    return bankCardRepository
        .findByUserUsernameAndCardNumber(username, cardNumber)
        .orElseThrow(
            () -> new ResourceNotFoundException("Card with number " + cardNumber + " not found"));
  }

  @Override
  @Transactional
  public BankCardDto activate(Long cardId) {
    BankCard bankCard =
        bankCardRepository
            .findById(cardId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Bank card with id " + cardId + " not found"));

    bankCard.setActivatedAt(OffsetDateTime.now());
    bankCard = bankCardRepository.save(bankCard);

    return new BankCardDto(bankCard);
  }

  @Override
  @Transactional
  public TransferResponseDto transfer(
      String fromCardNumber, String toCardNumber, BigDecimal amount) {
    BankCard fromCard = findMyCardByCardNumber(fromCardNumber);
    BankCard toCard = findMyCardByCardNumber(toCardNumber);

    if (fromCard.getStatus() != BankCardStatus.ACTIVE
        || toCard.getStatus() != BankCardStatus.ACTIVE) {
      throw new BusinessLogicException("Both cards must be active");
    }
    if (fromCard.getBalance().compareTo(amount) < 0) {
      throw new BusinessLogicException("Insufficient funds");
    }

    fromCard.setBalance(fromCard.getBalance().subtract(amount));
    toCard.setBalance(toCard.getBalance().add(amount));

    bankCardRepository.save(fromCard);
    bankCardRepository.save(toCard);

    return new TransferResponseDto(fromCard, toCard);
  }

  @Override
  public Page<BankCardDto> getCardsWithBlockRequest(Pageable pageable, String cardNumberLast4) {
    return bankCardRepository
        .findAllByCardNumberLast4ContainingAndActivatedAtIsNotNull(pageable, cardNumberLast4)
        .map(BankCardDto::new);
  }

  @Override
  public BankCardDetailsDto getFullDetails(Long cardId) {
    BankCard card =
        bankCardRepository
            .findById(cardId)
            .orElseThrow(() -> new ResourceNotFoundException("Card not found"));

    if (!card.getUser().getUsername().equals(currentUsername())) {
      throw new ResourceNotFoundException("Your card with id " + cardId + " not found");
    }

    return new BankCardDetailsDto(card);
  }
}
