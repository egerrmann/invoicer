package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID contactId;

    // TODO Set as NOT NULL after adding the administration and shop ids into user table
    @ManyToOne
    @JoinColumn(name = "userId"/*, nullable = false*/)
    private User user;

    @Column(unique = true, nullable = false, length = 18)
    private String moneybirdContactId;

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false, length = 50)
    private String address1;

    @Column(nullable = false, length = 50)
    private String address2;

    @Column(nullable = false, length = 35)
    private String city;

    @Column(nullable = false, length = 12)
    private String zipCode;

    @Column(nullable = false, length = 2)
    private String countryIso;
}
