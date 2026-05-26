package com.finzo.service;

import com.finzo.dto.PaymentRequest;
import com.finzo.model.Payment;
import com.finzo.model.Payment.PaymentStatus;
import com.finzo.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    private static final BigDecimal DAILY_LIMIT = new BigDecimal(\"50000.00\");
    private static final int MAX_FAILED_ATTEMPTS = 5;

    @Transactional
    public Payment initiatePayment(PaymentRequest request) {
        log.info(\"Initiating payment: {} -> {} | amount={} {}\",
                request.getSenderId(), request.getReceiverId(),
                request.getAmount(), request.getCurrency());

        // Validate sender != receiver
        if (request.getSenderId().equals(request.getReceiverId())) {
            throw new IllegalArgumentException(\"Cannot send payment to yourself\");
        }

        // Check daily limit
        BigDecimal todayTotal = paymentRepository.sumOutgoingByUserSince(
                request.getSenderId(), LocalDateTime.now().toLocalDate().atStartOfDay());
        if (todayTotal != null && todayTotal.add(request.getAmount()).compareTo(DAILY_LIMIT) > 0) {
            throw new PaymentLimitExceededException(
                    \"Daily limit exceeded. Current: \" + todayTotal + \", Requested: \" + request.getAmount());
        }

        // Check for too many failed attempts (fraud prevention)
        long failedCount = paymentRepository.countFailedBySender(request.getSenderId());
        if (failedCount >= MAX_FAILED_ATTEMPTS) {
            log.warn(\"Account {} blocked due to {} failed payment attempts\",
                    request.getSenderId(), failedCount);
            throw new AccountBlockedException(\"Account temporarily blocked due to suspicious activity\");
        }

        // Create payment record
        Payment payment = Payment.builder()
                .senderId(request.getSenderId())
                .receiverId(request.getReceiverId())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .description(request.getDescription())
                .status(PaymentStatus.PENDING)
                .build();

        Payment saved = paymentRepository.save(payment);
        log.info(\"Payment created: txnRef={} | status=PENDING\", saved.getTransactionRef());

        // Process asynchronously (simulate gateway call)
        processPayment(saved);

        return saved;
    }

    @Transactional
    public void processPayment(Payment payment) {
        payment.setStatus(PaymentStatus.PROCESSING);
        paymentRepository.save(payment);

        try {
            // Simulate payment gateway processing
            Thread.sleep(100);

            // Mark as completed
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setCompletedAt(LocalDateTime.now());
            paymentRepository.save(payment);

            log.info(\"Payment completed: txnRef={}\", payment.getTransactionRef());
        } catch (Exception e) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason(e.getMessage());
            paymentRepository.save(payment);
            log.error(\"Payment failed: txnRef={} | reason={}\",
                    payment.getTransactionRef(), e.getMessage());
        }
    }

    @Transactional
    public Payment refundPayment(String transactionRef) {
        Payment payment = paymentRepository.findByTransactionRef(transactionRef)
                .orElseThrow(() -> new PaymentNotFoundException(\"Payment not found: \" + transactionRef));

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new IllegalStateException(\"Only completed payments can be refunded\");
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setCompletedAt(LocalDateTime.now());
        return paymentRepository.save(payment);
    }

    public List<Payment> getPaymentHistory(String userId) {
        return paymentRepository.findBySenderIdOrderByCreatedAtDesc(userId);
    }

    public Payment getPaymentByRef(String transactionRef) {
        return paymentRepository.findByTransactionRef(transactionRef)
                .orElseThrow(() -> new PaymentNotFoundException(\"Payment not found: \" + transactionRef));
    }

    // Custom exceptions
    public static class PaymentLimitExceededException extends RuntimeException {
        public PaymentLimitExceededException(String msg) { super(msg); }
    }
    public static class AccountBlockedException extends RuntimeException {
        public AccountBlockedException(String msg) { super(msg); }
    }
    public static class PaymentNotFoundException extends RuntimeException {
        public PaymentNotFoundException(String msg) { super(msg); }
    }
}