package com.example.demo.controllers;

import com.example.demo.models.SalesInvoice;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigInteger;

@RestController
@RequestMapping("/moneybird")
public class MoneybirdController {
    @Value("${MBBearerToken}")
    private String token;
    @Value("${mbApiBaseUrl}")
    private String mbApiBaseUrl;

    @GetMapping
    public String getAllInvoices() {
        String response = WebClient.create(mbApiBaseUrl
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

    @PostMapping
    public String createInvoice() {
        /*SalesInvoice invoice = new SalesInvoice();
        invoice.setReference("30052");
        invoice.setContactId(new BigInteger("378315484942042971"));
        invoice.getDetailsAttributes().setDescription("Rocking Chair");
        invoice.getDetailsAttributes().setPrice(129.95);

        ObjectWriter objectWriter = new ObjectMapper()
                .writer()
                .withDefaultPrettyPrinter();

        String jsonInvoice = "";
        try {
            jsonInvoice = objectWriter.writeValueAsString(invoice);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        String jsonFromExample = "{\"sales_invoice\":{\"reference\":\"30052\",\"contact_id\":378315484942042971,\"details_attributes\":[{\"description\":\"Rocking Chair\",\"price\":129.95}]}}";

        String createdInvoice = WebClient.create(mbApiBaseUrl
                + "/sales_invoices.json")
                .post()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .body(BodyInserters.fromValue(jsonFromExample))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return createdInvoice;
    }
}
