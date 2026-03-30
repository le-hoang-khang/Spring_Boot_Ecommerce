package com.example.Midterm.Repositories;

import com.example.Midterm.Entities.BlackListToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface BlackListTokenRepository extends JpaRepository<BlackListToken, String> {
    void deleteByExpiryDateBefore(Date now);
}