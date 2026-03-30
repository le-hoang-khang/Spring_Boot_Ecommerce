package com.example.Midterm.Controllers.API;

import com.example.Midterm.DTOs.Request.ProductRequestDTO;
import com.example.Midterm.DTOs.Response.ApiResponse;
import com.example.Midterm.DTOs.Response.ProductResponseDTO;
import com.example.Midterm.Services.ProductService;
import com.example.Midterm.Validations.OnCreate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductRestController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponseDTO>>> getAllProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String color,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<ProductResponseDTO> results = productService.searchProducts(
                name,
                brandId,
                categoryId,
                minPrice,
                maxPrice,
                color,
                pageable);

        return ResponseEntity.ok(
                ApiResponse.<Page<ProductResponseDTO>>builder()
                        .status(HttpStatus.OK.value())
                        .message("Get products successfully")
                        .data(results)
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> getProductById(
            @PathVariable Long id) {
        ProductResponseDTO result = productService.getById(id);

        return ResponseEntity.ok(
                ApiResponse.<ProductResponseDTO>builder()
                        .status(HttpStatus.OK.value())
                        .message("Get product successfully with id " + id)
                        .data(result)
                        .build());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> createProduct(
            @Validated(OnCreate.class) @RequestBody ProductRequestDTO request) {
        ProductResponseDTO result = productService.save(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.<ProductResponseDTO>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Created product")
                        .data(result)
                        .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO request) {
        ProductResponseDTO result = productService.update(id, request);

        return ResponseEntity.ok(
                ApiResponse.<ProductResponseDTO>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Updated product with id " + id)
                        .data(result)
                        .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Deleted product with id " + id)
                        .build());
    }
}