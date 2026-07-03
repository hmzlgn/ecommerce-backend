package com.example.demo.Service;

import com.example.demo.DTO.ProductCreateDto;
import com.example.demo.DTO.ProductImageCreateDto;
import com.example.demo.DTO.ProductImageResponseDto;
import com.example.demo.DTO.ProductResponseDto;
import com.example.demo.Entity.Product;
import com.example.demo.Entity.ProductImage;
import com.example.demo.Repository.ProductImageRepository;
import com.example.demo.Repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductImageService {
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

    public ProductImageResponseDto addProductImage(ProductImageCreateDto request){
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Fotoğrafı eklenmek istenen ürün bulunamadı! " +
                "\nÜrün ID:"+ request.getProductId()));

        ProductImage productImage = new ProductImage();
        productImage.setProduct(product);
        productImage.setImageUrl(request.getImageUrl());

        productImageRepository.save(productImage);

        ProductImageResponseDto response = new ProductImageResponseDto();
        response.setId(productImage.getId());
        response.setImageUrl(productImage.getImageUrl());
        response.setProductId(product.getId());
        response.setProductName(product.getName());

        return response;
    }
}
