package com.example.demo.Product;

import com.example.demo.Brand.Brand;
import com.example.demo.Category.Category;
import com.example.demo.Brand.BrandRepository;
import com.example.demo.Category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    // --- 1. ÜRÜN OLUŞTURMA VE GÜNCELLEME ---

    // Ürün ekleme
    public ProductResponseDto createProduct(ProductCreateDto request) {

        validateProductInput(request.getStock(), request.getSellPrice(), request.getPurchasePrice());

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Kategori bulunamadı."));

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new RuntimeException("Marka bulunamadı."));

        Product product = mapToEntity(request, category, brand);
        product = productRepository.save(product);

        return mapToResponse(product);
    }

    // Ürün Güncelleme
    public ProductResponseDto updateProduct(ProductUpdateDto request) {
        // ürün var mı kontrol et
        Product existingProduct = productRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Güncellemek istediğiniz ürün bulunamadı! Ürün ID: " + request.getId()));

        // marka kısmı boş değilse yeni markayı set et
        if (request.getBrandId() != null) {
            Brand brand = brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new RuntimeException("Eklemek istediğiniz marka bulunamadı! Marka ID:" + request.getBrandId()));
            existingProduct.setBrand(brand);
        }

        // kategori kısmı boş değilse yeni kategoriyi set et
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Eklemek istediğiniz kategori bulunamadı! Kategori ID:" + request.getCategoryId()));
            existingProduct.setCategory(category);
        }

        // fiyat/stok validasyonu (sadece gönderilen alanlar için)
        if (request.getStock() != null && request.getStock() < 0) {
            throw new IllegalArgumentException("Stok negatif olamaz!");
        }
        if (request.getSellPrice() != null && request.getSellPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Satış fiyatı sıfırdan büyük olmalıdır!");
        }
        if (request.getPurchasePrice() != null && request.getPurchasePrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Alış fiyatı sıfırdan büyük olmalıdır!");
        }

        // applyUpdatesToProduct metodunu çağırarak kalan alanları set et
        applyUpdatesToProduct(existingProduct, request);

        // Değişiklikleri kaydet ve Response dön
        existingProduct = productRepository.save(existingProduct);
        return mapToResponse(existingProduct);
    }

    // --- 2. AKTİF / PASİF YÖNETİMİ ---

    // Ürün pasife alma (soft delete)
    public void deactivateProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pasife alınmak istenen ürün bulunamadı! ID: " + id));
        product.setActive(false);
        productRepository.save(product);
    }

    // Ürünü tekrar aktif etme
    public void activateProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aktif edilmek istenen ürün bulunamadı! ID: " + id));
        product.setActive(true);
        productRepository.save(product);
    }

    // --- 3. LİSTELEME İŞLEMLERİ (ADMIN: tümü / STOREFRONT: sadece aktif) ---

    // [ADMIN] tüm ürünleri listeleme (aktif + pasif)
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getAllProductsForAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.map(this::mapToResponse);
    }

    // sadece aktif ürünleri listeleme
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getActiveProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Product> productPage = productRepository.findByActiveTrue(pageable);
        return productPage.map(this::mapToResponse);
    }

    // Kategoriye göre aktif ürün listeleme
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getProductsByCategory(Long categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Product> productPage = productRepository.findByCategoryIdAndActiveTrue(categoryId, pageable);
        return productPage.map(this::mapToResponse);
    }

    // Markaya göre aktif ürün listeleme
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> getProductsByBrandId(Long brandId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Product> productPage = productRepository.findByBrandIdAndActiveTrue(brandId, pageable);
        return productPage.map(this::mapToResponse);
    }

    // isme göre aktif ürün arama
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> findByNameContainingIgnoreCase(String productName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Product> productPage = productRepository.findByNameContainingIgnoreCaseAndActiveTrue(productName, pageable);
        return productPage.map(this::mapToResponse);
    }

    // id ile ürün bulma (hem admin hem kullanıcı detay sayfası için kullanılabilir)
    @Transactional(readOnly = true)
    public ProductResponseDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ürün Bulunamadı! ID: " + id));
        return mapToResponse(product);
    }

    // --- 4. YARDIMCI METOTLAR ---

    private void validateProductInput(Integer stock, BigDecimal sellPrice, BigDecimal purchasePrice) {
        if (stock == null) {
            throw new RuntimeException("Stok bilgisi zorunludur!");
        }
        if (stock < 0) {
            throw new RuntimeException("Stok negatif olamaz!");
        }
        if (sellPrice == null || sellPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Satış fiyatı sıfırdan büyük olmalıdır!");
        }
        if (purchasePrice == null || purchasePrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Alış fiyatı sıfırdan büyük olmalıdır!");
        }
    }

    // Ürün güncelleme için mapping
    private void applyUpdatesToProduct(Product existingProduct, ProductUpdateDto request) {
        if (request.getName() != null) existingProduct.setName(request.getName());
        if (request.getDescription() != null) existingProduct.setDescription(request.getDescription());
        if (request.getPurchasePrice() != null) existingProduct.setPurchasePrice(request.getPurchasePrice());
        if (request.getSellPrice() != null) existingProduct.setSellPrice(request.getSellPrice());
        if (request.getStock() != null) existingProduct.setStock(request.getStock());
        if (request.getActive() != null) existingProduct.setActive(request.getActive());
    }

    // Ürün döndürme için mapping
    private ProductResponseDto mapToResponse(Product product) {
        ProductResponseDto response = new ProductResponseDto();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setSellPrice(product.getSellPrice());
        response.setStock(product.getStock());
        response.setCategoryName(product.getCategory().getName());
        response.setBrandName(product.getBrand().getName());
        return response;
    }

    // Veriyi entity'e çevirme için mapping
    private Product mapToEntity(ProductCreateDto request, Category category, Brand brand) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPurchasePrice(request.getPurchasePrice());
        product.setSellPrice(request.getSellPrice());
        product.setStock(request.getStock());
        product.setCategory(category);
        product.setBrand(brand);
        product.setActive(true);
        return product;
    }
}