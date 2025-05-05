//package com.Roman21780.card_management.service;
//
//import com.Roman21780.card_management.dto.request.TransferRequest;
//import com.Roman21780.card_management.dto.response.CardResponse;
//import com.Roman21780.card_management.dto.response.TransferResponse;
//import com.Roman21780.card_management.exception.CardOperationException;
//import com.Roman21780.card_management.exception.InsufficientFundsException;
//import com.Roman21780.card_management.model.Card;
//import com.Roman21780.card_management.model.Card.CardStatus;
//import com.Roman21780.card_management.model.User;
//import com.Roman21780.card_management.repository.CardRepository;
//import com.Roman21780.card_management.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class CardServiceTest {
//
//    @Mock
//    private CardRepository cardRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private EncryptionService encryptionService;
//
//    @InjectMocks
//    private CardService cardService;
//
//    private User testUser;
//    private Card testCard1;
//    private Card testCard2;
//
//    @BeforeEach
//    void setUp() {
//        testUser = User.builder()
//                .id(1L)
//                .email("user@example.com")
//                .password("password")
//                .role(User.Role.USER)
//                .build();
//
//        testCard1 = Card.builder()
//                .id(1L)
//                .encryptedNumber("encrypted1")
//                .cardHolderName("User One")
//                .expirationDate(LocalDate.now().plusYears(2))
//                .status(CardStatus.ACTIVE) // Используем CardStatus
//                .balance(new BigDecimal("1000.00"))
//                .user(testUser)
//                .build();
//
//        testCard2 = Card.builder()
//                .id(2L)
//                .encryptedNumber("encrypted2")
//                .cardHolderName("User One")
//                .expirationDate(LocalDate.now().plusYears(1))
//                .status(CardStatus.ACTIVE) // Используем CardStatus
//                .balance(new BigDecimal("500.00"))
//                .user(testUser)
//                .build();
//
//        when(encryptionService.decrypt(anyString())).thenReturn("1234567890123456");
//        when(encryptionService.maskCardNumber(anyString())).thenReturn("**** **** **** 3456");
//    }
//
//    @Test
//    void transferBetweenCards_shouldCompleteSuccessfully() {
//        // Arrange
//        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("200.00"));
//
//        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
//        when(cardRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testCard1));
//        when(cardRepository.findByIdAndUser(2L, testUser)).thenReturn(Optional.of(testCard2));
//
//        // Act
//        TransferResponse response = cardService.transferBetweenUserCards(request, "user@example.com");
//
//        // Assert
//        assertEquals(new BigDecimal("800.00"), testCard1.getBalance());
//        assertEquals(new BigDecimal("700.00"), testCard2.getBalance());
//        assertEquals("Transfer completed successfully", response.getMessage()); // Используем getMessage()
//        verify(cardRepository, times(1)).saveAll(List.of(testCard1, testCard2));
//    }
//
//    @Test
//    void transferBetweenCards_shouldThrowWhenInsufficientFunds() {
//        // Arrange
//        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("2000.00"));
//
//        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
//        when(cardRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testCard1));
//        when(cardRepository.findByIdAndUser(2L, testUser)).thenReturn(Optional.of(testCard2));
//
//        // Act & Assert
//        assertThrows(InsufficientFundsException.class, () -> {
//            cardService.transferBetweenUserCards(request, "user@example.com");
//        });
//
//        verify(cardRepository, never()).saveAll(anyList());
//    }
//
//    @Test
//    void transferBetweenCards_shouldThrowWhenCardNotActive() {
//        // Arrange
//        testCard1.setStatus(CardStatus.BLOCKED);
//        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("200.00"));
//
//        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
//        when(cardRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testCard1));
//        when(cardRepository.findByIdAndUser(2L, testUser)).thenReturn(Optional.of(testCard2));
//
//        // Act & Assert
//        assertThrows(CardOperationException.class, () -> {
//            cardService.transferBetweenUserCards(request, "user@example.com");
//        });
//    }
//
//    @Test
//    void blockCard_shouldChangeStatusToBlocked() {
//        // Arrange
//        String userEmail = "user@example.com";
//        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(testUser));
//        when(cardRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testCard1));
//
//        // Act
//        CardResponse response = cardService.blockCard(1L, userEmail);
//
//        // Assert
//        assertEquals(CardStatus.BLOCKED, testCard1.getStatus());
//        assertNotNull(response);
//        verify(cardRepository, times(1)).save(testCard1);
//    }
//
//    @Test
//    void activateCard_shouldChangeStatusToActive() {
//        // Arrange
//        testCard1.setStatus(CardStatus.BLOCKED);
//        when(cardRepository.findById(1L)).thenReturn(Optional.of(testCard1));
//
//        // Act
//        CardResponse response = cardService.activateCard(1L);
//
//        // Assert
//        assertEquals(CardStatus.ACTIVE, testCard1.getStatus());
//        assertNotNull(response);
//        verify(cardRepository, times(1)).save(testCard1);
//    }
//}