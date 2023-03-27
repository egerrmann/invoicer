package com.example.demo.models.moneybird;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoneybirdInvoiceRequest {
    private SalesInvoice invoice;
    private MoneybirdContact contact;
}
