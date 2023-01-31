package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/moneybird")
public class MoneybirdController {
    @Value("${MBBearerToken}")
    private String token;
    @Value("${administrationId}")
    private String adminId;

    @GetMapping
    public ResponseEntity getInvoices() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        ResponseEntity<?> result =
                restTemplate.exchange("https://moneybird.com/api/v2/"
                                + adminId
                                + "/sales_invoices.json",
                        HttpMethod.GET,
                        entity,
                        String.class);

        //return result.getBody();
        return result;
    }
}
