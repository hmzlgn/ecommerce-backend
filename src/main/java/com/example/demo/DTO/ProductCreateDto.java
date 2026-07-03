package com.example.demo.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductCreateDto {

    private String name;

    private BigDecimal purchasePrice;

    private BigDecimal sellPrice;

    private Integer stock;

    private Long categoryId;

    private Long brandId;   // ✔ EKLENDİ

    private String description; // ✔ EKLENDİ
}
