package com.Roman21780.card_management.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardRequest {
    @NotBlank
    @Size(min = 16, max = 19)
    private String cardNumber;

    @NotBlank
    private String cardHolderName;

    @Future
    private LocalDate expirationDate;

    private BigDecimal balance;
}