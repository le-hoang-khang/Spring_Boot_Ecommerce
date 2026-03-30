package com.example.Midterm.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "black_list_tokens")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BlackListToken {
    @Id
    private String id; // JTI (UUID) from token

    @Column(nullable = false)
    private Date expiryDate;
}