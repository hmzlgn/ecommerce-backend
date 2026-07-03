package com.example.demo.enums;

public enum OrderStatus {
    PENDING,        // Bekliyor (Ödeme henüz alınmadı)
    PROCESSING,     // İşleniyor (Ödeme alındı, paketleniyor)
    SHIPPED,        // Kargoya Verildi
    DELIVERED,      // Teslim Edildi
    CANCELLED,      // İptal Edildi
    REFUNDED,        // İade Edildi
    PAID
}
