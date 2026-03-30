package com.example.Midterm.Services;

import com.example.Midterm.DTOs.Request.ProductRequestDTO;
import com.example.Midterm.DTOs.Response.ProductResponseDTO;
import com.example.Midterm.Entities.Brand;
import com.example.Midterm.Entities.Product;
import com.example.Midterm.Exceptions.ResourceNotFoundException;
import com.example.Midterm.Repositories.BrandRepository;
import com.example.Midterm.Repositories.CategoryRepository;
import com.example.Midterm.Repositories.ProductRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public Page<ProductResponseDTO> getAll(Pageable pageable) {
        return productRepository
                .findAll(pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    public ProductResponseDTO getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapToResponseDTO(product);
    }

    @Override
    @Transactional
    public ProductResponseDTO save(ProductRequestDTO productRequest) {
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setColor(productRequest.getColor());
        product.setStockQuantity(productRequest.getStockQuantity());
        if (productRequest.getBrandId() != null) {
            product.setBrand(brandRepository
                    .findById(productRequest.getBrandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Brand not found")));
        }
        if (productRequest.getCategoryId() != null) {
            product.setCategory(categoryRepository
                    .findById(productRequest.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found")));
        }

        return mapToResponseDTO(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductResponseDTO update(Long id, ProductRequestDTO productRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        if (productRequest.getName() != null && !productRequest.getName().isEmpty()) {
            product.setName(productRequest.getName());
        }
        if (productRequest.getDescription() != null && !productRequest.getDescription().isEmpty()) {
            product.setDescription(productRequest.getDescription());
        }
        if (productRequest.getPrice() != null) {
            product.setPrice(productRequest.getPrice());
        }
        if (productRequest.getColor() != null && !productRequest.getColor().isEmpty()) {
            product.setColor(productRequest.getColor());
        }
        if (productRequest.getStockQuantity() != null) {
            product.setStockQuantity(productRequest.getStockQuantity());
        }
        if (productRequest.getBrandId() != null) {
            product.setBrand(brandRepository
                    .findById(productRequest.getBrandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Brand not found")));
        }
        if (productRequest.getCategoryId() != null) {
            product.setCategory(categoryRepository
                    .findById(productRequest.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found")));
        }

        return mapToResponseDTO(productRepository.save(product));
    }

    @Override
    @Transactional
    public void updateStock(Long id, Integer amount) {
        Product product = productRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));

        if (amount == null) {
            throw new RuntimeException("Invalid amount");
        }

        // Check stock quantity after changed
        int newStockQuantity = product.getStockQuantity() + amount;
        if (newStockQuantity < 0) {
            throw new RuntimeException("Stock cannot be negative");
        }

        product.setStockQuantity(newStockQuantity);
        productRepository.save(product);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found to delete");
        }
        productRepository.deleteById(id);
    }

    @Override
    public Page<ProductResponseDTO> findByCategoryName(String categoryName, Pageable pageable) {
        return productRepository
                .findByCategoryNameContainingIgnoreCase(categoryName, pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    public Page<ProductResponseDTO> findByName(String name, Pageable pageable) {
        return productRepository
                .findByNameContainingIgnoreCase(name, pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    public Page<ProductResponseDTO> findByBrandName(String brandName, Pageable pageable) {
        return productRepository
                .findByBrandNameContainingIgnoreCase(brandName, pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    public Page<ProductResponseDTO> findByPriceRange(Double minPrice, Double maxPrice, Pageable pageable) {
        return productRepository
                .findByPriceBetween(minPrice, maxPrice, pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    public Page<ProductResponseDTO> findByColor(String color, Pageable pageable) {
        return productRepository
                .findByColorIgnoreCase(color, pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    public Page<ProductResponseDTO> searchProducts(
            String name,
            Long brandId,
            Long categoryId,
            Double minPrice, Double maxPrice,
            String color,
            Pageable pageable) {

        Specification<Product> specification = new Specification<>() {
            @Override
            public @Nullable Predicate toPredicate(
                    Root<Product> root,
                    CriteriaQuery<?> query,
                    CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();

                if (name != null && !name.isEmpty()) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
                }
                if (brandId != null) {
                    predicates.add(criteriaBuilder.equal(root.get("brand").get("id"), brandId));
                }
                if (categoryId != null) {
                    predicates.add(criteriaBuilder.equal(root.get("category").get("id"), categoryId));
                }
                if (minPrice != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
                }
                if (maxPrice != null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
                }
                if (color != null && !color.isEmpty()) {
                    predicates.add(criteriaBuilder.equal(
                            criteriaBuilder.lower(root.get("color")), color.toLowerCase()));
                }

                return criteriaBuilder.and(predicates);
            }
        };

        return productRepository
                .findAll(specification, pageable)
                .map(this::mapToResponseDTO);
    }

    private ProductResponseDTO mapToResponseDTO(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .color(product.getColor())
                .stockQuantity(product.getStockQuantity())
                .brandName(product.getBrand() != null? product.getBrand().getName(): null)
                .categoryName(product.getCategory() != null? product.getCategory().getName(): null)
                .build();
    }
}