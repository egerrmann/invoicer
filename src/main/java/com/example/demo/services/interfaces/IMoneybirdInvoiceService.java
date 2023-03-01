package com.example.demo.services.interfaces;

import com.example.demo.models.SalesInvoice;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IMoneybirdInvoiceService {
    SalesInvoice getTestInvoice();
    Flux<SalesInvoice> getAllInvoices();
    Mono<SalesInvoice> createInvoice(SalesInvoice invoice);
}
