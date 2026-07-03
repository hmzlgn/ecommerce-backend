package com.example.demo.DTO;

import lombok.Data;

@Data
public class ProductImageCreateDto {
    private Long productId;
    private String imageUrl;

}
