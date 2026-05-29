package com.tantrus332.bankcards.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tantrus332.bankcards.entity.BankCard;
import com.tantrus332.bankcards.exception.BusinessLogicException;
import com.tantrus332.bankcards.repository.BankCardRepository;
import com.tantrus332.bankcards.service.UserService;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import com.tantrus332.bankcards.entity.User;

@ExtendWith(MockitoExtension.class)
class BankCardServiceImplTest {
  @Mock private BankCardRepository bankCardRepository;

  @Mock private UserService userService;

  @InjectMocks private BankCardServiceImpl bankCardService;

  private MockedStatic<SecurityContextHolder> securityContextMock;
  private SecurityContext securityContext;
  private Authentication authentication;

  @BeforeEach
  void setUp() {
    securityContextMock = mockStatic(SecurityContextHolder.class);
    securityContext = mock(SecurityContext.class);
    authentication = mock(Authentication.class);

    securityContextMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);

    when(authentication.getName()).thenReturn("ivan");
  }

  @AfterEach
  void tearDown() {
    securityContextMock.close();
  }

  @Test
  void transfer_Success() {
    String fromCardNum = "1111";
    String toCardNum = "2222";
    BigDecimal amount = new BigDecimal("100.00");

    User mockUser = new User();
    mockUser.setCardHolderName("Ivan Ivanov");

    BankCard fromCard = new BankCard();
    fromCard.setUser(mockUser);
    fromCard.setId(1L);
    fromCard.setBalance(new BigDecimal("1000.00"));
    fromCard.setActivatedAt(java.time.OffsetDateTime.now()); // Активна
    fromCard.setExpirationYear(2099);
    fromCard.setExpirationMonth(12);

    BankCard toCard = new BankCard();
    toCard.setUser(mockUser);
    toCard.setId(2L);
    toCard.setBalance(new BigDecimal("500.00"));
    toCard.setActivatedAt(java.time.OffsetDateTime.now());
    toCard.setExpirationYear(2099);
    toCard.setExpirationMonth(12);

    when(bankCardRepository.findByUserUsernameAndCardNumber("ivan", fromCardNum))
        .thenReturn(Optional.of(fromCard));
    when(bankCardRepository.findByUserUsernameAndCardNumber("ivan", toCardNum))
        .thenReturn(Optional.of(toCard));

    bankCardService.transfer(fromCardNum, toCardNum, amount);

    assertEquals(new BigDecimal("900.00"), fromCard.getBalance());
    assertEquals(new BigDecimal("600.00"), toCard.getBalance());

    verify(bankCardRepository, times(2)).save(any(BankCard.class));
  }

  @Test
  void transfer_InsufficientFunds_ThrowsException() {
    String fromCardNum = "1111";
    String toCardNum = "2222";
    BigDecimal amount = new BigDecimal("5000.00");

    BankCard fromCard = new BankCard();
    fromCard.setBalance(new BigDecimal("100.00"));
    fromCard.setActivatedAt(java.time.OffsetDateTime.now());
    fromCard.setExpirationYear(2099);
    fromCard.setExpirationMonth(12);

    BankCard toCard = new BankCard();
    toCard.setActivatedAt(java.time.OffsetDateTime.now());
    toCard.setExpirationYear(2099);
    toCard.setExpirationMonth(12);

    when(bankCardRepository.findByUserUsernameAndCardNumber("ivan", fromCardNum))
        .thenReturn(Optional.of(fromCard));
    when(bankCardRepository.findByUserUsernameAndCardNumber("ivan", toCardNum))
        .thenReturn(Optional.of(toCard));

    // Ожидаем BusinessLogicException
    assertThrows(
        BusinessLogicException.class,
        () -> bankCardService.transfer(fromCardNum, toCardNum, amount));

    verify(bankCardRepository, never()).save(any());
  }
}
