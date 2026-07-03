package com.example.demo.DTO;

import lombok.Data;

@Data
public class InvoiceCreateDto {
    private Long orderId;
    private String billingAddress;
}
