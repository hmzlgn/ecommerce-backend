package com.example.demo.ProductImage;

import lombok.Data;

@Data
public class ProductImageResponseDto {
    private Long id;
    private String imageUrl;
    private Long productId;
    private String productName;
}
