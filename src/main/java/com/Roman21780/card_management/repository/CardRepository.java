package com.Roman21780.card_management.repository;

import com.Roman21780.card_management.model.Card;
import com.Roman21780.card_management.model.Card.CardStatus; // Используем CardStatus
import com.Roman21780.card_management.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByUser(User user);

    Optional<Card> findByIdAndUser(Long id, User user);

    @Query("SELECT c FROM Card c WHERE (:status IS NULL OR c.status = :status) " +
            "AND (:cardHolderName IS NULL OR c.cardHolderName LIKE %:cardHolderName%)")
    Page<Card> findAllWithFilters(
            @Param("status") CardStatus status, // Используем CardStatus
            @Param("cardHolderName") String cardHolderName,
            Pageable pageable);
}