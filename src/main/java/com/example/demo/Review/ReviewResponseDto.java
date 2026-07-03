package com.example.demo.Review;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewResponseDto {
    private Long id;
    private String comment;
    private Integer rating;
    private LocalDateTime createdAt;

    private String userFullName;
    private String productName;
}
