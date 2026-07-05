package com.example.demo.Order;

import com.example.demo.OrderItem.OrderItemResponseDto;
import com.example.demo.enums.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponseDto {
    private Long id;
    private Long userId;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private List<OrderItemResponseDto> items; // Siparişin içindeki ürünler
}
