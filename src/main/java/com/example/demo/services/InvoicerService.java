package com.example.demo.services;

import com.example.demo.models.etsy.EtsyTransaction;
import com.example.demo.models.moneybird.MoneybirdContact;
import com.example.demo.models.moneybird.MoneybirdTaxRate;
import com.example.demo.models.moneybird.SalesInvoice;
import com.example.demo.models.etsy.EtsyReceipt;
import com.example.demo.services.interfaces.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class InvoicerService implements IInvoicerService {
    private final IEtsyService etsyService;
    private final IMoneybirdContactService contactService;
    private final IMoneybirdInvoiceService invoiceService;
    private final IMoneybirdTaxRatesService taxRatesService;

    public InvoicerService(IEtsyService etsyService, IMoneybirdContactService contactService, IMoneybirdInvoiceService invoiceService, IMoneybirdTaxRatesService taxRatesService) {
        this.etsyService = etsyService;
        this.contactService = contactService;
        this.invoiceService = invoiceService;
        this.taxRatesService = taxRatesService;
    }

    @Override
    public Flux<SalesInvoice> createInvoices() {
        List<EtsyReceipt> receipts = (List<EtsyReceipt>) etsyService.getReceipts().toIterable();
        Flux<SalesInvoice> invoices = Flux.fromIterable(receiptsToInvoices(receipts));

        // TODO: If MB allows creating several invoices at once,
        // then convert "invoices" to Flux<SalesInvoice> and do so.

        return invoices;
    }

    private List<SalesInvoice> receiptsToInvoices(List<EtsyReceipt> receipts) {
        List<SalesInvoice> invoices = new ArrayList<>();
        receipts.forEach(receipt -> {
            Mono<SalesInvoice> resp = invoiceService.createInvoice(receiptToInvoice(receipt));

            resp.subscribe(System.out::println, err -> {
                System.out.println(err.getLocalizedMessage());
            });
            invoices.add(resp.block());
            // If line 32 doesn't work then
            // create a single invoice in MB here.
        });
        return invoices;
    }

    private SalesInvoice receiptToInvoice(EtsyReceipt receipt) {
        SalesInvoice invoice = new SalesInvoice();

        MoneybirdContact contact = contactFromReceipt(receipt);
        if (contactService.getContactId(contact) != null) {
            contact.setId(new BigInteger(contactService.getContactId(contact)));
        } else {
            Mono<MoneybirdContact> resp = contactService.createContact(contact);
            resp.subscribe(null, error -> {
                System.out.println(error.getLocalizedMessage());
            });
            contact = resp.block();
        }
        invoice.setContactId(contact.getId());

        invoice.setInvoiceDate(receipt.getCreateIsoTimeDate());
        invoice.setCurrency(receipt.getTotalPrice().getCurrencyCode());
        // round?
        invoice.setDiscount((double) receipt.getDiscountAmt().getAmount()
                / receipt.getSubtotal().getAmount());

        invoice.setDetailsAttributes(detailsAttributesFromReceipt(receipt));


        return invoice;
    }

    private MoneybirdContact contactFromReceipt(EtsyReceipt receipt) {
        MoneybirdContact contact = new MoneybirdContact();

        // Setting up address
        contact.setAddress1(receipt.getFirstLine());
        contact.setAddress2(receipt.getSecondLine());
        contact.setCity(receipt.getCity());
        contact.setZipcode(receipt.getZip());

        // Getting a country from its ISO
        contact.setCountry(receipt.getCountryIso());
        // End of address part

        // Setting first and last names
        contact.setFirstAndLastName(receipt.getName());

        // Add the rest of the fields and create a contact with the Service

        return contact;
    }

    // Create a list of Invoice Details Attributes according to receipt's transactions
    private List<SalesInvoice.DetailsAttributes> detailsAttributesFromReceipt(EtsyReceipt receipt) {

        // Note: apparently, Moneybird's "DetailAttributes" is
        // (almost) the same thing as Etsy's "Transactions"

        ArrayList<SalesInvoice.DetailsAttributes> attributes = new ArrayList<>();
        MoneybirdTaxRate taxRate = getTaxRate(receipt);

        for (EtsyTransaction transaction : receipt.getTransactions()) {
            SalesInvoice.DetailsAttributes attr = new SalesInvoice.DetailsAttributes();
            assert taxRate != null;
            attr.setTaxRateId(new BigInteger(taxRate.getId()));
            attr.setDescription(transaction.getTitle());
            attr.setAmount(transaction.getQuantity().toString());
            attr.setPrice((double) transaction.getPrice().getAmount());

            // TODO: verify that the following is correct
            // The specified period is from Payment Date to Shipment Date
            attr.setPeriod(transaction.getPaidTimestamp() + ".." + transaction.getShippedTimestamp());

            attributes.add(attr);
        }



        return attributes;
    }

    private MoneybirdTaxRate getTaxRate(EtsyReceipt receipt) {
        List<MoneybirdTaxRate> taxRates = (List<MoneybirdTaxRate>) taxRatesService.getAllTaxRates().toIterable();

        double etsyTaxPercent = (double) receipt.getTotalTaxCost().getAmount() / receipt.getSubtotal().getAmount();

        for (MoneybirdTaxRate rate : taxRates) {
            if (Double.parseDouble(rate.getPercentage()) == etsyTaxPercent)
                return rate;
        }

        return null;
    }
}
