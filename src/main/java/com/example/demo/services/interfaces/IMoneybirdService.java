package com.example.demo.services.interfaces;

import com.example.demo.models.SalesInvoice;
import com.example.demo.services.MoneybirdService;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IMoneybirdService {
    SalesInvoice getTestInvoice();
    ResponseEntity<List<SalesInvoice>> getAllInvoices();
    ResponseEntity<SalesInvoice> createNewInvoice(SalesInvoice invoice);
}
