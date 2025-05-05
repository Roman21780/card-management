package com.Roman21780.card_management.service;

import com.Roman21780.card_management.dto.request.CardRequest;
import com.Roman21780.card_management.dto.request.TransferRequest;
import com.Roman21780.card_management.dto.response.CardResponse;
import com.Roman21780.card_management.dto.response.TransferResponse;
import com.Roman21780.card_management.exception.*;
import com.Roman21780.card_management.model.Card;
import com.Roman21780.card_management.model.Card.CardStatus; // Правильный импорт
import com.Roman21780.card_management.model.User;
import com.Roman21780.card_management.repository.CardRepository;
import com.Roman21780.card_management.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final EncryptionService encryptionService;

    public CardResponse createCard(CardRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (request.getExpirationDate().isBefore(LocalDate.now())) {
            throw new InvalidCardDataException("Expiration date must be in the future");
        }

        Card card = Card.builder()
                .encryptedNumber(encryptionService.encrypt(request.getCardNumber()))
                .cardHolderName(request.getCardHolderName())
                .expirationDate(request.getExpirationDate())
                .status(CardStatus.ACTIVE) // Используем CardStatus
                .balance(request.getBalance())
                .user(user)
                .build();

        card = cardRepository.save(card);
        return mapToCardResponse(card);
    }

    public Page<CardResponse> getAllCards(CardStatus status, String cardHolderName, Pageable pageable) {
        Page<Card> cards = cardRepository.findAllWithFilters(status, cardHolderName, pageable);
        return cards.map(this::mapToCardResponse);
    }

    public List<CardResponse> getUserCards(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return cardRepository.findByUser(user).stream()
                .map(this::mapToCardResponse)
                .collect(Collectors.toList());
    }

    public CardResponse blockCard(Long cardId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Card card = cardRepository.findByIdAndUser(cardId, user)
                .orElseThrow(() -> new CardNotFoundException("Card not found or not owned by user"));

        if (card.getStatus() == CardStatus.BLOCKED) {
            throw new CardOperationException("Card is already blocked");
        }

        card.setStatus(CardStatus.BLOCKED);
        card = cardRepository.save(card);
        return mapToCardResponse(card);
    }

    public CardResponse activateCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found"));

        if (card.getStatus() == CardStatus.ACTIVE) {
            throw new CardOperationException("Card is already active");
        }

        if (card.getExpirationDate().isBefore(LocalDate.now())) {
            throw new CardOperationException("Cannot activate expired card");
        }

        card.setStatus(CardStatus.ACTIVE);
        card = cardRepository.save(card);
        return mapToCardResponse(card);
    }

    public TransferResponse transferBetweenUserCards(TransferRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Card fromCard = cardRepository.findByIdAndUser(request.getFromCardId(), user)
                .orElseThrow(() -> new CardNotFoundException("Source card not found or not owned by user"));

        Card toCard = cardRepository.findByIdAndUser(request.getToCardId(), user)
                .orElseThrow(() -> new CardNotFoundException("Destination card not found or not owned by user"));

        validateTransfer(fromCard, toCard, request.getAmount());

        fromCard.setBalance(fromCard.getBalance().subtract(request.getAmount()));
        toCard.setBalance(toCard.getBalance().add(request.getAmount()));

        cardRepository.saveAll(List.of(fromCard, toCard));

        return new TransferResponse(
                fromCard.getId(),
                toCard.getId(),
                request.getAmount(),
                "Transfer completed successfully"
        );
    }

    private void validateTransfer(Card fromCard, Card toCard, BigDecimal amount) {
        if (fromCard.getStatus() != CardStatus.ACTIVE || toCard.getStatus() != CardStatus.ACTIVE) {
            throw new CardOperationException("Both cards must be active for transfer");
        }

        if (fromCard.getExpirationDate().isBefore(LocalDate.now()) ||
                toCard.getExpirationDate().isBefore(LocalDate.now())) {
            throw new CardOperationException("Cannot transfer with expired card");
        }

        if (fromCard.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Not enough funds on source card");
        }
    }

    private CardResponse mapToCardResponse(Card card) {
        return CardResponse.builder()
                .id(card.getId())
                .maskedNumber(encryptionService.maskCardNumber(encryptionService.decrypt(card.getEncryptedNumber())))
                .cardHolderName(card.getCardHolderName())
                .expirationDate(card.getExpirationDate())
                .status(card.getStatus())
                .balance(card.getBalance())
                .build();
    }
}