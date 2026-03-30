package com.example.Midterm.Services;

import com.example.Midterm.DTOs.Request.BrandRequestDTO;
import com.example.Midterm.DTOs.Response.BrandResponseDTO;
import com.example.Midterm.Entities.Brand;
import com.example.Midterm.Exceptions.ResourceNotFoundException;
import com.example.Midterm.Repositories.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandServiceImpl implements BrandService {
    private final BrandRepository brandRepository;

    @Override
    public List<BrandResponseDTO> getAll() {
        return brandRepository
                .findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BrandResponseDTO getById(Long id) {
        Brand brand = brandRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + id));
        return mapToResponseDTO(brand);
    }

    @Override
    public BrandResponseDTO getByName(String name) {
        Brand brand = brandRepository
                .findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with name: " + name));
        return mapToResponseDTO(brand);
    }

    @Override
    @Transactional
    public BrandResponseDTO save(BrandRequestDTO brandRequest) {
        Brand brand = new Brand();
        brand.setName(brandRequest.getName());
        brand.setDescription(brandRequest.getDescription());
        return mapToResponseDTO(brandRepository.save(brand));
    }

    @Override
    @Transactional
    public BrandResponseDTO update(Long id, BrandRequestDTO brandRequest) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found to update"));
        if (brandRequest.getName() != null && !brandRequest.getName().isEmpty()) {
            brand.setName(brandRequest.getName());
        }
        if (brandRequest.getDescription() != null && !brandRequest.getDescription().isEmpty()) {
            brand.setDescription(brandRequest.getDescription());
        }
        return mapToResponseDTO(brandRepository.save(brand));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!brandRepository.existsById(id)) {
            throw new ResourceNotFoundException("Brand not found to delete");
        }
        brandRepository.deleteById(id);
    }

    private BrandResponseDTO mapToResponseDTO(Brand brand) {
        return BrandResponseDTO.builder()
                .id(brand.getId())
                .name(brand.getName())
                .description(brand.getDescription())
                .totalProducts(brand.getProducts() != null? brand.getProducts().size(): 0)
                .build();
    }
}