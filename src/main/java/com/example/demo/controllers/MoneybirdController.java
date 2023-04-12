package com.example.demo.controllers;

import lombok.Getter;
import lombok.Setter;
import com.example.demo.models.moneybird.*;
import com.example.demo.services.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private IMoneybirdTaxRatesService taxRatesService;
    private IMoneybirdLedgerAccountService ledgerAccountService;
    private IInvoicerService invoicerService;

    public MoneybirdController(IMoneybirdTaxRatesService taxRatesService,
                               IMoneybirdLedgerAccountService ledgerAccountService,
                               IInvoicerService invoicerService) {
        this.taxRatesService = taxRatesService;
        this.ledgerAccountService = ledgerAccountService;
        this.invoicerService = invoicerService;
    }

    public MoneybirdController() {}

    @GetMapping("/invoices")
    public ResponseEntity<Flux<SalesInvoice>> getAllInvoices() throws Exception {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(invoiceService.getAllInvoices());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Flux.error(ex));
        }
    }

    @PostMapping("/invoices")
    public ResponseEntity<Mono<SalesInvoice>> createInvoice(
            @RequestBody MoneybirdInvoiceRequest invoiceRequest) throws Exception {

        try {
            SalesInvoice invoice = invoiceRequest.getInvoice();

            String contactId = createContact(invoiceRequest.getContact())
                    .getBody();
            invoice.setContactId(new BigInteger(contactId));

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(invoiceService.createInvoice(invoice));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Mono.error(ex));
        }
    }

    @GetMapping("/contacts")
    public ResponseEntity<Flux<MoneybirdContact>> getAllContacts() throws Exception {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(contactService.getAllContacts());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Flux.error(ex));
        }
    }

    @GetMapping("/contacts/{id}")
    private ResponseEntity<Mono<MoneybirdContact>> getContactById(
            @PathVariable String id) throws Exception {

        try {
            return ResponseEntity.status(HttpStatus.OK)
                .body(contactService.getContactById(id));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Mono.error(ex));
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ex.getMessage());
        }
    }

    @GetMapping("/tax-rates")
    public ResponseEntity<Flux<MoneybirdTaxRate>> getAllTaxes() {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(taxRatesService.getAllTaxRates());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Flux.error(ex));
        }
    }

    @GetMapping("/ledgers")
    public ResponseEntity<Flux<MoneybirdLedgerAccount>> getAllLedgers() {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ledgerAccountService.getAllLedgers());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Flux.error(ex));
        }
    }

    @PostMapping("/ledgers")
    public ResponseEntity<String> createLedger(
            @RequestBody MoneybirdLedgerAccount ledger) {

        try {
            String id = ledgerAccountService.getLedgerId(ledger);
            if (id == null) {
                id = ledgerAccountService.createLedger(ledger)
                        .block()
                        .getId();
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(id);
            }
            else
                return ResponseEntity.status(HttpStatus.OK)
                        .body(id);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ex.getMessage());
        }
    }
}
