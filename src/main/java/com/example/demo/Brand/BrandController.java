package com.example.demo.Brand;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {
    private final BrandService brandService;

    @PostMapping
    public BrandResponseDto createBrand(@RequestBody BrandCreateDto request){
        return brandService.createBrand(request);
    }

    @GetMapping
    public List<BrandResponseDto> getAllBrands(){
        return brandService.getAllBrands();
    }
}
