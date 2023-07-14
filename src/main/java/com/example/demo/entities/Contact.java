package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "moneybirdContactIdUnique",
                columnNames = "moneybirdContactId")
})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID contactId;

    // TODO Set as NOT NULL after adding the administration and shop ids into user table
    @ManyToOne
    @JoinColumn(name = "userId"/*, nullable = false*/)
    private User user;

    @Column(nullable = false)
    private Long moneybirdContactId;

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(length = 50)
    private String lastName;

    @Column(nullable = false, length = 50)
    private String address1;

    @Column(length = 50)
    private String address2;

    @Column(nullable = false, length = 35)
    private String city;

    @Column(nullable = false, length = 12)
    private String zipCode;

    @Column(nullable = false, length = 2)
    private String countryIso;
}
