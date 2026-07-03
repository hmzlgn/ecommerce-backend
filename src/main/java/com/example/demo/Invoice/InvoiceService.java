package com.example.demo.Invoice;

import com.example.demo.Order.Order;
import com.example.demo.Order.OrderRepository;
import com.example.demo.enums.InvoiceType;
import com.example.demo.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvoiceService {
    private final OrderRepository orderRepository;
    private final InvoiceRepository invoiceRepository;

    public InvoiceResponseDto createInvoice(InvoiceCreateDto request){
        // 1. Sipariş var mı kontrol et
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Sipariş Bulunamadı! ID: " + request.getOrderId()));

        // 2. Güvenlik Duvarları
        // DÜZELTME 1: Doğru depoya (InvoiceRepository) soruyoruz!
        if (invoiceRepository.existsByOrderId(request.getOrderId())){
            throw new RuntimeException("Bu siparişe daha önce fatura kesilmiş! Sipariş ID: " + order.getId());
        }
        if (order.getOrderStatus() != OrderStatus.PAID){
            throw new RuntimeException("Sipariş durumu fatura kesilmesine uygun değil! Sipariş Durumu: " + order.getOrderStatus());
        }

        // 3. Vergi (KDV) Matematiği (%20 KDV varsayımı)
        BigDecimal orderTotal = order.getTotalAmount();

        // KDV = Ana Para * 0.20
        BigDecimal totalTaxAmount = orderTotal.multiply(new BigDecimal("0.20"));

        // Toplam = Ana Para + KDV
        BigDecimal totalAmountWithTax = orderTotal.add(totalTaxAmount);

        // 4. Boş bir invoice oluştur ve içini doldur
        Invoice invoice = new Invoice();
        invoice.setInvoiceType(InvoiceType.SALES);
        invoice.setBillingAddress(request.getBillingAddress());
        invoice.setUser(order.getUser()); // Kullanıcıyı direkt siparişten çektik
        invoice.setOrder(order);
        invoice.setTotalTaxAmount(totalTaxAmount);
        invoice.setTotalAmountWithTax(totalAmountWithTax);
        invoice.setInvoiceNumber(UUID.randomUUID().toString()); //Şimdilik rastgele veriyoruz ileride maliye veya bankadadan alınacak

        invoice = invoiceRepository.save(invoice);

        // 5. Response tepsisi oluştur ve değişkenleri set et
        InvoiceResponseDto response = new InvoiceResponseDto();
        response.setId(invoice.getId());
        response.setInvoiceCreatedAt(invoice.getInvoiceCreatedAt()); // Artık veritabanından dolu gelecek
        response.setInvoiceNumber(invoice.getInvoiceNumber());
        response.setTotalTaxAmount(totalTaxAmount);
        response.setTotalAmountWithTax(totalAmountWithTax);
        response.setInvoiceType(invoice.getInvoiceType());
        response.setOrderId(order.getId());

        return response;
    }
}