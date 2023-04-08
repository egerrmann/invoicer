package com.example.demo.services;

import com.example.demo.models.etsy.EtsyTransaction;
import com.example.demo.models.moneybird.MoneybirdContact;
import com.example.demo.models.moneybird.MoneybirdTaxRate;
import com.example.demo.models.moneybird.SalesInvoice;
import com.example.demo.models.etsy.EtsyReceipt;
import com.example.demo.services.interfaces.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.text.DecimalFormat;
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
    public List<SalesInvoice> createInvoices() {
        List<EtsyReceipt> receipts = etsyService.getReceiptsList();
        return receiptsToInvoices(receipts);
    }

    private List<SalesInvoice> receiptsToInvoices(List<EtsyReceipt> receipts) {
        List<SalesInvoice> invoices = new ArrayList<>();
        for (EtsyReceipt receipt : receipts) {
            SalesInvoice invoiceFromReceipt = receiptToInvoice(receipt);
            SalesInvoice createdInvoice = invoiceService
                    .createInvoice(invoiceFromReceipt)
                    .block();
            invoices.add(createdInvoice);
        }
        return invoices;
    }

    private SalesInvoice receiptToInvoice(EtsyReceipt receipt) {
        SalesInvoice invoice = new SalesInvoice();

        setContactIdForInvoice(invoice, receipt);

        Long createTimestamp = receipt.getCreateTimestamp();
        String createDate = EtsyReceipt.timestampToIsoDate(createTimestamp);
        invoice.setInvoiceDate(createDate);

        String currencyCode = receipt.getTotalPrice().getCurrencyCode();
        invoice.setCurrency(currencyCode);

        setDiscountForInvoice(invoice, receipt);

        invoice.setDetailsAttributes(detailsAttributesFromReceipt(receipt));


        return invoice;
    }

    private void setContactIdForInvoice(SalesInvoice invoice,
                                        EtsyReceipt receipt) {
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
    }

    // TODO: discuss with Peter if discount is set correctly
    private void setDiscountForInvoice(SalesInvoice invoice,
                                       EtsyReceipt receipt) {
        double discount = receipt.getDiscountAmt().getAmount();
        double subtotal = receipt.getSubtotal().getAmount();
        double percentDiscount = discount / subtotal * 100;

        DecimalFormat df = new DecimalFormat("#.##");
        String formattedStringPercentDiscount = df.format(percentDiscount);
        Double formattedPercentDiscount = Double
                .valueOf(formattedStringPercentDiscount);

        invoice.setDiscount(formattedPercentDiscount);
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

        // Decide if we'll set a tax_rate_id.
        // Add the rest of the fields and create a contact with the Service

        return contact;
    }

    // Create a list of Invoice Details Attributes according to receipt's transactions
    private List<SalesInvoice.DetailsAttributes> detailsAttributesFromReceipt(EtsyReceipt receipt) {

        // Note: apparently, Moneybird's "DetailAttributes" is
        // (almost) the same thing as Etsy's "Transactions"

        ArrayList<SalesInvoice.DetailsAttributes> attributes
                = new ArrayList<>();
        MoneybirdTaxRate taxRate = getTaxRate(receipt);

        for (EtsyTransaction transaction : receipt.getTransactions()) {
            SalesInvoice.DetailsAttributes attr =
                    new SalesInvoice.DetailsAttributes();
            assert taxRate != null;
            attr.setTaxRateId(new BigInteger(taxRate.getId()));
            attr.setDescription(transaction.getTitle());
            attr.setAmount(transaction.getQuantity().toString());
            attr.setPrice((double) transaction.getPrice().getAmount()
                    / transaction.getPrice().getDivisor());

            // TODO: verify that the following is correct
            // The specified period is from Payment Date to Shipment Date
            // The following can be incorrect as these timestamps may be null
            /*Long paidTimestamp = transaction.getPaidTimestamp();
            String paidDate = EtsyReceipt
                    .timestampToIsoDate(paidTimestamp)
                    .replace("-", "");

            Long shippedTimestamp = transaction.getShippedTimestamp();
            String shippedDate = EtsyReceipt
                    .timestampToIsoDate(shippedTimestamp)
                    .replace("-", "");

            attr.setPeriod(paidDate + ".." + shippedDate);*/

            attributes.add(attr);
        }



        return attributes;
    }

    private MoneybirdTaxRate getTaxRate(EtsyReceipt receipt) {
        Iterable<MoneybirdTaxRate> taxRates = taxRatesService.getAllTaxRates().toIterable();

        double etsyTaxPercent = (double) receipt.getTotalTaxCost().getAmount() / receipt.getSubtotal().getAmount();

        for (MoneybirdTaxRate rate : taxRates) {
            if (Double.parseDouble(rate.getPercentage()) == etsyTaxPercent)
                return rate;
        }

        return null;
    }
}
