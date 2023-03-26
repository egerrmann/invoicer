package com.example.demo.services;

import com.example.demo.models.SalesInvoice;
import com.example.demo.services.interfaces.IMoneybirdInvoiceService;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MoneybirdInvoiceService implements IMoneybirdInvoiceService {
    private final WebClient webClientWithBaseUrl;
    private final SalesInvoiceWrapper wrappedInvoice;

    public MoneybirdInvoiceService(WebClient webClientWithBaseUrl, SalesInvoiceWrapper wrappedInvoice) {
        this.webClientWithBaseUrl = webClientWithBaseUrl;
        this.wrappedInvoice = wrappedInvoice;
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
    @Data
    @NoArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class SalesInvoiceWrapper {
        SalesInvoice salesInvoice;
    }
}
