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
    private MoneybirdService.SalesInvoiceWrapper wrapper;

    @GetMapping
    public ResponseEntity<List<SalesInvoice>> getAllInvoices() {
        return service.getAllInvoices();
    }

    @PostMapping
    public ResponseEntity<SalesInvoice> createInvoice() {
        SalesInvoice invoice = service.getTestInvoice();
        wrapper.setSalesInvoice(invoice);
        return service.createNewInvoice(wrapper);
    }

    @Autowired
    private void setService(IMoneybirdService service) {
        this.service = service;
    }

    @Autowired
    private void setWrapper(MoneybirdService.SalesInvoiceWrapper wrapper) {
        this.wrapper = wrapper;
    }
}
