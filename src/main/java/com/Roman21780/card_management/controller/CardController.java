package com.Roman21780.card_management.controller;

import com.Roman21780.card_management.dto.request.CardRequest;
import com.Roman21780.card_management.dto.request.TransferRequest;
import com.Roman21780.card_management.dto.response.CardResponse;
import com.Roman21780.card_management.dto.response.TransferResponse;
import com.Roman21780.card_management.model.Card.CardStatus; // Правильный импорт
import com.Roman21780.card_management.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<CardResponse>> getAllCards(
            @RequestParam(required = false) CardStatus status, // Используем CardStatus
            @RequestParam(required = false) String cardHolderName,
            @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(cardService.getAllCards(status, cardHolderName, pageable));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<CardResponse>> getUserCards() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(cardService.getUserCards(email));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardResponse> createCard(@Valid @RequestBody CardRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(cardService.createCard(request, email));
    }

    @PostMapping("/{cardId}/block")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CardResponse> blockCard(@PathVariable Long cardId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(cardService.blockCard(cardId, email));
    }

    @PostMapping("/{cardId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardResponse> activateCard(@PathVariable Long cardId) {
        return ResponseEntity.ok(cardService.activateCard(cardId));
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TransferResponse> transfer(
            @Valid @RequestBody TransferRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(cardService.transferBetweenUserCards(request, email));
    }
}