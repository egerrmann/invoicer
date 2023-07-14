package com.example.demo.controllers;

import com.example.demo.models.moneybird.SalesInvoice;
import com.example.demo.services.InvoicerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
// TODO: change the URL (to "/index" I suppose)
@RequestMapping("/invoicer")
public class InvoicerController {
    private final InvoicerService service;

    public InvoicerController(InvoicerService service) {
        this.service = service;
    }

    @GetMapping("/add-invoices")
    public ResponseEntity<List<SalesInvoice>> addAllInvoices() {
        // TODO I suppose we have to leave only 'try'-block, because
        //  CustomExceptionHandler returns a correct response entity depending
        //  on an exception thrown and there is no need to handle it over here
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(service.createInvoices());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(List.of());
        }
    }
}
