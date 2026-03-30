package com.example.Midterm.Repositories;

import com.example.Midterm.Entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Page<Product> findByBrandId(Long brandId, Pageable pageable);
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    Page<Product> findByStockQuantityGreaterThan(Integer quantity, Pageable pageable);

    Page<Product> findByCategoryNameContainingIgnoreCase(String categoryName, Pageable pageable);
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Product> findByBrandNameContainingIgnoreCase(String brandName, Pageable pageable);
    Page<Product> findByPriceBetween(Double minPrice, Double maxPrice, Pageable pageable);
    Page<Product> findByColorIgnoreCase(String color, Pageable pageable);
}