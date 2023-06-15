package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;

    @Column(nullable = false, length = 18)
    private Long moneybirdAccountId;

    @Column(nullable = false, length = 10)
    private Long etsyShopId;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Set<Contact> contacts;
}
