package com.example.fastfoodshop.repository;

import com.example.fastfoodshop.entity.Cart;
import com.example.fastfoodshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser_IdAndProduct_Id(Long userId, Long productId);

    Cart findByUserAndProduct_Id(User user, Long productId);

    List<Cart> findByUser(User user);

    void deleteAllByUser(User user);
}
