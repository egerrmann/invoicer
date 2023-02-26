package com.example.demo.controllers;

import com.example.demo.models.MoneybirdContact;
import com.example.demo.models.SalesInvoice;
import com.example.demo.services.interfaces.IMoneybirdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/moneybird")
public class MoneybirdController {
    private IMoneybirdService service;

    @GetMapping
    public ResponseEntity<List<SalesInvoice>> getAllInvoices() {
        return service.getAllInvoices();
    }

    @PostMapping
    public ResponseEntity<Mono<SalesInvoice>> createInvoice() {
        SalesInvoice testInvoice = service.getTestInvoice();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createNewInvoice(testInvoice));
    }

    @GetMapping("/contacts")
    public ResponseEntity<List<MoneybirdContact>> getAllContacts() {
        return service.getAllContacts();
    }

    @PostMapping("/contacts")
    public ResponseEntity<Mono<MoneybirdContact>> createContact() {
        MoneybirdContact testContact = service.getTestContact();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createNewContact(testContact));
    }

    @Autowired
    private void setService(IMoneybirdService service) {
        this.service = service;
    }

}
