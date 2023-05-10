package com.example.demo.services;

import com.example.demo.models.etsy.EtsyTransaction;
import com.example.demo.models.exceptions.IncorrectDataException;
import com.example.demo.models.moneybird.MoneybirdContact;
import com.example.demo.models.moneybird.MoneybirdTaxRate;
import com.example.demo.models.moneybird.SalesInvoice;
import com.example.demo.models.etsy.EtsyReceipt;
import com.example.demo.services.interfaces.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
            System.out.println(createdInvoice.getInvoiceDate());
            invoices.add(createdInvoice);
        }
        return invoices;
    }

    private SalesInvoice receiptToInvoice(EtsyReceipt receipt) {
        SalesInvoice invoice = new SalesInvoice();

        setContactIdForInvoice(invoice, receipt);

        Long createTimestamp = receipt.getCreateTimestamp();
        String createDate = timestampToIsoDate(createTimestamp);
        invoice.setInvoiceDate(createDate);

        String currencyCode = receipt.getTotalPrice().getCurrencyCode();
        invoice.setCurrency(currencyCode);
        invoice.setPiecesAreInclTax(true);

        setDiscountForInvoice(invoice, receipt);

        invoice.setDetailsAttributes(detailsAttributesFromReceipt(receipt));


        return invoice;
    }

    private void setContactIdForInvoice(SalesInvoice invoice,
                                        EtsyReceipt receipt) {
        MoneybirdContact contact = contactFromReceipt(receipt);
        String contactId = contactService.getContactId(contact);
        if (contactId != null && !contactId.equals("")) {
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
        double total = receipt.getTotalPrice().getAmount();
        double percentDiscount = discount * 100 / total;

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

        ArrayList<SalesInvoice.DetailsAttributes> attributes
                = new ArrayList<>();
        MoneybirdTaxRate taxRate = getMaxCountryTax(receipt.getCountryIso());
//        MoneybirdTaxRate taxRate = getMaxTaxRate(receipt);

        for (EtsyTransaction transaction : receipt.getTransactions()) {
            SalesInvoice.DetailsAttributes attr =
                    new SalesInvoice.DetailsAttributes();

            if (taxRate != null)
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

    // TODO we may consider getting TaxRates from MB only once
    //  to make interactions with MB as little as possible
    // Gets the largest tax rate
    private MoneybirdTaxRate getMaxCountryTax(String countryIso) {
        String shopIso = etsyService.getShopIso();
        if (countryIso != null) {
            // Getting a Max TaxRate for the specified country
            Iterable<MoneybirdTaxRate> taxRates = taxRatesService
                    .getAllTaxRates(countryIso)
                    .toIterable();
            if (taxRates.iterator().hasNext()) {
                // If there are any tax rates for the specified country,
                // this method is invoked and the isCountryKnown parameter set to "true"
                return getMaxTaxRate(taxRates, true);
            } else if (!countryIso.equals(shopIso)){
                // TODO check if the logic in the comment is right or maybe we should do something else, for example:
                //  1. We could force users to create tax rates for their home-countries as well (then make sure that it is possible from the Moneybird side)
                //  2. Or maybe this approach is incorrect, because the MB doesn't specify the 'country' for tax rates if the tax rate is from Netherlands? (Verify that as well)
                // This 'else if' makes sure the error is not thrown, when the specified country is the shop's country.
                // This allows in this case the function to continue and use "basic" tax rates for the home-country.
                // By "basic" tax rates we mean the tax rates with no specified country.
                throw new IncorrectDataException("The country with ISO '%s' doesn't exist in the Moneybird account".formatted(countryIso), HttpStatus.BAD_REQUEST);
            }
        }

        // Getting Max TaxRate for the country where the shop is located
        // or for the case if the country is not specified.
        // TODO check what to do in case the customer's country (from Etsy receipt) is not specified:
        //  1. Should we take the TaxRate from the country of the shop? (which happens right now)
        //  2. Or should we throw an exception?

        Iterable<MoneybirdTaxRate> taxRates = taxRatesService
                .getAllTaxRates()
                .toIterable();
        return getMaxTaxRate(taxRates, false);
    }

    // Gets a max TaxRate form provided 'taxRates'.
    // If 'isCountryKnown' is 'false' then the function returns a max standard TaxRate.
    private MoneybirdTaxRate getMaxTaxRate(Iterable<MoneybirdTaxRate> taxRates, boolean isCountryKnown) {
        double ratePercentage = 0;
        MoneybirdTaxRate maxRate = null;

        for (MoneybirdTaxRate rate : taxRates) {
            if (!isCountryKnown && rate.getCountry() != null) {
                continue;
            }
            double currentRatePerc = Double.parseDouble(rate.getPercentage());
            if (ratePercentage < currentRatePerc) {
                ratePercentage = currentRatePerc;
                maxRate = rate;
            }
        }
        return maxRate;
    }

    // Calculates the TaxRate from Etsy, finds the same TaxRate in MB,
    // and returns it.
    private MoneybirdTaxRate getEtsyTaxRate(EtsyReceipt receipt) {
        Iterable<MoneybirdTaxRate> taxRates = taxRatesService
                .getAllTaxRates(receipt.getCountryIso())
                .toIterable();

        double tax = receipt.getTotalTaxCost().getAmount();
        double subtotal = receipt.getSubtotal().getAmount();
        double taxPercent = tax * 100 / subtotal;

        DecimalFormat df = new DecimalFormat("#.##");
        String formattedStringTax = df.format(taxPercent);
        double formattedTax = Double.parseDouble(formattedStringTax);

        for (MoneybirdTaxRate rate : taxRates) {
            if (Double.parseDouble(rate.getPercentage()) == formattedTax)
                return rate;
        }

        return null;
    }

    private String timestampToIsoDate(Long timestamp) {
        return LocalDate.ofEpochDay(timestamp / 86400L)
                .format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
