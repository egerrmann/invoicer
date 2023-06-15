package com.example.demo.models.moneybird;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MoneybirdTaxRate {
    private String id;
    private Long administrationId;
    private String name;
    private String percentage;
    private String taxRateType;
    private String country;
    private Boolean showTax;
    private Boolean active;
    private String createdAt;
    private String updatedAt;
}
