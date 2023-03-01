package com.example.demo.services.interfaces;

import com.example.demo.models.MoneybirdContact;
import com.example.demo.models.SalesInvoice;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IMoneybirdInvoiceService {
    SalesInvoice getTestInvoice();
    MoneybirdContact getTestContact();
    Flux<SalesInvoice> getAllInvoices();
    Mono<SalesInvoice> createNewInvoice(SalesInvoice invoice);
    Flux<MoneybirdContact> getAllContacts();
    Mono<MoneybirdContact> createNewContact(MoneybirdContact contact);
}
