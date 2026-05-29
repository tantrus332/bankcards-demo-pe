package com.tantrus332.bankcards.entity;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneOffset;

import com.tantrus332.bankcards.util.AttributeEncryptor;
import com.tantrus332.bankcards.util.BankCardStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bank_cards")
public class BankCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = AttributeEncryptor.class)
    @Column(nullable = false, unique = true, name = "card_number")
    private String cardNumber;

    @Column(nullable = false, name = "card_number_last4")
    private String cardNumberLast4;

    @ManyToOne
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Column(nullable = false, name = "expiration_year")
    private Integer expirationYear;

    @Column(nullable = false, name = "expiration_month")
    private Integer expirationMonth;

    @Column(scale = 2, nullable = false)
    private BigDecimal balance;

    @Column(name = "block_requested_at")
    private OffsetDateTime blockRequestedAt;

    @Column(name = "activated_at")
    private OffsetDateTime activatedAt;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }

    public void setFullCardNumber(String newCardNumber) {
        setCardNumberLast4(newCardNumber.substring(12, 16));
        setCardNumber(newCardNumber);
    }

    public YearMonth getExpirationYearMonth() {
        return YearMonth.of(getExpirationYear(), getExpirationMonth());
    }

    public OffsetDateTime getExpirationDt() {
        YearMonth ym = getExpirationYearMonth();

        return ym.atEndOfMonth()
            .atTime(LocalTime.MAX)
            .atOffset(ZoneOffset.UTC);
    }

    public BankCardStatus getStatus() {
        if(OffsetDateTime.now().isAfter(getExpirationDt())) {
            return BankCardStatus.EXPIRED;
        } else if(getActivatedAt() == null) {
            return BankCardStatus.BLOCKED;
        } else {
            return BankCardStatus.ACTIVE;
        }
    }
}
