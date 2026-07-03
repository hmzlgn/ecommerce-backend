package com.example.demo.Repository;

import com.example.demo.Entity.Invoice;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    boolean existsByOrderId(Long orderId);
    List<Invoice> findByUserId(Long userId);
}
