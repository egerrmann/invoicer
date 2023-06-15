package com.example.demo.controllers;

import com.example.demo.models.moneybird.*;
import com.example.demo.repositories.IContactRepository;
import com.example.demo.services.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/moneybird")
public class MoneybirdController {
    private IMoneybirdInvoiceService invoiceService;
    private IMoneybirdContactService contactService;
    private final IMoneybirdTaxRatesService taxRatesService;
    private final IMoneybirdLedgerAccountService ledgerAccountService;
    private final IInvoicerService invoicerService;
    private final IContactRepository contactRepository;

    public MoneybirdController(IMoneybirdTaxRatesService taxRatesService,
                               IMoneybirdLedgerAccountService ledgerAccountService, IInvoicerService invoicerService, IContactRepository contactRepository) {
        this.taxRatesService = taxRatesService;
        this.ledgerAccountService = ledgerAccountService;
        this.invoicerService = invoicerService;
        this.contactRepository = contactRepository;
    }

    @GetMapping("/invoices")
    public ResponseEntity<Flux<SalesInvoice>> getAllInvoices() {
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
            @RequestBody MoneybirdInvoiceRequest invoiceRequest) {

        try {
            SalesInvoice invoice = invoiceRequest.getInvoice();

            Long contactId = createContact(invoiceRequest.getContact())
                    .getBody();
            invoice.setContactId(contactId);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(invoiceService.createInvoice(invoice));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Mono.error(ex));
        }
    }

    @GetMapping("/contacts")
    public ResponseEntity<Flux<MoneybirdContact>> getAllContacts() {
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
            @PathVariable String id) {

        try {
            return ResponseEntity.status(HttpStatus.OK)
                .body(contactService.getContactById(id));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Mono.error(ex));
        }
    }

    @PostMapping("/contacts")
    public ResponseEntity<Long> createContact(
            @RequestBody MoneybirdContact contact) {

        try {
            Long id = contactService.getContactId(contact);
            if (id == null) {
                Long body = contactService
                        .createContact(contact)
                        .block()
                        .getId();

                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(body);
            }
            // status code?
            else
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(id);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/tax-rates")
    public ResponseEntity<Flux<MoneybirdTaxRate>> getAllTaxes() {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(taxRatesService.getAllTaxRates(""));
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
            String id = ledgerAccountService.getLedgerId(ledger.getName());
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

    // TODO Delete it if it's extra (we should, i suppose)
    @PatchMapping("/invoices/{id}/send-invoice")
    public ResponseEntity<Mono<SalesInvoice>> sendInvoice(
            @PathVariable String id) {

        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(invoiceService.sendInvoice(id));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Mono.error(ex));
        }
    }

    @GetMapping
    public String testDB() {
        contactService.updateContactTable();
        return "Table is updated";
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
