package com.example.Midterm.Controllers.API;

import com.example.Midterm.DTOs.Request.CartProductRequestDTO;
import com.example.Midterm.DTOs.Response.ApiResponse;
import com.example.Midterm.DTOs.Response.CartResponseDTO;
import com.example.Midterm.Entities.Account;
import com.example.Midterm.Services.CartService;
import com.example.Midterm.Validations.OnCreate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Validated // enable constraints (in case) for @RequestParam and @PathVariable
public class CartRestController {
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponseDTO>> getCart(Authentication authentication) {
        Long customerId = getCustomerIdFromAuthentication(authentication);
        CartResponseDTO result = cartService.getCartByCustomer(customerId);

        return ResponseEntity.ok(
                ApiResponse.<CartResponseDTO>builder()
                        .status(HttpStatus.OK.value())
                        .message("Get cart successfully")
                        .data(result)
                        .build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> addCartItem(
            @Validated(OnCreate.class) @RequestBody CartProductRequestDTO request,
            Authentication authentication) {

        Long customerId = getCustomerIdFromAuthentication(authentication);
        cartService.addToCart(request, customerId);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Added product to cart")
                        .build());
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<Void>> updateCartItemQuantity(
            @Valid @RequestBody CartProductRequestDTO request,
            Authentication authentication) {

        Long customerId = getCustomerIdFromAuthentication(authentication);
        cartService.updateQuantity(
                request.getProductId(),
                request.getQuantity(),
                customerId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Updated quantity of cart item with id " + request.getProductId())
                        .build());
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteCartItem(
            @PathVariable Long productId,
            Authentication authentication) {

        Long customerId = getCustomerIdFromAuthentication(authentication);
        cartService.removeFromCart(productId, customerId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Deleted cart item with id " + productId)
                        .build());
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearCart(Authentication authentication) {
        Long customerId = getCustomerIdFromAuthentication(authentication);
        cartService.clearCart(customerId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Cleared cart")
                        .build());
    }

    private Long getCustomerIdFromAuthentication(Authentication auth) {
        return ((Account) auth.getPrincipal()).getId();
    }
}