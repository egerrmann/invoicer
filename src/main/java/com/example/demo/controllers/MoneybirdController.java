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
        SalesInvoice invoice = getTestInvoice();

        String jsonInvoice = getJsonFromInvoice(invoice);

        String createdInvoice = WebClient.create(mbApiBaseUrl
                + "/sales_invoices.json")
                .post()
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .body(BodyInserters.fromValue(jsonInvoice))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return createdInvoice;
    }

    private String getJsonFromInvoice(SalesInvoice invoice) {
        ObjectWriter objectWriter = new ObjectMapper()
                .writer()
                .withDefaultPrettyPrinter();

        String jsonInvoice = "{\"sales_invoice\":";
        try {
            jsonInvoice += objectWriter.writeValueAsString(invoice);
        } catch (Exception e) {
            e.printStackTrace();
        }
        jsonInvoice += "}";

        return jsonInvoice;
    }

    private SalesInvoice getTestInvoice() {
        SalesInvoice invoice = new SalesInvoice();
        invoice.setReference("30052");
        invoice.setContactId(new BigInteger("378315484942042971"));
        //invoice.setDiscount(15.5);

        SalesInvoice.DetailsAttributes detailsAttributes =
                invoice.new DetailsAttributes();
        detailsAttributes.setDescription("My own chair");
        detailsAttributes.setPrice(129.95);
        invoice.getDetailsAttributes().add(detailsAttributes);

        return invoice;
    }
}
