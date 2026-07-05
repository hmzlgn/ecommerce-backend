package com.example.demo.CancelInvoice;

import com.example.demo.Invoice.Invoice;
import com.example.demo.Invoice.InvoiceRepository;
import com.example.demo.enums.InvoiceType;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class CancelInvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final CancelInvoiceRepository cancelInvoiceRepository;

    public CancelInvoiceResponseDTO createCancelInvoice(CancelInvoiceCreateDto request) {

        // 1. Faturayı Bul ve Durumunu Kontrol Et
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new RuntimeException("Fatura Bulunamadı! Fatura ID:" + request.getInvoiceId()));

        if (invoice.getInvoiceType() == InvoiceType.REFUND) {
            throw new RuntimeException("Fatura zaten iptal edilmiş durumda!");
        }

        // 2. İptal Faturasını (Entity) Oluştur ve Kaydet
        CancelInvoice cancelInvoice = new CancelInvoice();
        cancelInvoice.setCancelledBy(invoice.getUser());
        cancelInvoice.setCancelDate(LocalDateTime.now());
        cancelInvoice.setInvoice(invoice);
        cancelInvoice.setReason(request.getReason());
        cancelInvoice = cancelInvoiceRepository.save(cancelInvoice);

        // 3. Orijinal Faturanın Durumunu Güncelle ve Kaydet
        invoice.setInvoiceType(InvoiceType.REFUND);
        invoiceRepository.save(invoice);

        // 4. Paketleme Görevlisine Devret
        return mapToResponse(cancelInvoice, invoice);
    }

    // --- YARDIMCI PAKETLEME METODU (DRY Prensibi) ---
    private CancelInvoiceResponseDTO mapToResponse(CancelInvoice cancelInvoice, Invoice invoice) {
        CancelInvoiceResponseDTO response = new CancelInvoiceResponseDTO();
        response.setOriginalInvoiceId(invoice.getId());
        response.setCancelReason(cancelInvoice.getReason());
        response.setInvoiceType(InvoiceType.REFUND);
        return response;
    }
}

