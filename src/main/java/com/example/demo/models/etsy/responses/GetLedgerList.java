package com.example.demo.models.etsy.responses;

import com.example.demo.models.etsy.EtsyLedger;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GetLedgerList {
    private int count;
    private List<EtsyLedger> results;
}
