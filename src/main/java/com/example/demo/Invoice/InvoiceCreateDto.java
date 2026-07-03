package com.example.demo.Invoice;

import lombok.Data;

@Data
public class InvoiceCreateDto {
    private Long orderId;
    private String billingAddress;
}
