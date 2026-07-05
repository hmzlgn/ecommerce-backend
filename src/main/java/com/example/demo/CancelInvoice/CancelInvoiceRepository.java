package com.example.demo.CancelInvoice;

import com.example.demo.enums.CancelInvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CancelInvoiceRepository extends JpaRepository<CancelInvoice,Long> {
    boolean existsByInvoiceIdAndStatus(Long invoiceId, CancelInvoiceStatus status);
}
