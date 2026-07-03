package com.example.demo.StockMovement;

import com.example.demo.enums.StockMovementType;
import lombok.Data;

@Data
public class StockMovementCreateDto {
    private Long productId;
    private Integer quantity;

    private StockMovementType movementType;
}
