package com.example.demo.controllers;

import com.example.demo.models.MoneybirdContact;
import com.example.demo.models.MoneybirdInvoiceRequest;
import com.example.demo.models.SalesInvoice;
import com.example.demo.services.interfaces.IMoneybirdContactService;
import com.example.demo.services.interfaces.IMoneybirdInvoiceService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

@RestController
@RequestMapping("/moneybird")
public class MoneybirdController {
    private IMoneybirdInvoiceService invoiceService;
    // decide if getter and setter are needed for testing
    @Getter
    @Setter
    private IMoneybirdContactService contactService;

    @Autowired
    public MoneybirdController(IMoneybirdInvoiceService invoiceService, IMoneybirdContactService contactService) {
        this.invoiceService = invoiceService;
        this.contactService = contactService;
    }

    public MoneybirdController() {}

    @GetMapping("/invoices")
    public ResponseEntity<Flux<SalesInvoice>> getAllInvoices() throws Exception {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(invoiceService.getAllInvoices());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Flux.error(ex));
        }
    }

    @PostMapping("/invoices")
    public ResponseEntity<Mono<SalesInvoice>> createInvoice(
            @RequestBody MoneybirdInvoiceRequest invoiceRequest) throws Exception {

        try {
            SalesInvoice invoice = invoiceRequest.getInvoice();

            BigInteger contactId = contactService
                    .createContact(invoiceRequest.getContact())
                    .block()
                    .getId();
            invoice.setContactId(contactId);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(invoiceService.createInvoice(invoice));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Mono.error(ex));
        }
    }

    @GetMapping("/contacts")
    public ResponseEntity<Flux<MoneybirdContact>> getAllContacts() throws Exception {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(contactService.getAllContacts());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Flux.error(ex));
        }
    }

    @GetMapping("/contacts/{id}")
    private ResponseEntity<Mono<MoneybirdContact>> getContactById(
            @PathVariable String id) throws Exception {

        try {
            return ResponseEntity.status(HttpStatus.OK)
                .body(contactService.getContactById(id));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Mono.error(ex));
        }
    }

    @PostMapping("/contacts")
    public ResponseEntity<String> createContact(
            @RequestBody MoneybirdContact contact) throws Exception {

        try {
            String id = contactService.getContactId(contact);
            if (id == null) {
                String body = contactService
                        .createContact(contact)
                        .block()
                        .getId()
                        .toString();

                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(body);
            }
            else
                return ResponseEntity.status(HttpStatus.OK)
                        .body(id);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }
}
