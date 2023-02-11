package com.example.demo.models;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SalesInvoice {
    private BigInteger contactId;
    private Integer contactPersonId;
    private Integer originalEstimateId;
    private Integer documentStyleId;
    private Integer workflowId;
    private String reference;
    private String invoiceSequenceId;
    private String invoiceDate;
    private Integer firstDueInterval;
    private String currency;
    private Boolean piecesAreInclTax;
    private String paymentConditions;
    private Double discount;
    // ???
    private Boolean fromCheckpoint;
    private List<DetailsAttributes> detailsAttributes = new ArrayList<>();
    private List<CustomFieldsAttributes> customFieldsAttributes = new ArrayList<>();

    @Getter
    @Setter
    @ToString
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public class DetailsAttributes {
        private Integer id;
        private String description;
        private String period;
        private Double price;
        private String amount;
        private Integer taxRateId;
        private Integer ledgerAccountId;
        private Integer projectId;
        private Integer productId;
        // type?
        private List timeEntryIds = new ArrayList();
        private Integer rowOrder;
        private Boolean destroy;
        private Boolean automatedTaxEnabled;
    }

    @Getter
    @Setter
    @ToString
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public class CustomFieldsAttributes {
        private Integer id;
        private String value;
    }
}
