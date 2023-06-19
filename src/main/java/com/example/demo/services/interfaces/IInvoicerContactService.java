package com.example.demo.services.interfaces;

import com.example.demo.models.etsy.EtsyReceipt;
import com.example.demo.models.moneybird.SalesInvoice;

public interface IInvoicerContactService {
    void setContactIdForInvoice(SalesInvoice invoice, EtsyReceipt receipt);
    void updateContactTable();
}
