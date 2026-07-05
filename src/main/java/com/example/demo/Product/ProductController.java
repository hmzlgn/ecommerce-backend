package com.example.demo.Product;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product", description = "Ürün işlemleri")
public class ProductController {
    private final ProductService productService;

    //ürün ekleme
    @PostMapping
    public ProductResponseDto createProduct(@RequestBody ProductCreateDto request) {
        return productService.createProduct(request);
    }

    //Kullanıcı ve admin için ürün listeleme
    @GetMapping("/active")
    public Page<ProductResponseDto> getActiveProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return productService.getActiveProducts(page, size);
    }

    //Admin için ürün listeleme
    @GetMapping("/allProducts")
    public Page<ProductResponseDto> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return productService.getAllProductsForAdmin(page, size);
    }

    @GetMapping("/{id}")
    public ProductResponseDto getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PutMapping("/{id}")
    public ProductResponseDto updateProduct(@PathVariable Long id, @RequestBody ProductUpdateDto request) {
        request.setId(id);
        return productService.updateProduct(request);
    }

    @DeleteMapping("/{id}")
    public void deleteProductById(@PathVariable Long id){
        productService.deactivateProduct(id);
    }
    @GetMapping("/category/{categoryId}")
    public Page<ProductResponseDto> getProductsByCategory(@PathVariable Long categoryId,
                                                    @RequestParam(defaultValue = "0")int pageNumber,
                                                    @RequestParam(defaultValue = "50") int pageSize)
    {
        return productService.getProductsByCategory(categoryId,pageNumber,pageSize);
    }
    @GetMapping("/brand/{brandId}")
    public Page<ProductResponseDto> getProductsByBrand(@PathVariable Long brandId,
                                                       @RequestParam(defaultValue = "0")int pageNumber,
                                                       @RequestParam(defaultValue = "50") int pageSize){
        return productService.getProductsByBrandId(brandId,pageNumber,pageSize);
    }
    @GetMapping("/search/{productName}")
    public Page<ProductResponseDto> findByNameContainingIgnoreCase(@RequestParam String productName,
                                                       @RequestParam(defaultValue = "0")int pageNumber,
                                                       @RequestParam(defaultValue = "50") int pageSize){
        return productService.findByNameContainingIgnoreCase(productName,pageNumber,pageSize);
    }

}
