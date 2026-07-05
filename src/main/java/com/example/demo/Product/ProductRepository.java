package com.example.demo.Product;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Long id);

    Page<Product> findByStockGreaterThan(int quantity, Pageable pageable);

    // --- ADMIN TARAFI: aktif/pasif fark etmeksizin tüm ürünleri görebilmeli ---
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    Page<Product> findByNameContainingIgnoreCase(String productName, Pageable pageable);
    Page<Product> findByBrandId(Long brandId, Pageable pageable);

    // --- MÜŞTERİ TARAFI (STOREFRONT): sadece aktif ürünler listelenmeli ---
    Page<Product> findByActiveTrue(Pageable pageable);
    Page<Product> findByCategoryIdAndActiveTrue(Long categoryId, Pageable pageable);
    Page<Product> findByBrandIdAndActiveTrue(Long brandId, Pageable pageable);
    Page<Product> findByNameContainingIgnoreCaseAndActiveTrue(String productName, Pageable pageable);
}