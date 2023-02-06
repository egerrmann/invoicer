package com.example.demo.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;

@Getter
@Setter
@ToString
public class SalesInvoice {
    private int salesId;
    private int contactPersonId;
    private int originalEstimateId;
    private int documentStyleId;
    private int workflowId;
    private String reference;
    private String invoiceSequenceId;
    private String invoiceDate;
    private int firstDueInterval;
    private String currency;
    private boolean piecesAreInclTax;
    private String paymentConditions;
    private double discount;
    // ???
    private boolean fromCheckpoint;

    @Getter
    @Setter
    @ToString
    class DetailsAttributes {
        private int id;
        private String description;
        private String period;
        private double price;
        private String amount;
        private int taxRateId;
        private int ledgerAccountId;
        private int projectId;
        private int productId;
        // type?
        private ArrayList timeEntryIds;
        private int rowOrder;
        private boolean destroy;
        private boolean automatedTaxEnabled;
    }

    @Getter
    @Setter
    @ToString
    class CustomFieldsAttributes {
        private int id;
        private String value;
    }
}
