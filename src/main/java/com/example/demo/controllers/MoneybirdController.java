package com.example.demo.controllers;

import com.example.demo.models.MoneybirdContact;
import com.example.demo.models.SalesInvoice;
import com.example.demo.services.interfaces.IMoneybirdContactService;
import com.example.demo.services.interfaces.IMoneybirdInvoiceService;
import lombok.Getter;
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
    // decide if getter is needed
    @Getter
    private IMoneybirdContactService contactService;

    @GetMapping("/invoices")
    public ResponseEntity<Flux<SalesInvoice>> getAllInvoices() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(invoiceService.getAllInvoices());
    }

    @PostMapping("/invoices")
    public ResponseEntity<Mono<SalesInvoice>> createInvoice(
            /*@RequestBody MoneybirdInvoiceRequest invoiceRequest*/) {

        SalesInvoice testInvoice = invoiceService.getTestInvoice();

        String contactId = createContact().getBody();
        testInvoice.setContactId(new BigInteger(contactId));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(invoiceService.createInvoice(testInvoice));
    }

    @GetMapping("/contacts")
    public ResponseEntity<Flux<MoneybirdContact>> getAllContacts() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(contactService.getAllContacts());
    }

    @GetMapping("/contacts/{id}")
    private ResponseEntity<Mono<MoneybirdContact>> getContactById(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(contactService.getContactById(id));
    }

    // TODO: substitute the testContact variable with a contact method argument
    @PostMapping("/contacts")
    public ResponseEntity<String> createContact(
            /*@RequestBody MoneybirdContact contact*/) {

        String id = contactService.getContactId();
        if (id == null) {
            String body = contactService
                    .createContact(contactService.getTestContact())
                    .block()
                    .getId()
                    .toString();

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(body);
        }
        // status code?
        else
            return ResponseEntity.status(HttpStatus.OK)
                    .body(id);
    }

    @Autowired
    private void setInvoiceService(IMoneybirdInvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @Autowired
    public void setContactService(IMoneybirdContactService contactService) {
        this.contactService = contactService;
    }

}
