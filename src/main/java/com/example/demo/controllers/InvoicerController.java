package com.example.demo.controllers;

import com.example.demo.services.InvoicerService;
import org.springframework.web.bind.annotation.RestController;

@RestController("/invoicer")
public class InvoicerController {
    private final InvoicerService service;

    public InvoicerController(InvoicerService service) {
        this.service = service;
    }

}
