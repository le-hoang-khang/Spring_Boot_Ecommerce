package com.example.Midterm.Services;

import com.example.Midterm.DTOs.Request.ProductRequestDTO;
import com.example.Midterm.DTOs.Response.ProductResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Page<ProductResponseDTO> getAll(Pageable pageable);
    ProductResponseDTO getById(Long id);
    ProductResponseDTO save(ProductRequestDTO productRequest);
    ProductResponseDTO update(Long id, ProductRequestDTO productRequest);
    void updateStock(Long id, Integer amount);
    void delete(Long id);

    Page<ProductResponseDTO> findByCategoryName(String categoryName, Pageable pageable);
    Page<ProductResponseDTO> findByName(String name, Pageable pageable);
    Page<ProductResponseDTO> findByBrandName(String brandName, Pageable pageable);
    Page<ProductResponseDTO> findByPriceRange(Double minPrice, Double maxPrice, Pageable pageable);
    Page<ProductResponseDTO> findByColor(String color, Pageable pageable);
    Page<ProductResponseDTO> searchProducts(String name, Long brandId, Long categoryId,
                                            Double minPrice, Double maxPrice,
                                            String color, Pageable pageable);
}