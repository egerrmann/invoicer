package com.example.demo.controllers;

import com.example.demo.models.MoneybirdContact;
import com.example.demo.models.SalesInvoice;
import com.example.demo.services.interfaces.IMoneybirdInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RestController
@RequestMapping("/moneybird")
public class MoneybirdController {
    private IMoneybirdInvoiceService service;

    @GetMapping("/invoices")
    public ResponseEntity<Flux<SalesInvoice>> getAllInvoices() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.getAllInvoices());
    }

    @PostMapping("/invoices")
    public ResponseEntity<Mono<SalesInvoice>> createInvoice(
            @RequestBody SalesInvoice invoice) {

        SalesInvoice testInvoice = service.getTestInvoice();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createNewInvoice(testInvoice));
    }

    @GetMapping("/contacts")
    public ResponseEntity<Flux<MoneybirdContact>> getAllContacts() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(service.getAllContacts());
    }

    @PostMapping("/contacts")
    public ResponseEntity<Mono<MoneybirdContact>> createContact(
            /*@RequestBody MoneybirdContact contact*/) {

        MoneybirdContact testContact = service.getTestContact();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createNewContact(testContact));
    }

    @PostMapping("/contacts/create")
    public ResponseEntity<Mono<MoneybirdContact>> createContactIfDoesntExist(
            /*@RequestBody MoneybirdContact contact*/) {

        MoneybirdContact testContact = service.getTestContact();
        Optional<String> testContactFullName = testContact.getOptionalFullName();
        Optional<String> testContactCompanyName = testContact.getOptionalCompanyName();

        Iterable<MoneybirdContact> contacts = service.getAllContacts().toIterable();
        ResponseEntity<Mono<MoneybirdContact>> result = null;

        for (MoneybirdContact addedContact : contacts) {
            Optional<String> addedContactFullName =
                    addedContact.getOptionalFullName();
            Optional<String> addedContactCompanyName =
                    addedContact.getOptionalCompanyName();

            if (testContactFullName.isPresent()
                    && addedContactFullName.isPresent()
                    && addedContactFullName.get().equals(testContactFullName.get())) {
                result = createContact();
                break;
            } else if (testContactCompanyName.isPresent()
                    && addedContactCompanyName.isPresent()
                    && testContactCompanyName.get().equals(addedContactCompanyName.get())) {
                result = createContact();
                break;
            }
        }

        if (result == null)
            return createContact();
        else
            return ResponseEntity.status(HttpStatusCode.valueOf(405))
                    .body(Mono.empty().ofType(MoneybirdContact.class));
    }

    /*@PostMapping
    public ResponseEntity<Mono<SalesInvoice>> createInvoiceForContact(
            @RequestBody MoneybirdService.InvoiceAndContact invoiceAndContact) {

        SalesInvoice testInvoice = service.getTestInvoice();
        MoneybirdContact testContact = service.getTestContact();
    }*/

    @Autowired
    private void setService(IMoneybirdInvoiceService service) {
        this.service = service;
    }

}
