package com.example.demo.Payment;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentResponseDto {
    private Long id;
    private Long orderId;
    private BigDecimal amount;
    private String paymentStatus;
    private String transactionId;
    private LocalDateTime paymentDate;
}
