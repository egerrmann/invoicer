package com.example.demo.controllers;

import com.example.demo.models.SalesInvoice;
import com.example.demo.services.MoneybirdService;
import com.example.demo.services.interfaces.IMoneybirdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<SalesInvoice> createInvoice() {
        SalesInvoice testInvoice = service.getTestInvoice();
        MoneybirdService.SalesInvoiceWrapper wrappedInvoice =
                service.getWrappedInvoice(testInvoice);
        return service.createNewInvoice(wrappedInvoice);
    }

    @Autowired
    private void setService(IMoneybirdService service) {
        this.service = service;
    }

}
