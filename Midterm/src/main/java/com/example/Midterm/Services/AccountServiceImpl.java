package com.example.Midterm.Services;

import com.example.Midterm.DTOs.Request.AccountRequestDTO;
import com.example.Midterm.DTOs.Request.PasswordUpdateRequestDTO;
import com.example.Midterm.DTOs.Response.AccountResponseDTO;
import com.example.Midterm.Entities.Account;
import com.example.Midterm.Entities.Cart;
import com.example.Midterm.Entities.Role;
import com.example.Midterm.Exceptions.DuplicateResourceException;
import com.example.Midterm.Exceptions.IncorrectOldPasswordException;
import com.example.Midterm.Exceptions.ResourceNotFoundException;
import com.example.Midterm.Repositories.AccountRepository;
import com.example.Midterm.Repositories.CartRepository;
import com.example.Midterm.Repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public Page<AccountResponseDTO> getAll(Pageable pageable) {
        return accountRepository
                .findAll(pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    public AccountResponseDTO getById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
        return mapToResponseDTO(account);
    }

    @Override
    public Page<AccountResponseDTO> getByFullName(String fullName, Pageable pageable) {
        return accountRepository
                .findByFullNameContainingIgnoreCase(fullName, pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    @Transactional
    public AccountResponseDTO save(AccountRequestDTO accountRequest) {
        // Check existing username and email
        if (accountRepository.existsByUsername(accountRequest.getUsername())) {
            throw new DuplicateResourceException("Username '" + accountRequest.getUsername() + "' is existed");
        }
        if (accountRepository.existsByEmail(accountRequest.getEmail())) {
            throw new DuplicateResourceException("Email '" + accountRequest.getEmail() + "' is existed");
        }

        // Create new account
        Account account = new Account();
        account.setUsername(accountRequest.getUsername());
        account.setPassword(passwordEncoder.encode(accountRequest.getPassword()));
        account.setFullName(accountRequest.getFullName());
        account.setEmail(accountRequest.getEmail());
        account.setAddress(accountRequest.getAddress());
        account.setPhoneNumber(accountRequest.getPhoneNumber());

        // Find current role if role id is provided
        // Otherwise assign ROLE_USER by default
        Role role;
        if (accountRequest.getRoleId() != null) {
            role = roleRepository
                    .findById(accountRequest.getRoleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + accountRequest.getRoleId()));
        } else {
            role = roleRepository
                    .findByName("ROLE_USER")
                    .orElseThrow(() -> new ResourceNotFoundException("Role User not found"));
        }
        account.setRole(role);

        // Create an empty cart for user after register successfully
        Cart cart = new Cart();
        cart.setCustomer(account);
        cartRepository.save(cart);

        return mapToResponseDTO(accountRepository.save(account));
    }

    @Override
    @Transactional
    public AccountResponseDTO update(Long id, AccountRequestDTO accountRequest) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));

        // Check existing email
        if (accountRepository.existsByEmailAndIdNot(accountRequest.getEmail(), account.getId())) {
            throw new DuplicateResourceException("Email '" + accountRequest.getEmail() + "' is existed");
        }

        // Set new information
        if (accountRequest.getFullName() != null && !accountRequest.getFullName().isEmpty()) {
            account.setFullName(accountRequest.getFullName());
        }
        if (accountRequest.getEmail() != null && !accountRequest.getEmail().isEmpty()) {
            account.setEmail(accountRequest.getEmail());
        }
        if (accountRequest.getAddress() != null && !accountRequest.getAddress().isEmpty()) {
            account.setAddress(accountRequest.getAddress());
        }
        if (accountRequest.getPhoneNumber() != null && !accountRequest.getPhoneNumber().isEmpty()) {
            account.setPhoneNumber(accountRequest.getPhoneNumber());
        }
        if (accountRequest.getRoleId() != null) {
            Role role = roleRepository.findById(accountRequest.getRoleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
            account.setRole(role);
        }

        return mapToResponseDTO(accountRepository.save(account));
    }

    @Override
    @Transactional
    public void updatePassword(Long id, PasswordUpdateRequestDTO passwordUpdateRequest) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));

        // Check old password
        if (!passwordEncoder.matches(passwordUpdateRequest.getOldPassword(), account.getPassword())) {
            throw new IncorrectOldPasswordException("Old password is not correct");
        }

        // Save new password
        account.setPassword(passwordEncoder.encode(passwordUpdateRequest.getNewPassword()));
        accountRepository.save(account);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new ResourceNotFoundException("Account not found to delete");
        }
        accountRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return accountRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username " + username));
    }

    private AccountResponseDTO mapToResponseDTO(Account account) {
        return AccountResponseDTO.builder()
                .id(account.getId())
                .username(account.getUsername())
                .fullName(account.getFullName())
                .email(account.getEmail())
                .address(account.getAddress())
                .phoneNumber(account.getPhoneNumber())
                .roleName(account.getRole().getName())
                .build();
    }
}