package com.example.demo.Repository;

import com.example.demo.Entity.Favorite;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByUserIdAndProductId(Long userId, Long ProductId);
    void deleteByUserIdAndProductId(Long userId, Long ProductId);
}
