package com.example.Midterm.Services;


import com.example.Midterm.DTOs.Request.RoleRequestDTO;
import com.example.Midterm.DTOs.Response.RoleResponseDTO;

import java.util.List;

public interface RoleService {
    List<RoleResponseDTO> getAll();
    RoleResponseDTO getById(Long id);
    RoleResponseDTO getByName(String name);
    RoleResponseDTO save(RoleRequestDTO roleRequest);
    RoleResponseDTO update(Long id, RoleRequestDTO roleRequest);
    void delete(Long id);
}