package com.example.demo.Brand;



import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;

    @Transactional
    public BrandResponseDto createBrand(BrandCreateDto request){

        if (brandRepository.existByName(request.getName())){
            throw new RuntimeException("Bu marka zaten sistemde kayıtlı: " + request.getName());
        }
        Brand brand = new Brand();
        brand.setName(request.getName());


        brand = brandRepository.save(brand);

        BrandResponseDto response = new BrandResponseDto();
        response.setId(brand.getId());
        response.setName(brand.getName());
        return response;

    }
    public List<BrandResponseDto> getAllBrands(){
        List<Brand> brands = brandRepository.findAll();
        return brands.stream().map(brand -> {
            BrandResponseDto dto = new BrandResponseDto();
            dto.setId(brand.getId());
            dto.setName(brand.getName());
            return dto;
        }).collect(Collectors.toList());

    }
}
