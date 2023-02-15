package com.example.demo.services;

import com.example.demo.models.SalesInvoice;
import com.example.demo.services.interfaces.IMoneybirdService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigInteger;

@Service
public class MoneybirdService implements IMoneybirdService {
    private WebClient webClientWithBaseUrl;
    @Value("${MBBearerToken}")
    private String token;

    @Override
    public String getJsonFromInvoice(SalesInvoice invoice) {
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

    @Override
    public SalesInvoice getTestInvoice() {
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

    @Override
    public ResponseEntity<String> getResponseFromMB(HttpMethod methodType,
                                                    String jsonInvoice) {
        return getWebClientWithBaseUrl()
                .method(methodType)
                .uri("/sales_invoices.json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .body(BodyInserters.fromValue(jsonInvoice))
                .retrieve()
                .toEntity(String.class)
                .block();
    }

    public WebClient getWebClientWithBaseUrl() {
        return webClientWithBaseUrl;
    }

    @Value("${mbApiBaseUrl}")
    private void setWebClientWithBaseUrl(String baseUrl) {
        webClientWithBaseUrl = WebClient.create(baseUrl);
    }
}
