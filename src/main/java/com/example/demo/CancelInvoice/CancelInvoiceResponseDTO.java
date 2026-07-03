package com.example.demo.CancelInvoice;

import com.example.demo.enums.InvoiceType;
import lombok.Data;

@Data
public class CancelInvoiceResponseDTO {
    private Long originalInvoiceId;
    private String cancelReason;
    private InvoiceType invoiceType;
}
