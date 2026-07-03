package com.example.demo.ProductImage;

import com.example.demo.Product.Product;
import com.example.demo.Product.ProductRepository;
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

        productImage=productImageRepository.save(productImage);

        ProductImageResponseDto response = new ProductImageResponseDto();
        response.setId(productImage.getId());
        response.setImageUrl(productImage.getImageUrl());
        response.setProductId(product.getId());
        response.setProductName(product.getName());

        return response;
    }
}
