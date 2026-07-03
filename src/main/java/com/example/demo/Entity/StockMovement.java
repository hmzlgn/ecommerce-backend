package com.example.demo.Entity;

import com.example.demo.enums.StockMovementType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Product product;

    private Integer quantity;

    @Enumerated(EnumType.STRING)
    private StockMovementType movementType;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

}
