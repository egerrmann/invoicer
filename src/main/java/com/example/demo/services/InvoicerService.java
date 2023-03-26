package com.example.demo.services;

import com.example.demo.models.MoneybirdContact;
import com.example.demo.models.SalesInvoice;
import com.example.demo.models.etsy.EtsyReceipt;
import com.example.demo.services.interfaces.IEtsyService;
import com.example.demo.services.interfaces.IInvoicerService;
import com.example.demo.services.interfaces.IMoneybirdContactService;
import com.example.demo.services.interfaces.IMoneybirdInvoiceService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InvoicerService implements IInvoicerService {
    private final IEtsyService etsyService;
    private final IMoneybirdContactService contactService;
    private final IMoneybirdInvoiceService invoiceService;

    public InvoicerService(IEtsyService etsyService, IMoneybirdContactService contactService, IMoneybirdInvoiceService invoiceService) {
        this.etsyService = etsyService;
        this.contactService = contactService;
        this.invoiceService = invoiceService;
    }

    @Override
    public void createInvoices() {
        List<EtsyReceipt> receipts = (List<EtsyReceipt>) etsyService.getReceipts().toIterable();
        List<SalesInvoice> invoices = receiptsToInvoices(receipts);

        // If MB allows creating several invoices at once,
        // then convert "invoices" to Flux<SalesInvoice> and do so.
    }

    private List<SalesInvoice> receiptsToInvoices(List<EtsyReceipt> receipts) {
        List<SalesInvoice> invoices = new ArrayList<>();
        receipts.forEach(receipt -> {
            invoices.add(receiptToInvoice(receipt));
            // If line 32 doesn't work then
            // create a single invoice in MB here.
        });
        return invoices;
    }

    private SalesInvoice receiptToInvoice(EtsyReceipt receipt) {
        SalesInvoice invoice = new SalesInvoice();

        return invoice;
    }

    private MoneybirdContact contactFromReceipt(EtsyReceipt receipt) {
        MoneybirdContact contact = new MoneybirdContact();

        // Setting up address
        contact.setAddress1(receipt.getFirstLine());
        contact.setAddress2(receipt.getSecondLine());
        contact.setCity(receipt.getCity());
        contact.setZipcode(receipt.getZip());
            // Note: this line is incorrect. But there is no way to get the country from Etsy receipt,
            // so most likely we will need to find a country by its ISO (e.g. FR, NL, etc.).
            // Hopefully, there is already a tool that does it for us.
            contact.setCountry(receipt.getCountryIso());
        // End of address part



        // Add the rest of the fields and create a contact with the Service

        return contact;
    }
}
