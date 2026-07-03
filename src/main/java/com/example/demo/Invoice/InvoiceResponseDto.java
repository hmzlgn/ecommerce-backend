package com.example.demo.Invoice;

import com.example.demo.enums.InvoiceType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InvoiceResponseDto {
    private Long id;
    private Long orderId;
    private String invoiceNumber;
    private BigDecimal totalAmountWithTax;
    private LocalDateTime invoiceCreatedAt;
    private BigDecimal totalTaxAmount;
    private InvoiceType invoiceType;
    private Long originalInvoiceId;
}
