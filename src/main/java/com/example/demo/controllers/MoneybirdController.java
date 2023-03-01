package com.example.demo.controllers;

import com.example.demo.models.MoneybirdContact;
import com.example.demo.models.SalesInvoice;
import com.example.demo.services.interfaces.IMoneybirdContactService;
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
    private IMoneybirdInvoiceService invoiceService;
    private IMoneybirdContactService contactService;

    @GetMapping("/invoices")
    public ResponseEntity<Flux<SalesInvoice>> getAllInvoices() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(invoiceService.getAllInvoices());
    }

    @PostMapping("/invoices")
    public ResponseEntity<Mono<SalesInvoice>> createInvoice(
            /*@RequestBody SalesInvoice invoice*/) {

        SalesInvoice testInvoice = invoiceService.getTestInvoice();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(invoiceService.createNewInvoice(testInvoice));
    }

    @GetMapping("/contacts")
    public ResponseEntity<Flux<MoneybirdContact>> getAllContacts() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(contactService.getAllContacts());
    }

    public ResponseEntity<Mono<MoneybirdContact>> createContact(
            /*@RequestBody MoneybirdContact contact*/) {

        MoneybirdContact testContact = contactService.getTestContact();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(contactService.createNewContact(testContact));
    }

    // TODO: substitute the testContact variable with a contact method argument
    // TODO: return an id of a created/existing contact
    @PostMapping("/contacts/create")
    public ResponseEntity<Mono<MoneybirdContact>> createContactIfDoesntExist(
            /*@RequestBody MoneybirdContact contact*/) {

        MoneybirdContact testContact = contactService.getTestContact();
        Optional<String> testContactFullName = testContact.getOptionalFullName();
        Optional<String> testContactCompanyName = testContact.getOptionalCompanyName();

        Iterable<MoneybirdContact> addedContacts =
                contactService.getAllContacts().toIterable();
        ResponseEntity<Mono<MoneybirdContact>> result = null;

        for (MoneybirdContact addedContact : addedContacts) {
            Optional<String> addedContactFullName =
                    addedContact.getOptionalFullName();
            Optional<String> addedContactCompanyName =
                    addedContact.getOptionalCompanyName();

            boolean areFullNamesEqual = testContactFullName.isPresent()
                    && addedContactFullName.isPresent()
                    && addedContactFullName.get().equals(testContactFullName.get());
            boolean areCompanyNamesEqual = testContactCompanyName.isPresent()
                    && addedContactCompanyName.isPresent()
                    && testContactCompanyName.get().equals(addedContactCompanyName.get());

            if (areFullNamesEqual || areCompanyNamesEqual) {
                break;
            }
        }

        // TODO: change response body if contact exists
        if (result == null)
            return createContact();
        else
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Mono.empty().ofType(MoneybirdContact.class));
    }

    /*@PostMapping
    public ResponseEntity<Mono<SalesInvoice>> createInvoiceForContact(
            @RequestBody MoneybirdInvoiceRequest) {

        SalesInvoice testInvoice = service.getTestInvoice();
        MoneybirdContact testContact = service.getTestContact();
    }*/

    @Autowired
    private void setInvoiceService(IMoneybirdInvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @Autowired
    public void setContactService(IMoneybirdContactService contactService) {
        this.contactService = contactService;
    }

}
