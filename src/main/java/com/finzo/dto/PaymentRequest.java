package com.finzo.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PaymentRequest {
    @NotBlank(message = "Sender ID is required")
    private String senderId;

    @NotBlank(message = "Receiver ID is required")
    private String receiverId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    @DecimalMax(value = "100000.00", message = "Amount cannot exceed 100,000")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    @Pattern(regexp = "^(USD|EUR|GBP|INR|JPY)$", message = "Unsupported currency")
    private String currency;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
}