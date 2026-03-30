package com.example.Midterm.Services;


import com.example.Midterm.DTOs.Request.CategoryRequestDTO;
import com.example.Midterm.DTOs.Response.CategoryResponseDTO;

import java.util.List;

public interface CategoryService {
    List<CategoryResponseDTO> getAll();
    CategoryResponseDTO getById(Long id);
    CategoryResponseDTO getByName(String name);
    CategoryResponseDTO save(CategoryRequestDTO categoryRequest);
    CategoryResponseDTO update(Long id, CategoryRequestDTO categoryRequest);
    void delete(Long id);
}