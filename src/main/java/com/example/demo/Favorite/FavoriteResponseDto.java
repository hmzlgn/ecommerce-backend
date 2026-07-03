package com.example.demo.Favorite;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FavoriteResponseDto {
    private Long id;
    private Long productId;
    private String productName;
    private BigDecimal productPrice; // Vitrin bilgisi!
}
