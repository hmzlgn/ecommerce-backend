package com.example.demo.DTO;

import com.example.demo.enums.PaymentStatus;
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
