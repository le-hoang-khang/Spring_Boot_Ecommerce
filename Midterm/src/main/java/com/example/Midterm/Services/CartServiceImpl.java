package com.example.Midterm.Services;

import com.example.Midterm.DTOs.Request.CartProductRequestDTO;
import com.example.Midterm.DTOs.Response.CartProductResponseDTO;
import com.example.Midterm.DTOs.Response.CartResponseDTO;
import com.example.Midterm.Entities.Account;
import com.example.Midterm.Entities.Cart;
import com.example.Midterm.Entities.CartProduct;
import com.example.Midterm.Entities.Product;
import com.example.Midterm.Exceptions.ResourceNotFoundException;
import com.example.Midterm.Repositories.AccountRepository;
import com.example.Midterm.Repositories.CartProductRepository;
import com.example.Midterm.Repositories.CartRepository;
import com.example.Midterm.Repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartProductRepository cartProductRepository;
    private final ProductRepository productRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional(readOnly = true)
    public CartResponseDTO getCartByCustomer(Long customerId) {
        Cart cart = cartRepository
                .findByCustomerId(customerId)
                .orElseGet(() -> createAndAddNewCart(customerId));
        return mapToResponseDTO(cart);
    }

    @Override
    public void addToCart(CartProductRequestDTO cartProductRequest, Long customerId) {
        if (cartProductRequest.getQuantity() == null || cartProductRequest.getQuantity() <= 0) {
            throw new RuntimeException("Quantity to add must be greater than 0");
        }

        Cart cart = cartRepository
                .findByCustomerId(customerId)
                .orElseGet(() -> createAndAddNewCart(customerId));
        Product product = productRepository
                .findById(cartProductRequest.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + cartProductRequest.getProductId()));

        // Increase the quantity of existing product in the cart
        // or create new item
        CartProduct cartProduct = cartProductRepository
                .findByCartIdAndProductId(cart.getId(), product.getId())
                .orElseGet(() -> createNewCartProduct(cart, product));

        Integer newQuantity = cartProduct.getQuantity() + cartProductRequest.getQuantity();

        // Check stock quantity before add
        if (product.getStockQuantity() < newQuantity) {
            throw new RuntimeException("Insufficient quantity");
        }

        cartProduct.setQuantity(newQuantity);
        cartProductRepository.save(cartProduct);
    }

    @Override
    public void updateQuantity(Long productId, Integer quantity, Long customerId) {
        // Delete item if quantity <= 0
        if (quantity == null || quantity <= 0) {
            removeFromCart(productId, customerId);
            return;
        }

        Cart cart = cartRepository
                .findByCustomerId(customerId)
                .orElseGet(() -> createAndAddNewCart(customerId));
        Product product = productRepository
                .findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + productId));

        // Check stock quantity before update
        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient quantity");
        }

        // Set the quantity of existing product in the cart
        // Or create new item
        CartProduct cartProduct = cartProductRepository
                .findByCartIdAndProductId(cart.getId(), product.getId())
                .orElseGet(() -> createNewCartProduct(cart, product));

        cartProduct.setQuantity(quantity);
        cartProductRepository.save(cartProduct);
    }

    @Override
    public void removeFromCart(Long productId, Long customerId) {
        Cart cart = cartRepository
                .findByCustomerId(customerId)
                .orElseGet(() -> createAndAddNewCart(customerId));

        CartProduct cartProduct = cartProductRepository
                .findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        cartProductRepository.delete(cartProduct);
    }

    @Override
    public void clearCart(Long customerId) {
        Cart cart = cartRepository
                .findByCustomerId(customerId)
                .orElseGet(() -> createAndAddNewCart(customerId));
        cartProductRepository.deleteByCartId(cart.getId());
    }

    private CartResponseDTO mapToResponseDTO(Cart cart) {
        List<CartProductResponseDTO> items = cart
                .getProducts()
                .stream()
                .map(item ->
                        CartProductResponseDTO.builder()
                                .id(item.getId())
                                .productId(item.getProduct().getId())
                                .productName(item.getProduct().getName())
                                .unitPrice(item.getProduct().getPrice())
                                .quantity(item.getQuantity())
                                .subTotal(item.getQuantity() * item.getProduct().getPrice())
                                .build()
                ).collect(Collectors.toList());

        return CartResponseDTO.builder()
                .id(cart.getId())
                .customerId(cart.getCustomer().getId())
                .customerName(cart.getCustomer().getFullName())
                .products(items)
                .totalItems(items.size())
                .totalPrice(items.stream().mapToDouble(CartProductResponseDTO::getSubTotal).sum())
                .build();
    }

    private Cart createAndAddNewCart(Long customerId) {
        Account account = accountRepository
                .findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id " + customerId));
        Cart cart = new Cart();
        cart.setCustomer(account);
        return cartRepository.save(cart);
    }

    private CartProduct createNewCartProduct(Cart cart, Product product) {
        CartProduct cartProduct = new CartProduct();
        cartProduct.setCart(cart);
        cartProduct.setProduct(product);
        cartProduct.setQuantity(0);
        cart.getProducts().add(cartProduct);
        return cartProduct;
    }
}