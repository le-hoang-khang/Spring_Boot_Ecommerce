package com.example.Midterm.Services;

import com.example.Midterm.DTOs.Request.AccountRequestDTO;
import com.example.Midterm.DTOs.Request.PasswordUpdateRequestDTO;
import com.example.Midterm.DTOs.Response.AccountResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;


public interface AccountService extends UserDetailsService {
    Page<AccountResponseDTO> getAll(Pageable pageable);
    AccountResponseDTO getById(Long id);
    Page<AccountResponseDTO> getByFullName(String fullName, Pageable pageable);
    AccountResponseDTO save(AccountRequestDTO accountRequest);
    AccountResponseDTO update(Long id, AccountRequestDTO accountRequest);
    void updatePassword(Long id, PasswordUpdateRequestDTO passwordUpdateRequest);
    void delete(Long id);
}