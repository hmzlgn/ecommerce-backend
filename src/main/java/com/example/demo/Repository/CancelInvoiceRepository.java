package com.example.demo.Repository;

import com.example.demo.Entity.CancelInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CancelInvoiceRepository extends JpaRepository<CancelInvoice,Long> {
}
