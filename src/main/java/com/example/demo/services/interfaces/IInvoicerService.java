package com.example.demo.services.interfaces;

import com.example.demo.models.moneybird.SalesInvoice;
import reactor.core.publisher.Flux;

public interface IInvoicerService {
    Flux<SalesInvoice> createInvoices();
}
