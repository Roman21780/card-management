package com.Roman21780.card_management.dto.response;

import com.Roman21780.card_management.model.Card.CardStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class CardResponse {
    private Long id;
    private String maskedNumber;
    private String cardHolderName;
    private LocalDate expirationDate;
    private CardStatus status; // Соответствует CardStatus из модели
    private BigDecimal balance;
}