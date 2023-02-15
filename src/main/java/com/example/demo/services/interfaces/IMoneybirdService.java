package com.example.demo.services.interfaces;

import com.example.demo.models.SalesInvoice;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;

public interface IMoneybirdService {
    String getJsonFromInvoice(SalesInvoice invoice);
    SalesInvoice getTestInvoice();
    ResponseEntity<String> getResponseFromMB(HttpMethod methodType,
                                             String jsonInvoice);
}
