package com.example.demo.DTO;

import com.example.demo.enums.PaymentMethod;
import lombok.Data;

import java.util.List;

@Data
public class OrderCreateDto {
    private Long userId;
    private String shippingAddress;
    private PaymentMethod paymentMethod;

    private List<OrderItemRequestDto> items;
}
