package com.example.demo.repositories;

import com.example.demo.entities.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IContactRepository extends JpaRepository<Contact, UUID> {
    List<Contact> findByMoneybirdContactId(Long id);
}
