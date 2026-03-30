package com.example.Midterm.Services;

import com.example.Midterm.DTOs.Request.RoleRequestDTO;
import com.example.Midterm.DTOs.Response.RoleResponseDTO;
import com.example.Midterm.Entities.Role;
import com.example.Midterm.Exceptions.ResourceNotFoundException;
import com.example.Midterm.Repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    public List<RoleResponseDTO> getAll() {
        return roleRepository
                .findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RoleResponseDTO getById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
        return mapToResponseDTO(role);
    }

    @Override
    public RoleResponseDTO getByName(String name) {
        Role role = roleRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with name: " + name));
        return mapToResponseDTO(role);
    }

    @Override
    @Transactional
    public RoleResponseDTO save(RoleRequestDTO roleRequest) {
        Role role = new Role();
        role.setName(roleRequest.getName());
        return mapToResponseDTO(roleRepository.save(role));
    }

    @Override
    @Transactional
    public RoleResponseDTO update(Long id, RoleRequestDTO roleRequest) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found to update"));
        if (roleRequest.getName() != null && !roleRequest.getName().isEmpty()) {
            role.setName(roleRequest.getName());
        }
        return mapToResponseDTO(roleRepository.save(role));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role not found to delete");
        }
        roleRepository.deleteById(id);
    }

    private RoleResponseDTO mapToResponseDTO(Role role) {
        return RoleResponseDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }
}