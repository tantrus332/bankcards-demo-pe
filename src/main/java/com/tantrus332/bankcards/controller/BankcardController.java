package com.tantrus332.bankcards.controller;

import org.springframework.web.bind.annotation.RestController;

import com.tantrus332.bankcards.dto.BankCardDetailsDto;
import com.tantrus332.bankcards.dto.BankCardDto;
import com.tantrus332.bankcards.dto.TransferRequestDto;
import com.tantrus332.bankcards.dto.TransferResponseDto;
import com.tantrus332.bankcards.service.BankCardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/bank-cards")
@Tag(name = "Пользовательские методы для карт")
public class BankcardController {
    private final BankCardService bankCardService;

    @GetMapping
    @Operation(summary = "Список моих карт")
    public Page<BankCardDto> getMyCards(Pageable pageable, @RequestParam(defaultValue = "") String cardNumberLast4) {
        return bankCardService.getMyCards(pageable, cardNumberLast4);
    }

    @Operation(summary = "Запрос на блокировку карты")
    @PutMapping("/{cardId}/request-block/")
    public BankCardDto requestBlockMyCard(@PathVariable Long cardId) {
        return bankCardService.requestBlock(cardId);
    }

    @PutMapping("/transfer")
    @Operation(summary = "Перевод с одной на другую карту")
    public TransferResponseDto transfer(@Validated @RequestBody TransferRequestDto requestDto) {
        return bankCardService.transfer(
            requestDto.getFromCardNumber(),
            requestDto.getToCardNumber(),
            requestDto.getAmount()
        );
    }

    @GetMapping("/{cardId}/details")
    @Operation(summary = "Получить скрытые данные моей карты")
    public BankCardDetailsDto getFullDetails(@PathVariable Long cardId) {
        return bankCardService.getFullDetails(cardId);
    }
}
