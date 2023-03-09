package com.example.demo.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoneybirdInvoiceRequest {
    private SalesInvoice invoice;
    private MoneybirdContact contact;
}
