package com.example.Midterm.Repositories;

import com.example.Midterm.Entities.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CartProductRepository extends JpaRepository<CartProduct, Long> {
    List<CartProduct> findByCartId(Long cartId);
    Optional<CartProduct> findByCartIdAndProductId(Long cartId, Long productId);

    @Modifying
    @Transactional
    void deleteByCartId(Long cartId);
}