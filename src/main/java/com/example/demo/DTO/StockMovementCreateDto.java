package com.example.demo.DTO;

import com.example.demo.enums.StockMovementType;
import lombok.Data;

@Data
public class StockMovementCreateDto {
    private Long productId;
    private Integer quantity;

    private StockMovementType movementType;
}
