package com.example.demo.services;

import com.example.demo.models.SalesInvoice;
import com.example.demo.services.interfaces.IMoneybirdService;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigInteger;
import java.util.List;

@Service
public class MoneybirdService implements IMoneybirdService {
    private WebClient webClientWithBaseUrl;
    private SalesInvoiceWrapper wrappedInvoice;
    @Value("${MBBearerToken}")
    private String token;

    @Override
    public SalesInvoice getTestInvoice() {
        SalesInvoice invoice = new SalesInvoice();
        invoice.setReference("30052");
        invoice.setContactId(new BigInteger("378315484942042971"));
        //invoice.setDiscount(15.5);

        SalesInvoice.DetailsAttributes detailsAttributes =
                new SalesInvoice.DetailsAttributes();
        detailsAttributes.setDescription("My own chair");
        detailsAttributes.setPrice(129.95);
        invoice.getDetailsAttributes().add(detailsAttributes);

        return invoice;
    }

    @Override
    public ResponseEntity<List<SalesInvoice>> getAllInvoices() {
        return webClientWithBaseUrl.get()
                .uri("/sales_invoices.json")
                .retrieve()
                .toEntityList(SalesInvoice.class)
                .block();
    }

    @Override
    public ResponseEntity<SalesInvoice> createNewInvoice(SalesInvoice invoice) {
        // For some reason code from below doesn't work. Apparently, there's
        // a problem with the headers when getting a response from Moneybird
        /*return webClientWithBaseUrl.post()
                .uri("/sales_invoices.json")
                .body(BodyInserters.fromValue(invoice))
                .retrieve()
                .toEntity(SalesInvoice.class)
                .block();*/
        wrappedInvoice.setSalesInvoice(invoice);

        ResponseEntity<SalesInvoice> responseEntity = webClientWithBaseUrl.post()
                .uri("/sales_invoices.json")
                .body(BodyInserters.fromValue(wrappedInvoice))
                .retrieve()
                .toEntity(SalesInvoice.class)
                .block();

        return new ResponseEntity<>(responseEntity.getBody(),
                responseEntity.getStatusCode());
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
