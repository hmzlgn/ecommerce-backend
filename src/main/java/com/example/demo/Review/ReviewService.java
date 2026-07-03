package com.example.demo.Review;

import com.example.demo.Product.Product;
import com.example.demo.User.User;
import com.example.demo.Product.ProductRepository;
import com.example.demo.User.UserRepository;
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

        review=reviewRepository.save(review);

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
