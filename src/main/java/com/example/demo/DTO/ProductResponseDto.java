package com.example.demo.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductResponseDto {

    private Long id;

    private String name;

    private BigDecimal sellPrice;

    private Integer stock;

    private String description;   // ✔

    private String categoryName;

    private String brandName;     // ✔
}
