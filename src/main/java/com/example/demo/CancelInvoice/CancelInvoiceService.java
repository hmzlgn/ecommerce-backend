package com.example.demo.CancelInvoice;

import com.example.demo.Invoice.Invoice;
import com.example.demo.Invoice.InvoiceRepository;
import com.example.demo.enums.CancelInvoiceStatus;
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

    // --- 1. AŞAMA: Müşteri İptal Talebi Oluşturur (Sadece Talep) ---
    public CancelInvoiceResponseDTO createCancelRequest(CancelInvoiceCreateDto request) {

        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new RuntimeException("Fatura Bulunamadı! Fatura ID:" + request.getInvoiceId()));

        if (invoice.getInvoiceType() == InvoiceType.REFUND) {
            throw new RuntimeException("Bu fatura zaten iptal edilmiş!");
        }

        // Opsiyonel Güvenlik: Aynı faturaya ikinci bir beklemede talep açılmasın
        boolean hasPending = cancelInvoiceRepository.existsByInvoiceIdAndStatus(invoice.getId(), CancelInvoiceStatus.PENDING);
        if (hasPending) {
            throw new RuntimeException("Bu fatura için zaten onay bekleyen bir iptal talebiniz var!");
        }

        CancelInvoice cancelInvoice = new CancelInvoice();
        cancelInvoice.setCancelledBy(invoice.getUser());
        cancelInvoice.setCancelDate(LocalDateTime.now());
        cancelInvoice.setInvoice(invoice);
        cancelInvoice.setReason(request.getReason());
        cancelInvoice.setStatus(CancelInvoiceStatus.PENDING); // YENİ: Başlangıçta PENDING

        cancelInvoice = cancelInvoiceRepository.save(cancelInvoice);

        // DİKKAT: Ana faturaya (Invoice) HİÇ DOKUNMADIK! Sadece talep açtık.
        return mapToResponse(cancelInvoice, invoice);
    }

    // --- 2. AŞAMA: Admin/Satıcı İptali Onaylar ---
    public CancelInvoiceResponseDTO approveCancelRequest(Long cancelInvoiceId) {
        CancelInvoice cancelInvoice = cancelInvoiceRepository.findById(cancelInvoiceId)
                .orElseThrow(() -> new RuntimeException("İptal talebi bulunamadı!"));

        if (cancelInvoice.getStatus() != CancelInvoiceStatus.PENDING) {
            throw new RuntimeException("Sadece bekleyen talepler onaylanabilir!");
        }

        // 1. Talebin durumunu ONAYLANDI yap
        cancelInvoice.setStatus(CancelInvoiceStatus.APPROVED);
        cancelInvoice = cancelInvoiceRepository.save(cancelInvoice);

        // 2. ŞİMDİ asıl faturayı bul ve İPTAL EDİLDİ (REFUND) yap
        Invoice invoice = cancelInvoice.getInvoice();
        invoice.setInvoiceType(InvoiceType.REFUND);
        invoiceRepository.save(invoice);

        return mapToResponse(cancelInvoice, invoice);
    }

    // --- 3. AŞAMA: Admin/Satıcı İptali Reddeder ---
    public CancelInvoiceResponseDTO rejectCancelRequest(Long cancelInvoiceId) {
        CancelInvoice cancelInvoice = cancelInvoiceRepository.findById(cancelInvoiceId)
                .orElseThrow(() -> new RuntimeException("İptal talebi bulunamadı!"));

        if (cancelInvoice.getStatus() != CancelInvoiceStatus.PENDING) {
            throw new RuntimeException("Sadece bekleyen talepler reddedilebilir!");
        }

        // Talebin durumunu REDDEDİLDİ yap
        cancelInvoice.setStatus(CancelInvoiceStatus.REJECTED);
        cancelInvoice = cancelInvoiceRepository.save(cancelInvoice);

        // Asıl faturaya yine dokunmuyoruz, satış aynen devam ediyor.
        return mapToResponse(cancelInvoice, cancelInvoice.getInvoice());
    }

    // --- YARDIMCI PAKETLEME METODU ---
    private CancelInvoiceResponseDTO mapToResponse(CancelInvoice cancelInvoice, Invoice invoice) {
        CancelInvoiceResponseDTO response = new CancelInvoiceResponseDTO();
        response.setOriginalInvoiceId(invoice.getId());
        response.setCancelReason(cancelInvoice.getReason());
        // Frontend'e faturanın son halini dönüyoruz
        response.setInvoiceType(invoice.getInvoiceType());
        return response;
    }
}