package com.example.demo.services;

import com.example.demo.models.moneybird.MoneybirdContact;
import com.example.demo.models.moneybird.MoneybirdTaxRate;
import com.example.demo.models.moneybird.SalesInvoice;
import com.example.demo.models.etsy.EtsyReceipt;
import com.example.demo.services.interfaces.IEtsyService;
import com.example.demo.services.interfaces.IInvoicerService;
import com.example.demo.services.interfaces.IMoneybirdContactService;
import com.example.demo.services.interfaces.IMoneybirdInvoiceService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    public Flux<SalesInvoice> createInvoices() {
        List<EtsyReceipt> receipts = (List<EtsyReceipt>) etsyService.getReceipts().toIterable();
        List<SalesInvoice> invoices = receiptsToInvoices(receipts);

        // If MB allows creating several invoices at once,
        // then convert "invoices" to Flux<SalesInvoice> and do so.

        return null;
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

        invoice.setInvoiceDate(receipt.getCreateIsoDate());
        invoice.setCurrency(receipt.getTotalPrice().getCurrencyCode());
        // round?
        invoice.setDiscount((double) receipt.getDiscountAmt().getAmount()
                / receipt.getSubtotal().getAmount());

        SalesInvoice.DetailsAttributes attributes =
                new SalesInvoice.DetailsAttributes();
        /*attributes.setPrice(receipt.getTotalPrice().getAmount());
        invoice.getDetailsAttributes()
                .add()*/


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

    private MoneybirdTaxRate getTaxRate(MoneybirdTaxRate taxRate) {
        return null;
    }
}
