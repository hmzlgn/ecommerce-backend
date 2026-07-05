package com.example.demo.Product;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductUpdateDto {

        private Long id;
        private String name;
        private BigDecimal purchasePrice;
        private BigDecimal sellPrice;
        private Integer stock;
        private Long categoryId;
        private Long brandId;
        private String description;
        private Boolean active; // opsiyonel
    }

