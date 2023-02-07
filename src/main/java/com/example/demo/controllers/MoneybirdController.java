package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@RequestMapping("/moneybird")
public class MoneybirdController {
    @Value("${MBBearerToken}")
    private String token;
    @Value("${MBApiLinkBeginning}")
    private String MBLinkBeginning;

    @GetMapping
    public String getAllInvoices() {
        String response = WebClient.create(MBLinkBeginning
                        + "/sales_invoices.json")
                .get()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return response;
    }
}
