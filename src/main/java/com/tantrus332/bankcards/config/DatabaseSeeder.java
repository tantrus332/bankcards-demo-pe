package com.tantrus332.bankcards.config;

import com.tantrus332.bankcards.entity.User;
import com.tantrus332.bankcards.repository.BankCardRepository;
import com.tantrus332.bankcards.repository.UserRepository;
import com.tantrus332.bankcards.service.BankCardService;
import com.tantrus332.bankcards.util.UserRole;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DatabaseSeeder {
  @Bean
  public CommandLineRunner initData(
      UserRepository userRepository,
      BankCardService bankCardService,
      BankCardRepository bankCardRepository,
      PasswordEncoder passwordEncoder) {
    return args -> {
      if (userRepository.findByUsername("admin").isEmpty()) {
        User admin =
            User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .role(UserRole.ADMIN)
                .cardHolderName("FEDOR YAKUSHEV")
                .build();

        admin = userRepository.save(admin);
      }

      if (userRepository.findByUsername("user1").isEmpty()) {
        User user1 =
            User.builder()
                .username("user1")
                .password(passwordEncoder.encode("user1"))
                .role(UserRole.USER)
                .cardHolderName("OLGA FEDOROVA")
                .build();
        user1 = userRepository.save(user1);

        var card1Dto = bankCardService.create(user1.getId());
        var card1Id = card1Dto.getId();
        var card1 = bankCardRepository.findById(card1Id).orElseThrow();
        card1.setBalance(new BigDecimal("10000.00"));
        bankCardRepository.save(card1);

        var card2Dto = bankCardService.create(user1.getId());
        var card2Id = card2Dto.getId();
        var card2 = bankCardRepository.findById(card2Id).orElseThrow();
        card2.setBalance(new BigDecimal("5000.0"));
        bankCardRepository.save(card2);
      }

      if (userRepository.findByUsername("user2").isEmpty()) {
        User user2 =
            User.builder()
                .username("user2")
                .password(passwordEncoder.encode("user2"))
                .role(UserRole.USER)
                .cardHolderName("ALEXEY SOROKIN")
                .build();
        user2 = userRepository.save(user2);

        var card3Dto = bankCardService.create(user2.getId());
        var card3Id = card3Dto.getId();
        var card3 = bankCardRepository.findById(card3Id).orElseThrow();
        card3.setBalance(new BigDecimal("15000.00"));
        bankCardRepository.save(card3);

        var card4Dto = bankCardService.create(user2.getId());
        var card4Id = card4Dto.getId();
        var card4 = bankCardRepository.findById(card4Id).orElseThrow();
        card4.setBalance(new BigDecimal("12000.0"));
        bankCardRepository.save(card4);
      }
    };
  }
}
