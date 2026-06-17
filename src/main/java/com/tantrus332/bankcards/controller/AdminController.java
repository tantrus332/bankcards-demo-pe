package com.tantrus332.bankcards.controller;

import com.tantrus332.bankcards.dto.BankCardDto;
import com.tantrus332.bankcards.dto.BankCardUpdateDto;
import com.tantrus332.bankcards.dto.UserDto;
import com.tantrus332.bankcards.dto.UserStoreDto;
import com.tantrus332.bankcards.dto.UserUpdateRequestDto;
import com.tantrus332.bankcards.entity.User;
import com.tantrus332.bankcards.service.BankCardService;
import com.tantrus332.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Администратор", description = "Методы доступные только админу")
public class AdminController {
  private UserService userService;
  private BankCardService bankCardService;

  @PostMapping("/users")
  @Operation(summary = "Регистрация пользователя")
  public UserDto registerUser(@Validated @RequestBody UserStoreDto userStoreDto) {
    User user = userService.createUser(userStoreDto);
    return new UserDto(user);
  }

  @GetMapping("/users")
  @Operation(summary = "Список всех пользователей")
  public Page<UserDto> getUsers(
      @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
    return userService.getUsers(pageable);
  }

  @PutMapping("/users/{id}")
  @Operation(summary = "Редактировать пользователя")
  public UserDto updateUser(
      @Validated @RequestBody UserUpdateRequestDto userStoreDto, @PathVariable Long id) {
    User user = userService.updateUser(userStoreDto, id);
    return new UserDto(user);
  }

  @GetMapping("/users/{id}")
  @Operation(summary = "Получить пользователя")
  public UserDto getUser(@PathVariable Long id) {
    User user = userService.getUser(id);
    return new UserDto(user);
  }

  @DeleteMapping("/users/{id}")
  @Operation(summary = "Удалить пользователя")
  public void deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
  }

  @PostMapping("/bank-cards/{ownerUserId}")
  @Operation(summary = "Создать карту")
  public BankCardDto createCard(@PathVariable Long ownerUserId) {
    return bankCardService.create(ownerUserId);
  }

  @Operation(summary = "Активировать карту")
  @PutMapping("/bank-cards/activate/{cardId}")
  public BankCardDto activateCard(@PathVariable Long cardId) {
    return bankCardService.activate(cardId);
  }

  @Operation(summary = "Удалить карту")
  @DeleteMapping("/bank-cards/{cardId}")
  public void deleteCard(@PathVariable Long cardId) {
    bankCardService.delete(cardId);
  }

  @Operation(summary = "Список карт с запросом на блокировку")
  @GetMapping("/bank-cards/with-block-request")
  public Page<BankCardDto> cardsWithBlockRequest(
      @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
      @RequestParam(defaultValue = "") String cardNumberLast4) {
    return bankCardService.getCardsWithBlockRequest(pageable, cardNumberLast4);
  }

  @Operation(summary = "Блокировка карты")
  @PutMapping("/bank-cards/confirm-block/{cardId}")
  public BankCardDto confirmBlock(@PathVariable Long cardId) {
    return bankCardService.confirmBlock(cardId);
  }

  @Operation(summary = "Поиск по всем картам")
  @GetMapping("/bank-cards")
  public Page<BankCardDto> getCards(
      @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
      @RequestParam(defaultValue = "") String cardNumberLast4) {
    return bankCardService.getAll(pageable, cardNumberLast4);
  }

  @Operation(summary = "Редактирование карты")
  @PutMapping("/bank-cards/{cardId}")
  public BankCardDto updateCard(
      @PathVariable Long cardId, @Validated @RequestBody BankCardUpdateDto cardDto) {
    return bankCardService.update(cardId, cardDto);
  }
}
