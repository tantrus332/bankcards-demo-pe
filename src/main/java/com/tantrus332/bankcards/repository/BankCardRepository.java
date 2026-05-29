package com.tantrus332.bankcards.repository;

import com.tantrus332.bankcards.entity.BankCard;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankCardRepository extends JpaRepository<BankCard, Long> {
  boolean existsByCardNumber(String cardNumber);

  Optional<BankCard> findByUserUsernameAndId(String username, Long id);

  Optional<BankCard> findByUserUsernameAndCardNumber(String username, String cardNumber);

  Page<BankCard> findAllByUserUsername(String username, Pageable pageable);

  Page<BankCard> findAllByCardNumberLast4Containing(Pageable pageable, String cardNumberLast4);

  Page<BankCard> findAllByUserUsernameAndCardNumberLast4Containing(
      Pageable pageable, String username, String cardNumberLast4);

  Page<BankCard> findAllByCardNumberLast4ContainingAndActivatedAtIsNotNull(
      Pageable pageable, String cardNumberLast4);
}
