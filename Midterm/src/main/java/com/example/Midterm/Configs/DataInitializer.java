package com.example.Midterm.Configs;

import com.example.Midterm.DTOs.Request.AccountRequestDTO;
import com.example.Midterm.DTOs.Request.BrandRequestDTO;
import com.example.Midterm.DTOs.Request.CategoryRequestDTO;
import com.example.Midterm.DTOs.Request.ProductRequestDTO;
import com.example.Midterm.DTOs.Request.RoleRequestDTO;
import com.example.Midterm.Services.AccountService;
import com.example.Midterm.Services.BrandService;
import com.example.Midterm.Services.CategoryService;
import com.example.Midterm.Services.ProductService;
import com.example.Midterm.Services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final RoleService roleService;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final AccountService accountService;
    private final ProductService productService;


    @Override
    @Transactional
    public void run(String... args) throws Exception {
        initiateRoles(new String[] {"ROLE_ADMIN", "ROLE_USER"});
        initiateBrands(new String[] {
                "Apple",
                "Samsung",
                "Sony",
                "Dell",
                "Asus"
        });
        initiateCategories(new String[] {
                "Smartphone",
                "Laptop",
                "Tablet",
                "Accessories",
                "Smartwatch"
        });
        initiateAccounts();
        initiateProducts();
    }


    private void initiateRoles(String[] roleNames) {
        if (roleService.getAll().isEmpty()) {
            for (String roleName: roleNames) {
                RoleRequestDTO roleDTO = new RoleRequestDTO();
                roleDTO.setName(roleName);
                roleService.save(roleDTO);
            }

            System.out.println("Initiated Roles");
        }
    }

    private void initiateBrands(String[] brandNames) {
        if (brandService.getAll().isEmpty()) {
            for (String brandName: brandNames) {
                BrandRequestDTO brandDTO = new BrandRequestDTO();
                brandDTO.setName(brandName);
                brandDTO.setDescription("A leading technology brand " + brandName);
                brandService.save(brandDTO);
            }

            System.out.println("Initiated Brands");
        }
    }

    private void initiateCategories(String[] categoryNames) {
        if (categoryService.getAll().isEmpty()) {
            for (String categoryName: categoryNames) {
                CategoryRequestDTO categoryDTO = new CategoryRequestDTO();
                categoryDTO.setName(categoryName);
                categoryService.save(categoryDTO);
            }

            System.out.println("Initiated Categories");
        }
    }

    private void initiateAccounts() {
        if (accountService.getAll(Pageable.ofSize(1)).isEmpty()) {
            Long adminRoleId = roleService.getByName("ROLE_ADMIN").getId();
            Long userRoleId = roleService.getByName("ROLE_USER").getId();

            // Create admin account
            AccountRequestDTO adminDTO = new AccountRequestDTO();
            adminDTO.setUsername("admin");
            adminDTO.setPassword("admin123");
            adminDTO.setFullName("Administrator");
            adminDTO.setEmail("admin@gmail.com");
            adminDTO.setRoleId(adminRoleId);
            accountService.save(adminDTO);

            // Create user account
            AccountRequestDTO customerDTO = new AccountRequestDTO();
            customerDTO.setUsername("customer01");
            customerDTO.setPassword("customer123");
            customerDTO.setFullName("Customer 01");
            customerDTO.setEmail("customer@gmail.com");
            customerDTO.setRoleId(userRoleId);
            accountService.save(customerDTO);

            System.out.println("Initiated Accounts and Carts");
        }
    }

    private void initiateProducts() {
        if (productService.getAll(Pageable.ofSize(1)).isEmpty()) {
            Long appleId = brandService.getByName("Apple").getId();
            Long samsungId = brandService.getByName("Samsung").getId();
            Long laptopId = categoryService.getByName("Laptop").getId();
            Long phoneId = categoryService.getByName("Smartphone").getId();

            // Create iPhone
            ProductRequestDTO p1 = new ProductRequestDTO();
            p1.setName("iPhone 15 Pro Max");
            p1.setPrice(34990000.0);
            p1.setColor("Natural Titanium");
            p1.setStockQuantity(50);
            p1.setBrandId(appleId);
            p1.setCategoryId(phoneId);
            productService.save(p1);

            // Create Galaxy S24
            ProductRequestDTO p2 = new ProductRequestDTO();
            p2.setName("Galaxy S24 Ultra");
            p2.setPrice(29990000.0);
            p2.setColor("Gray Titanium");
            p2.setStockQuantity(30);
            p2.setBrandId(samsungId);
            p2.setCategoryId(phoneId);
            productService.save(p2);

            // Create MacBook
            ProductRequestDTO p3 = new ProductRequestDTO();
            p3.setName("MacBook Air M3");
            p3.setPrice(27990000.0);
            p3.setColor("Starlight");
            p3.setStockQuantity(20);
            p3.setBrandId(appleId);
            p3.setCategoryId(laptopId);
            productService.save(p3);

            System.out.println("Initiated Products");
        }
    }
}