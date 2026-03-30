package com.example.Midterm.Services;

import com.example.Midterm.DTOs.Request.BrandRequestDTO;
import com.example.Midterm.DTOs.Response.BrandResponseDTO;

import java.util.List;

public interface BrandService {
    List<BrandResponseDTO> getAll();
    BrandResponseDTO getById(Long id);
    BrandResponseDTO getByName(String name);
    BrandResponseDTO save(BrandRequestDTO brandRequest);
    BrandResponseDTO update(Long id, BrandRequestDTO brandRequest);
    void delete(Long id);
}