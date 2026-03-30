package com.example.Midterm.Repositories;

import com.example.Midterm.Entities.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);

    Page<Account> findByFullNameContainingIgnoreCase(String fullName, Pageable pageable);
    Page<Account> findByRoleId(Long roleId, Pageable pageable);
    Page<Account> findByRoleName(String roleName, Pageable pageable);
}