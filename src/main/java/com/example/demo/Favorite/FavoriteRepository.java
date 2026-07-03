package com.example.demo.Favorite;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByUserIdAndProductId(Long userId, Long ProductId);
    void deleteByUserIdAndProductId(Long userId, Long ProductId);
}
