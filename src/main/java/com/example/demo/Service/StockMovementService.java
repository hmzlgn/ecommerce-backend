package com.example.demo.Service;

import com.example.demo.DTO.StockMovementCreateDto;
import com.example.demo.Entity.Product;
import com.example.demo.Entity.StockMovement;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Repository.StockMovementRepository;
import com.example.demo.enums.PaymentMethod;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StockMovementService {
    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void recordMovement(StockMovementCreateDto request){

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Ürün Bulunamadı! ID: " + request.getProductId()));

        StockMovement movement = new StockMovement();
        movement.setProduct(product);
        movement.setQuantity(request.getQuantity());
        movement.setMovementType(request.getMovementType());
        stockMovementRepository.save(movement);
    }

}
