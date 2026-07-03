package com.example.demo.Product;

import com.example.demo.Brand.Brand;
import com.example.demo.Category.Category;
import com.example.demo.Brand.BrandRepository;
import com.example.demo.Category.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    public ProductResponseDto createProduct(ProductCreateDto dto) {

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Kategori bulunamadı."));

        Brand brand = brandRepository.findById(dto.getBrandId())
                .orElseThrow(() -> new RuntimeException("Marka bulunamadı."));

        Product product = new Product();

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPurchasePrice(dto.getPurchasePrice());
        product.setSellPrice(dto.getSellPrice());
        product.setStock(dto.getStock());
        product.setCategory(category);
        product.setBrand(brand);
        product.setActive(true);

        Product saved = productRepository.save(product);

        return mapToResponse(saved);
    }

    public List<ProductResponseDto> getAllProducts() {

        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ProductResponseDto getProduct(Long id){

            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Ürün Bulunamadı! \nID: " + id));

            return mapToResponse(product);
    }

    private ProductResponseDto mapToResponse(Product product){

        ProductResponseDto dto = new ProductResponseDto();

        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setSellPrice(product.getSellPrice());
        dto.setStock(product.getStock());
        dto.setCategoryName(product.getCategory().getName());
        dto.setBrandName(product.getBrand().getName());

        return dto;
    }

}
