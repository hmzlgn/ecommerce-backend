package com.example.demo.Order;

import com.example.demo.OrderItem.OrderItemRequestDto;
import com.example.demo.enums.PaymentMethod;
import lombok.Data;

import java.util.List;

@Data
public class OrderCreateDto {
    private List<OrderItemRequestDto> items;
    private String shippingAddress;
    private PaymentMethod paymentMethod;
    private String orderNote;
}
