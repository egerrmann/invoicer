package com.example.demo.services;

import com.example.demo.models.SalesInvoice;
import com.example.demo.services.interfaces.IMoneybirdInvoiceService;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

@Service
public class MoneybirdInvoiceService implements IMoneybirdInvoiceService {
    private WebClient webClientWithBaseUrl;
    private SalesInvoiceWrapper wrappedInvoice;
    @Value("${MBBearerToken}")
    private String token;

    @Override
    public SalesInvoice getTestInvoice() {
        SalesInvoice invoice = new SalesInvoice();
        invoice.setReference("30052");
        invoice.setContactId(new BigInteger("380279277811139756"));
        //invoice.setDiscount(15.5);

        SalesInvoice.DetailsAttributes detailsAttributes =
                new SalesInvoice.DetailsAttributes();
        detailsAttributes.setDescription("My own chair");
        detailsAttributes.setPrice(129.95);
        invoice.getDetailsAttributes().add(detailsAttributes);

        return invoice;
    }

    @Override
    public Flux<SalesInvoice> getAllInvoices() {
        return webClientWithBaseUrl.get()
                .uri("/sales_invoices.json")
                .exchangeToFlux(response -> {
                    if (response.statusCode().equals(HttpStatus.OK))
                        return response.bodyToFlux(SalesInvoice.class);
                    else return response.createError().flux().cast(SalesInvoice.class);
                });
    }

    @Override
    public Mono<SalesInvoice> createInvoice(SalesInvoice invoice) {
        wrappedInvoice.setSalesInvoice(invoice);

        return webClientWithBaseUrl.post()
                .uri("/sales_invoices.json")
                .body(BodyInserters.fromValue(wrappedInvoice))
                .exchangeToMono(response -> {
                    if (response.statusCode() == HttpStatus.CREATED)
                        return response.bodyToMono(SalesInvoice.class);
                    else return response.createError();
                });
    }

    @Component
    @Getter
    @Setter
    @NoArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class SalesInvoiceWrapper {
        SalesInvoice salesInvoice;
    }

    @Value("${mbApiBaseUrl}")
    private void setWebClientWithBaseUrl(String baseUrl) {
        webClientWithBaseUrl = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
    }

    @Autowired
    private void setWrappedInvoice(SalesInvoiceWrapper wrappedInvoice) {
        this.wrappedInvoice = wrappedInvoice;
    }
}
