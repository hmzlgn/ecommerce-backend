package com.example.demo.CancelInvoice;

import com.example.demo.Invoice.Invoice;
import com.example.demo.Invoice.InvoiceRepository;
import com.example.demo.enums.InvoiceType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CancelInvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final CancelInvoiceRepository cancelInvoiceRepository;

    public CancelInvoiceResponseDTO createCancelInvoice(CancelInvoiceCreateDto request) {
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new RuntimeException("Fatura Bulunamadı! Fatura ID:" + request.getInvoiceId()));
        if (invoice.getInvoiceType()== InvoiceType.REFUND){
            throw new RuntimeException("Fatura zaten iptal edilmiş durumda!");
        }
        CancelInvoice cancelInvoice = new CancelInvoice();
        cancelInvoice.setCancelledBy(invoice.getUser());
        cancelInvoice.setCancelDate(LocalDateTime.now());
        cancelInvoice.setInvoice(invoice);
        cancelInvoice.setReason(request.getReason());
        cancelInvoice = cancelInvoiceRepository.save(cancelInvoice);

        invoice.setInvoiceType(InvoiceType.REFUND);
        invoiceRepository.save(invoice);

        CancelInvoiceResponseDTO response = new CancelInvoiceResponseDTO();
        response.setInvoiceType(cancelInvoice.getInvoice().getInvoiceType());
        response.setOriginalInvoiceId(invoice.getId());
        response.setCancelReason(cancelInvoice.getReason());
        response.setInvoiceType(InvoiceType.REFUND);

        return response;
    }
}
