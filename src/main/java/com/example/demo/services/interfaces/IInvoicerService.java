package com.example.demo.services.interfaces;

import com.example.demo.models.moneybird.SalesInvoice;

import java.util.List;

public interface IInvoicerService {
    List<SalesInvoice> createInvoices();
}
