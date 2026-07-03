package com.example.demo.Category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Category createCategory(CategoryCreateDto request) {
        Category category = new Category();
        category.setName(request.getName());
        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Belirtilen üst kategori bulunamadı!"));
            category.setParentCategory(parent);
        }
        return category=categoryRepository.save(category);
    }

    // TÜM KATEGORİLERİ LİSTELEME
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}
