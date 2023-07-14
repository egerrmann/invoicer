package com.example.demo.services;

import com.example.demo.models.etsy.EtsyPrice;
import com.example.demo.models.etsy.EtsyReceipt;
import com.example.demo.models.etsy.EtsyTransaction;
import com.example.demo.models.moneybird.MoneybirdContact;
import com.example.demo.models.moneybird.MoneybirdTaxRate;
import com.example.demo.models.moneybird.SalesInvoice;
import com.example.demo.services.interfaces.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoicerService implements IInvoicerService {
    private final IEtsyService etsyService;
    private final IInvoicerContactService invoicerContactService;
    private final IMoneybirdInvoiceService invoiceService;
    private final IMoneybirdTaxRatesService taxRatesService;
    private final IMoneybirdLedgerAccountService ledgerAccountService;

    @Override
    public List<SalesInvoice> createInvoices(String startDate, String endDate) {
        invoicerContactService.updateContactTable();
        List<EtsyReceipt> receipts = etsyService.getReceiptsList(startDate, endDate);
        return receiptsToInvoices(receipts);
    }

    private List<SalesInvoice> receiptsToInvoices(List<EtsyReceipt> receipts) {
        List<SalesInvoice> invoices = new ArrayList<>();
        for (EtsyReceipt receipt : receipts) {
            SalesInvoice invoiceFromReceipt = receiptToInvoice(receipt, false);
            invoices.add(createAndSendInvoice(invoiceFromReceipt));
            if (!receipt.getRefunds().isEmpty()) {
                invoiceFromReceipt = receiptToInvoice(receipt, true);
                invoices.add(createAndSendInvoice(invoiceFromReceipt));
            }
        }
        return invoices;
    }

    private SalesInvoice createAndSendInvoice(SalesInvoice invoiceFromReceipt) {
        SalesInvoice createdInvoice = invoiceService
                .createInvoice(invoiceFromReceipt)
                .block();
        return invoiceService
                .sendInvoice(createdInvoice.getId())
                .block();
    }

    private SalesInvoice receiptToInvoice(EtsyReceipt receipt, boolean isWithRefund) {
        SalesInvoice invoice = new SalesInvoice();

        invoicerContactService.setContactIdForInvoice(invoice, receipt);
        invoice.setReference(receipt.getReceiptId().toString());

        Long createTimestamp = receipt.getCreateTimestamp();
        String createDate = timestampToIsoDate(createTimestamp);
        invoice.setInvoiceDate(createDate);

        String currencyCode = receipt.getTotalPrice().getCurrencyCode();
        invoice.setCurrency(currencyCode);
        invoice.setPricesAreInclTax(true);

        //setDiscountForInvoice(invoice, receipt);

        invoice.setDetailsAttributes(detailsAttributesFromReceipt(receipt, isWithRefund));


        return invoice;
    }

    // Create a list of Invoice Details Attributes according to receipt's transactions
    private List<SalesInvoice.DetailsAttributes> detailsAttributesFromReceipt(
            EtsyReceipt receipt,
            boolean isWithRefund) {

        ArrayList<SalesInvoice.DetailsAttributes> attributes
                = new ArrayList<>();


        String countryIso = receipt.getCountryIso();
        MoneybirdTaxRate taxRate = taxRatesService.getMaxCountryTax(countryIso);
        String moneybirdLedgerId = ledgerAccountService.getLedgerId(countryIso);

        if (isWithRefund) {
            // add a refund cost attribute
            SalesInvoice.DetailsAttributes refund =
                    getRefundedCostAttribute(receipt, taxRate, moneybirdLedgerId);
            attributes.add(refund);
            return attributes;
        }

        for (EtsyTransaction transaction : receipt.getTransactions()) {
            SalesInvoice.DetailsAttributes attr
                    = new SalesInvoice.DetailsAttributes();

            if (taxRate != null)
                attr.setTaxRateId(Long.parseLong(taxRate.getId()));

            attr.setDescription(transaction.getTitle());
            attr.setAmount(transaction.getQuantity().toString());
            attr.setPrice((double) transaction.getPrice().getAmount()
                    / transaction.getPrice().getDivisor());

            attr.setLedgerAccountId(Long.parseLong(moneybirdLedgerId));

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

        // add a delivery attribute
        attributes.add(getDeliveryAttribute(receipt, taxRate, moneybirdLedgerId));

        // add a discount attribute if discount is applied by etsy
        SalesInvoice.DetailsAttributes discount
                = getDiscountAttribute(receipt, taxRate, moneybirdLedgerId);
        if (discount.getPrice() != 0)
            attributes.add(discount);


        return attributes;
    }

    private SalesInvoice.DetailsAttributes getDeliveryAttribute(
            EtsyReceipt receipt,
            MoneybirdTaxRate taxRate,
            String ledgerId) {

        SalesInvoice.DetailsAttributes deliveryAttr
                = new SalesInvoice.DetailsAttributes();

        deliveryAttr.setDescription("Delivery");
        deliveryAttr.setTaxRateId(Long.parseLong(taxRate.getId()));

        EtsyPrice shippingCost = receipt.getTotalShippingCost();
        deliveryAttr.setPrice((double) shippingCost.getAmount()
                / shippingCost.getDivisor());

        deliveryAttr.setLedgerAccountId(Long.parseLong(ledgerId));

        return deliveryAttr;
    }

    private SalesInvoice.DetailsAttributes getDiscountAttribute(
            EtsyReceipt receipt,
            MoneybirdTaxRate taxRate,
            String ledgerId) {

        SalesInvoice.DetailsAttributes discountAttr
                = new SalesInvoice.DetailsAttributes();

        discountAttr.setDescription("Discount");
        discountAttr.setTaxRateId(Long.parseLong(taxRate.getId()));

        EtsyPrice discount = receipt.getDiscountAmt();
        discountAttr.setPrice(-1. * discount.getAmount()
                / discount.getDivisor());

        discountAttr.setLedgerAccountId(Long.parseLong(ledgerId));

        return discountAttr;
    }

    private SalesInvoice.DetailsAttributes getRefundedCostAttribute(
            EtsyReceipt receipt,
            MoneybirdTaxRate taxRate,
            String ledgerId) {

        SalesInvoice.DetailsAttributes refundAttr
                = new SalesInvoice.DetailsAttributes();

        refundAttr.setDescription("Refunded cost");
        refundAttr.setTaxRateId(Long.parseLong(taxRate.getId()));

        List<EtsyReceipt.Refund> refunds = receipt.getRefunds();
        double totalRefunds = refunds.stream()
                .map(EtsyReceipt.Refund::getAmount)
                .map(o -> (double) o.getAmount() / o.getDivisor())
                .reduce(0., Double::sum);
        refundAttr.setPrice(-totalRefunds);

        refundAttr.setLedgerAccountId(Long.parseLong(ledgerId));

        return refundAttr;
    }

    // TODO Delete this method
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
