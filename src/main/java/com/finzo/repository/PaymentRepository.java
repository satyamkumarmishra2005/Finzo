package com.finzo.repository;

import com.finzo.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findBySenderIdOrderByCreatedAtDesc(String senderId);
    List<Payment> findByReceiverIdOrderByCreatedAtDesc(String receiverId);
    Optional<Payment> findByTransactionRef(String transactionRef);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.senderId = :userId AND p.status = 'COMPLETED' AND p.createdAt >= :since")
    BigDecimal sumOutgoingByUserSince(String userId, LocalDateTime since);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.senderId = :userId AND p.status = 'FAILED'")
    long countFailedBySender(String userId);

    List<Payment> findByStatusOrderByCreatedAtAsc(Payment.PaymentStatus status);
}