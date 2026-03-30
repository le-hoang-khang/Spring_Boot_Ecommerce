package com.example.Midterm.Services;

import com.example.Midterm.Entities.BlackListToken;
import com.example.Midterm.Repositories.BlackListTokenRepository;
import com.example.Midterm.Security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class BlackListTokenServiceImpl implements BlackListTokenService {
    private final BlackListTokenRepository blackListTokenRepository;
    private final JwtTokenProvider tokenProvider;

    @Override
    public void invalidateToken(String token) {
        try {
            BlackListToken blackListToken = new BlackListToken(
                    tokenProvider.getJtiFromToken(token),
                    tokenProvider.getExpiryDateFromToken(token));

            blackListTokenRepository.save(blackListToken);
        } catch (Exception e) {
            System.err.println("Error when processing black list token: " + e.getMessage());
        }
    }

    @Override
    public boolean isBlackListed(String token) {
        if (token == null) {
            return false;
        }
        String jti = tokenProvider.getJtiFromToken(token);
        if (jti == null) {
            return false;
        }
        return blackListTokenRepository.existsById(jti);
    }
}