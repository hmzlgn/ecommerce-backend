package com.example.demo.DTO;

import lombok.Data;

@Data
public class ReviewCreateDto {
    private Long userId;
    private Long productId;
    private String comment;
    private Integer rating;//1-5 arası puanlama.
}
