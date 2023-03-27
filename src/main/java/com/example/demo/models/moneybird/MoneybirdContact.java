package com.example.demo.models.moneybird;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigInteger;
import java.util.Optional;

@Getter
@Setter
@ToString
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MoneybirdContact {
    private BigInteger id;
    private String companyName;
    private String address1;
    private String address2;
    private String zipcode;
    private String city;
    private String country;
    private String phone;
    private String deliveryMethod;
    private String customerId;
    private String taxNumber;
    private String firstname;
    private String lastname;
    private String chamberOfCommerce;
    private String bankAccount;
    private String sendInvoicesToAttention;
    private String sendInvoicesToEmail;
    private String sendEstimatesToAttention;
    private String sendEstimatesToEmail;
    private Boolean sepaActive;
    private String sepaIban;
    private String sepaIbanAccountName;
    private String sepaBic;
    private String sepaMandateId;
    private String sepaMandateDate;
    private String sepaSequenceType;
    private String siIdentifierType;
    private String siIdentifier;
    private Integer invoiceWorkflowId;
    private Integer estimateWorkflowId;
    private Boolean emailUbl;
    private Boolean directDebit;
    // some fields are not added


    // decide if we need these methods (most likely not as etsy
    // provides only a company name, not name with surname)
    /*public String getFullName() {
        if (firstname == null || lastname == null)
            return null;
        return firstname + lastname;
    }*/

    /*public Optional<String> getOptionalFullName() {
        return Optional.ofNullable(getFullName());
    }*/

    public Optional<String> getOptionalCompanyName() {
        return Optional.ofNullable(companyName);
    }
}
