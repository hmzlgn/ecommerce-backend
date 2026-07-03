package com.example.demo.Repository;

import com.example.demo.Entity.Brand;
import jakarta.persistence.Column;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BrandRepository extends JpaRepository<Brand,Long> {
    boolean existByName(String name);
}
