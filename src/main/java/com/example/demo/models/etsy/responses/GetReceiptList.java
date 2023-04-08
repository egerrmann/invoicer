package com.example.demo.models.etsy.responses;

import com.example.demo.models.etsy.EtsyReceipt;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GetReceiptList {
    private int count;
    private List<EtsyReceipt> results;
}
