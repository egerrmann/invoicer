package com.example.demo.services.interfaces;

import com.example.demo.models.MoneybirdContact;
import com.example.demo.models.SalesInvoice;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IMoneybirdService {
    SalesInvoice getTestInvoice();
    MoneybirdContact getTestContact();
    ResponseEntity<List<SalesInvoice>> getAllInvoices();
    Mono<SalesInvoice> createNewInvoice(SalesInvoice invoice);
    ResponseEntity<List<MoneybirdContact>> getAllContacts();
    Mono<MoneybirdContact> createNewContact(MoneybirdContact contact);
}
