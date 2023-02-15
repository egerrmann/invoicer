package com.example.demo.controllers;

import com.example.demo.models.SalesInvoice;
import com.example.demo.services.interfaces.IMoneybirdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.Optional;

@RestController
@RequestMapping("/moneybird")
public class MoneybirdController {
    @Value("${mbApiBaseUrl}")
    public String mbApiBaseUrl;
    @Value("${MBBearerToken}")
    private String token;
    private IMoneybirdService service;

    @GetMapping
    public ResponseEntity<String> getAllInvoices() {
        return service.getResponseFromMB(HttpMethod.GET, "");
    }

    @PostMapping
    public ResponseEntity<String> createInvoice() {
        SalesInvoice invoice = service.getTestInvoice();

        String jsonInvoice = service.getJsonFromInvoice(invoice);

        return service.getResponseFromMB(HttpMethod.POST, jsonInvoice);
    }

    @Autowired
    public void setService(IMoneybirdService service) {
        this.service = service;
    }
}
