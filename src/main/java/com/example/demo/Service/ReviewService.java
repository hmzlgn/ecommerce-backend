package com.example.demo.Service;

import com.example.demo.DTO.ReviewCreateDto;
import com.example.demo.DTO.ReviewResponseDto;
import com.example.demo.Entity.Product;
import com.example.demo.Entity.Review;
import com.example.demo.Entity.User;
import com.example.demo.Repository.ProductRepository;
import com.example.demo.Repository.ReviewRepository;
import com.example.demo.Repository.UserRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    public ReviewResponseDto createReview(ReviewCreateDto request){
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Yorum yapılmak istenen ürün bulunamadı!Ürün ID:" + request.getProductId()));
        User user =userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Yorum yapmak isteyen kullanıcı bulunamadı! Kullanıcı ID:" + request.getUserId()));

        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new RuntimeException("Puanlama sadece 1 ile 5 arasında olabilir! Sizin puanınız: " + request.getRating());
        }
        Review review = new Review();

        review.setProduct(product);
        review.setUser(user);
        review.setComment(request.getComment());
        review.setRating(request.getRating());

        reviewRepository.save(review);

        ReviewResponseDto response = new ReviewResponseDto();
        response.setId(review.getId());
        response.setProductName(product.getName());
        response.setUserFullName(user.getFirstName()+" "+user.getLastName());
        response.setRating(review.getRating());
        response.setComment(request.getComment());
        response.setCreatedAt(review.getCreatedAt());

        return response;

    }
}
