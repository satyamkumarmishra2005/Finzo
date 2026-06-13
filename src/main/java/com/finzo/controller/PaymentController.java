package com.finzo.controller;

import com.finzo.dto.PaymentRequest;
import com.finzo.model.Payment;
import com.finzo.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(\"/api/v1/payments\")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Payment> createPayment(@Valid @RequestBody PaymentRequest request) {
        Payment payment = paymentService.initiatePayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }

    @GetMapping(\"/history/{userId}\")
    public ResponseEntity<List<Payment>> getHistory(@PathVariable String userId) {
        return ResponseEntity.ok(paymentService.getPaymentHistory(userId));
    }

    @GetMapping(\"/{transactionRef}\")
    public ResponseEntity<Payment> getPayment(@PathVariable String transactionRef) {
        return ResponseEntity.ok(paymentService.getPaymentByRef(transactionRef));
    }

    @PostMapping(\"/{transactionRef}/refund\")
    public ResponseEntity<Payment> refundPayment(@PathVariable String transactionRef) {
        return ResponseEntity.ok(paymentService.refundPayment(transactionRef));
    }

    @ExceptionHandler(PaymentService.PaymentLimitExceededException.class)
    public ResponseEntity<Map<String, String>> handleLimitExceeded(PaymentService.PaymentLimitExceededException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(\"error\", \"DAILY_LIMIT_EXCEEDED\", \"message\", e.getMessage()));
    }

    @ExceptionHandler(PaymentService.AccountBlockedException.class)
    public ResponseEntity<Map<String, String>> handleBlocked(PaymentService.AccountBlockedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of(\"error\", \"ACCOUNT_BLOCKED\", \"message\", e.getMessage()));
    }

    @ExceptionHandler(PaymentService.PaymentNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(PaymentService.PaymentNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(\"error\", \"NOT_FOUND\", \"message\", e.getMessage()));
    }
}