package com.example.demo.Service;



import com.example.demo.DTO.BrandCreateDto;
import com.example.demo.DTO.BrandResponseDto;
import com.example.demo.Entity.Brand;
import com.example.demo.Repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;

    public BrandResponseDto createBrand(BrandCreateDto request){

        if (brandRepository.existByName(request.getName())){
            throw new RuntimeException("Bu marka zaten sistemde kayıtlı: " + request.getName());
        }
        Brand brand = new Brand();
        brand.setName(request.getName());

        // Veritabanına kaydet
        Brand savedBrand = brandRepository.save(brand);

        // Müşteriye dönecek tepsiyi (Response) hazırla
        BrandResponseDto response = new BrandResponseDto();
        response.setId(savedBrand.getId());
        response.setName(savedBrand.getName());

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
