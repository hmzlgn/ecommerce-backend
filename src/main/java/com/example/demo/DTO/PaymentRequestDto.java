package com.example.demo.DTO;

import com.example.demo.enums.PaymentMethod;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequestDto {
    private Long orderId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
}
