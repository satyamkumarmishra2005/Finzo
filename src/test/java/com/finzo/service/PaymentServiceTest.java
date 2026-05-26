package com.finzo.service;

import com.finzo.dto.PaymentRequest;
import com.finzo.model.Payment;
import com.finzo.model.Payment.PaymentStatus;
import com.finzo.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock private PaymentRepository paymentRepository;
    @InjectMocks private PaymentService paymentService;

    private PaymentRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = PaymentRequest.builder()
                .senderId(\"user-001\")
                .receiverId(\"user-002\")
                .amount(new BigDecimal(\"150.00\"))
                .currency(\"USD\")
                .description(\"Test payment\")
                .build();
    }

    @Test
    void initiatePayment_success() {
        when(paymentRepository.sumOutgoingByUserSince(anyString(), any())).thenReturn(BigDecimal.ZERO);
        when(paymentRepository.countFailedBySender(anyString())).thenReturn(0L);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> {
            Payment p = inv.getArgument(0);
            p.setId(1L);
            return p;
        });

        Payment result = paymentService.initiatePayment(validRequest);

        assertNotNull(result);
        assertEquals(\"user-001\", result.getSenderId());
        assertEquals(new BigDecimal(\"150.00\"), result.getAmount());
        verify(paymentRepository, atLeastOnce()).save(any());
    }

    @Test
    void initiatePayment_selfTransferBlocked() {
        validRequest.setReceiverId(\"user-001\");
        assertThrows(IllegalArgumentException.class,
                () -> paymentService.initiatePayment(validRequest));
    }

    @Test
    void initiatePayment_dailyLimitExceeded() {
        when(paymentRepository.sumOutgoingByUserSince(anyString(), any()))
                .thenReturn(new BigDecimal(\"49900.00\"));
        when(paymentRepository.countFailedBySender(anyString())).thenReturn(0L);

        assertThrows(PaymentService.PaymentLimitExceededException.class,
                () -> paymentService.initiatePayment(validRequest));
    }

    @Test
    void initiatePayment_accountBlocked() {
        when(paymentRepository.sumOutgoingByUserSince(anyString(), any())).thenReturn(BigDecimal.ZERO);
        when(paymentRepository.countFailedBySender(anyString())).thenReturn(5L);

        assertThrows(PaymentService.AccountBlockedException.class,
                () -> paymentService.initiatePayment(validRequest));
    }

    @Test
    void refundPayment_success() {
        Payment payment = Payment.builder()
                .id(1L).transactionRef(\"TXN-123\")
                .status(PaymentStatus.COMPLETED)
                .build();
        when(paymentRepository.findByTransactionRef(\"TXN-123\")).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Payment refunded = paymentService.refundPayment(\"TXN-123\");
        assertEquals(PaymentStatus.REFUNDED, refunded.getStatus());
    }

    @Test
    void refundPayment_notCompleted_throws() {
        Payment payment = Payment.builder()
                .id(1L).transactionRef(\"TXN-456\")
                .status(PaymentStatus.PENDING)
                .build();
        when(paymentRepository.findByTransactionRef(\"TXN-456\")).thenReturn(Optional.of(payment));

        assertThrows(IllegalStateException.class,
                () -> paymentService.refundPayment(\"TXN-456\"));
    }
}