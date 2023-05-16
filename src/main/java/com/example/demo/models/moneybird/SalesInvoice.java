package com.example.demo.models.moneybird;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SalesInvoice {
    private String id;
    private BigInteger contactId;
    private Integer contactPersonId;
    private Integer originalEstimateId;
    private BigInteger documentStyleId;
    private BigInteger workflowId;
    private String reference;
    private String invoiceSequenceId;
    private String invoiceDate;
    private Integer firstDueInterval;

    @JsonProperty("due_date")
    public Integer getFirstDueInterval() {
        return firstDueInterval;
    }

    @JsonProperty("first_due_interval")
    public void setFirstDueInterval(Integer firstDueInterval) {
        this.firstDueInterval = firstDueInterval;
    }

    private String currency;
    private Boolean pricesAreInclTax;
    private String paymentConditions;
    private Double discount;
    // ??? (maybe we should delete this field)
    private Boolean fromCheckout;

    private List<DetailsAttributes> detailsAttributes = new ArrayList<>();

    @JsonProperty("details_attributes")
    public List<DetailsAttributes> getDetailsAttributes() {
        return detailsAttributes;
    }

    @JsonProperty("details")
    public void setDetailsAttributes(List<DetailsAttributes> detailsAttributes) {
        this.detailsAttributes = detailsAttributes;
    }

    private List<CustomFieldsAttributes> customFieldsAttributes = new ArrayList<>();

    @JsonProperty("custom_fields_attributes")
    public List<CustomFieldsAttributes> getCustomFieldsAttributes() {
        return customFieldsAttributes;
    }

    @JsonProperty("custom_fields")
    public void setCustomFieldsAttributes(List<CustomFieldsAttributes> customFieldsAttributes) {
        this.customFieldsAttributes = customFieldsAttributes;
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class DetailsAttributes {
        private BigInteger id;
        private String description;
        private String period;
        private Double price;
        private String amount;
        private BigInteger taxRateId;
        private BigInteger ledgerAccountId;
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
    @NoArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class CustomFieldsAttributes {
        private BigInteger id;
        private String value;
    }
}
