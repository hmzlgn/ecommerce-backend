package com.example.demo.Favorite;

import com.example.demo.Product.Product;
import com.example.demo.User.User;
import com.example.demo.Product.ProductRepository;
import com.example.demo.User.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public FavoriteResponseDto addFavorite(FavoriteDto request){
        //1.Kullanıcı ve ürün var mı kontrol et
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->new RuntimeException("Kullanıcı Bulunamadı! ID: " + request.getUserId()));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Ürün bulunamadı! Ürün ID: " + request.getProductId()));

        //2.İkisi de varsa ürün daha önce favoriye eklenmiş mi kontrol et
        if (favoriteRepository.existsByUserIdAndProductId(request.getUserId(), request.getProductId())){
            throw new RuntimeException("Ürün daha önce favorilere eklenmiş.");
        }
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProduct(product);
        favorite=favoriteRepository.save(favorite);

        FavoriteResponseDto response = new FavoriteResponseDto();
        response.setId(favorite.getId());
        response.setProductName(product.getName());
        response.setProductPrice(product.getSellPrice());
        response.setProductId(product.getId());


        return response;
    }

    @Transactional
    public void removeFavorite(FavoriteDto request){
        favoriteRepository.deleteByUserIdAndProductId(request.getUserId(), request.getProductId());
    }
}
