package com.example.Midterm.Services;

public interface BlackListTokenService {
    void invalidateToken(String token);
    boolean isBlackListed(String token);
}