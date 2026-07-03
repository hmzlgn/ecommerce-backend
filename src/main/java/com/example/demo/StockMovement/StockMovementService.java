package com.example.demo.StockMovement;

import com.example.demo.Product.Product;
import com.example.demo.Product.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
        movement=stockMovementRepository.save(movement);
    }

}
